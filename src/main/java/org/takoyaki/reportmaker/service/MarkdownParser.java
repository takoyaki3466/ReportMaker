package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.BookReference;
import org.takoyaki.reportmaker.model.EquationBlock;
import org.takoyaki.reportmaker.model.Equipment;
import org.takoyaki.reportmaker.model.FigureBlock;
import org.takoyaki.reportmaker.model.ReportDocument;
import org.takoyaki.reportmaker.model.ReportEquation;
import org.takoyaki.reportmaker.model.ReportFigure;
import org.takoyaki.reportmaker.model.ReportSection;
import org.takoyaki.reportmaker.model.SectionType;
import org.takoyaki.reportmaker.model.SubsectionBlock;
import org.takoyaki.reportmaker.model.TextBlock;
import org.takoyaki.reportmaker.model.WebReference;
import org.takoyaki.reportmaker.i18n.I18n;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MarkdownParser implements MarkdownImporter {
    private static final Pattern SUBSECTION_PATTERN = Pattern.compile("(?:(\\d+(?:\\.\\d+)*)\\s+)?(.*)");
    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[(?:"
            + Pattern.quote(I18n.text("block.figure")) + "(\\d+)\\s*)?(.*)]\\((.*)\\)");
    private static final Pattern TAG_PATTERN = Pattern.compile("\\\\tag\\{(\\d+)}");
    private static final Pattern BOOK_PATTERN = Pattern.compile("(.+): \\\"(.+)\\\", (.+) \\(([^)]+)\\)");

    private final NumberingPolicy numberingService;

    public MarkdownParser(NumberingPolicy numberingService) {
        this.numberingService = numberingService;
    }

    @Override
    public ReportDocument parse(String markdown, Path sourceDirectory) {
        ReportDocument document = emptyDocument();
        List<String> lines = markdown.lines().toList();
        ReportSection currentSection = null;
        StringBuilder textBuffer = new StringBuilder();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.startsWith(MarkdownConstants.TITLE_PREFIX) && !line.startsWith(MarkdownConstants.SECTION_PREFIX)) {
                document.setTitle(line.substring(MarkdownConstants.TITLE_PREFIX.length()).strip());
            } else if (line.startsWith("**") && line.endsWith("**") && document.getEnglishTitle().isBlank()) {
                document.setEnglishTitle(line.substring(2, line.length() - 2).strip());
            } else if (line.startsWith(MarkdownConstants.SECTION_PREFIX)
                    && !line.startsWith(MarkdownConstants.SUBSECTION_PREFIX)) {
                flushText(currentSection, textBuffer);
                String title = line.substring(MarkdownConstants.SECTION_PREFIX.length()).strip();
                currentSection = createSection(document, title);
            } else if (line.startsWith(MarkdownConstants.SUBSECTION_PREFIX) && currentSection != null) {
                flushText(currentSection, textBuffer);
                addSubsection(currentSection, line.substring(MarkdownConstants.SUBSECTION_PREFIX.length()).strip());
            } else if (line.equals(MarkdownConstants.EQUATION_DELIMITER) && currentSection != null) {
                flushText(currentSection, textBuffer);
                index = parseEquation(lines, index + 1, document, currentSection);
            } else if (line.startsWith("![") && currentSection != null) {
                flushText(currentSection, textBuffer);
                parseFigure(line, sourceDirectory, document, currentSection);
            } else if (currentSection != null && currentSection.getType() == SectionType.EQUIPMENT && line.startsWith("|")) {
                index = parseEquipment(lines, index, document);
            } else if (currentSection != null && I18n.text("section.references").equals(currentSection.getTitle())) {
                index = parseReference(lines, index, document);
            } else if (!isGeneratedCaption(line)) {
                if (!line.isBlank() || textBuffer.length() > 0) {
                    textBuffer.append(line).append('\n');
                }
            }
        }
        flushText(currentSection, textBuffer);
        ensureStandardSections(document);
        return document;
    }

    private ReportDocument emptyDocument() {
        ReportDocument document = new ReportDocument();
        document.getPreparationSections().clear();
        document.getMainSections().clear();
        return document;
    }

    private ReportSection createSection(ReportDocument document, String title) {
        SectionType type = numberingService.typeForTitle(title);
        ReportSection section = new ReportSection(type, title);
        if (type == SectionType.EQUIPMENT || I18n.text("section.references").equals(title)) {
            return section;
        }
        if (type.getCategory() == org.takoyaki.reportmaker.model.ReportCategory.PREPARATION) {
            document.getPreparationSections().add(section);
        } else {
            document.getMainSections().add(section);
        }
        return section;
    }

    private void addSubsection(ReportSection section, String heading) {
        Matcher matcher = SUBSECTION_PATTERN.matcher(heading);
        if (matcher.matches()) {
            section.getBlocks().add(new SubsectionBlock(matcher.group(2), matcher.group(1) == null ? "" : matcher.group(1)));
        }
    }

    private int parseEquation(List<String> lines, int start, ReportDocument document, ReportSection section) {
        StringBuilder latex = new StringBuilder();
        int number = numberingService.nextEquationNumber(document);
        int index = start;
        while (index < lines.size() && !lines.get(index).equals(MarkdownConstants.EQUATION_DELIMITER)) {
            Matcher tag = TAG_PATTERN.matcher(lines.get(index));
            if (tag.matches()) {
                number = Integer.parseInt(tag.group(1));
            } else {
                latex.append(lines.get(index)).append('\n');
            }
            index++;
        }
        String name = "";
        if (index + 1 < lines.size()) {
            Matcher caption = Pattern.compile(Pattern.quote(I18n.text("block.equation"))
                    + "\\(" + number + "\\)\\s*(.*)").matcher(lines.get(index + 1));
            if (caption.matches()) {
                name = caption.group(1).strip();
            }
        }
        ReportEquation equation = new ReportEquation(UUID.randomUUID(), number, name, latex.toString().strip());
        document.getEquations().add(equation);
        section.getBlocks().add(new EquationBlock(equation.getId()));
        return index;
    }

    private void parseFigure(String line, Path sourceDirectory, ReportDocument document, ReportSection section) {
        Matcher matcher = IMAGE_PATTERN.matcher(line);
        if (!matcher.matches()) {
            section.getBlocks().add(new TextBlock(line));
            return;
        }
        int number = matcher.group(1) == null ? numberingService.nextFigureNumber(document)
                : Integer.parseInt(matcher.group(1));
        Path path = sourceDirectory.resolve(matcher.group(3)).normalize();
        ReportFigure figure = new ReportFigure(UUID.randomUUID(), number, matcher.group(2).strip(), path);
        document.getFigures().add(figure);
        section.getBlocks().add(new FigureBlock(figure.getId()));
    }

    private int parseEquipment(List<String> lines, int start, ReportDocument document) {
        int index = start;
        while (index < lines.size() && lines.get(index).startsWith("|")) {
            String line = lines.get(index);
            if (index > start + 1) {
                List<String> cells = tableCells(line);
                if (cells.size() >= EquipmentColumns.headers().size()) {
                    Equipment item = new Equipment();
                    item.setName(cells.get(0)); item.setRating(cells.get(1));
                    item.setEquipmentClass(cells.get(2)); item.setManufacturer(cells.get(3));
                    item.setModel(cells.get(4)); item.setSerialNumber(cells.get(5)); item.setAssetNumber(cells.get(6));
                    document.getEquipment().add(item);
                }
            }
            index++;
        }
        return index - 1;
    }

    private List<String> tableCells(String line) {
        String content = line.substring(1, line.length() - 1);
        String[] raw = content.split("(?<!\\\\)\\|");
        List<String> cells = new ArrayList<>();
        for (String cell : raw) {
            cells.add(cell.strip().replace("\\|", "|").replace("<br>", "\n"));
        }
        return cells;
    }

    private int parseReference(List<String> lines, int index, ReportDocument document) {
        String line = lines.get(index);
        Matcher numbered = Pattern.compile("\\d+\\.\\s+(.+)").matcher(line);
        if (!numbered.matches()) {
            return index;
        }
        String first = numbered.group(1);
        Matcher book = BOOK_PATTERN.matcher(first);
        if (book.matches()) {
            document.getReferences().add(new BookReference(book.group(1), book.group(2), book.group(3), book.group(4)));
            return index;
        }
        Matcher webTitle = Pattern.compile("(.+): \\\"(.+)\\\"").matcher(first);
        if (webTitle.matches() && index + 2 < lines.size()) {
            String url = lines.get(index + 1).strip();
            String dateLine = lines.get(index + 2).strip();
            String date = dateLine.startsWith("(") && dateLine.endsWith(")")
                    ? dateLine.substring(1, dateLine.length() - 1) : dateLine;
            document.getReferences().add(new WebReference(webTitle.group(1), webTitle.group(2), url, date));
            return index + 2;
        }
        return index;
    }

    private void flushText(ReportSection section, StringBuilder text) {
        String value = text.toString().strip();
        if (section != null && !value.isBlank() && section.getType() != SectionType.EQUIPMENT
                && !I18n.text("section.references").equals(section.getTitle())) {
            section.getBlocks().add(new TextBlock(value));
        }
        text.setLength(0);
    }

    private void ensureStandardSections(ReportDocument document) {
        for (SectionType type : List.of(SectionType.PURPOSE, SectionType.PRINCIPLE,
                SectionType.METHOD, SectionType.PRE_ASSIGNMENT)) {
            if (document.getPreparationSections().stream().noneMatch(section -> section.getType() == type)) {
                document.getPreparationSections().add(new ReportSection(type));
            }
        }
        for (SectionType type : List.of(SectionType.RESULT, SectionType.DISCUSSION_SUMMARY)) {
            if (document.getMainSections().stream().noneMatch(section -> section.getType() == type)) {
                document.getMainSections().add(new ReportSection(type));
            }
        }
    }

    private boolean isGeneratedCaption(String line) {
        String figure = Pattern.quote(I18n.text("block.figure"));
        String equation = Pattern.quote(I18n.text("block.equation"));
        return line.matches(figure + "\\d+\\s+.*")
                || line.matches(equation + "\\(\\d+\\)\\s+.*");
    }
}
