package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.BookReference;
import org.takoyaki.reportmaker.model.EquationBlock;
import org.takoyaki.reportmaker.model.Equipment;
import org.takoyaki.reportmaker.model.FigureBlock;
import org.takoyaki.reportmaker.model.Reference;
import org.takoyaki.reportmaker.model.ReportBlock;
import org.takoyaki.reportmaker.model.ReportDocument;
import org.takoyaki.reportmaker.model.ReportEquation;
import org.takoyaki.reportmaker.model.ReportFigure;
import org.takoyaki.reportmaker.model.ReportSection;
import org.takoyaki.reportmaker.model.SubsectionBlock;
import org.takoyaki.reportmaker.model.TextBlock;
import org.takoyaki.reportmaker.model.WebReference;
import org.takoyaki.reportmaker.i18n.I18n;

import java.nio.file.Path;
import java.util.Optional;
import java.util.StringJoiner;

public final class MarkdownRenderer implements MarkdownGenerator {
    private final NumberingPolicy numberingService;

    public MarkdownRenderer(NumberingPolicy numberingService) {
        this.numberingService = numberingService;
    }

    @Override
    public String render(ReportDocument document) {
        StringBuilder markdown = new StringBuilder();
        markdown.append(MarkdownConstants.TITLE_PREFIX).append(document.getTitle().strip()).append("\n\n");
        if (!document.getEnglishTitle().isBlank()) {
            markdown.append("**").append(document.getEnglishTitle().strip()).append("**\n\n");
        }
        document.getPreparationSections().forEach(section -> appendSection(markdown, document, section));
        appendEquipment(markdown, document);
        document.getMainSections().forEach(section -> appendSection(markdown, document, section));
        appendReferences(markdown, document);
        return markdown.toString();
    }

    public String figureFileName(ReportFigure figure) {
        String extension = extensionOf(figure.getImagePath());
        return "figure-%03d%s".formatted(figure.getNumber(), extension);
    }

    private void appendSection(StringBuilder markdown, ReportDocument document, ReportSection section) {
        markdown.append(MarkdownConstants.SECTION_PREFIX).append(section.getTitle()).append("\n\n");
        int subsectionIndex = 0;
        for (ReportBlock block : section.getBlocks()) {
            if (block instanceof TextBlock text) {
                markdown.append(text.getText().strip()).append("\n\n");
            } else if (block instanceof SubsectionBlock subsection) {
                subsectionIndex++;
                String number = subsection.getImportedNumber().isBlank()
                        ? numberingService.subsectionNumber(section, subsectionIndex)
                        : subsection.getImportedNumber();
                markdown.append(MarkdownConstants.SUBSECTION_PREFIX).append(number).append(' ')
                        .append(subsection.getTitle().strip()).append("\n\n");
            } else if (block instanceof FigureBlock figureBlock) {
                findFigure(document, figureBlock).ifPresent(figure -> appendFigure(markdown, figure));
            } else if (block instanceof EquationBlock equationBlock) {
                findEquation(document, equationBlock).ifPresent(equation -> appendEquation(markdown, equation));
            }
        }
    }

    private void appendEquipment(StringBuilder markdown, ReportDocument document) {
        markdown.append(MarkdownConstants.SECTION_PREFIX).append(I18n.text("section.equipment")).append("\n\n");
        markdown.append("| ").append(String.join(" | ", EquipmentColumns.headers())).append(" |\n");
        markdown.append("| ").append("--- | ".repeat(EquipmentColumns.headers().size())).append("\n");
        for (Equipment equipment : document.getEquipment()) {
            StringJoiner row = new StringJoiner(" | ", "| ", " |\n");
            row.add(escapeCell(equipment.getName())).add(escapeCell(equipment.getRating()))
                    .add(escapeCell(equipment.getEquipmentClass())).add(escapeCell(equipment.getManufacturer()))
                    .add(escapeCell(equipment.getModel())).add(escapeCell(equipment.getSerialNumber()))
                    .add(escapeCell(equipment.getAssetNumber()));
            markdown.append(row);
        }
        markdown.append('\n');
    }

    private void appendFigure(StringBuilder markdown, ReportFigure figure) {
        String label = I18n.text("display.figure", figure.getNumber(), figure.getName());
        markdown.append("![").append(label).append("](")
                .append(MarkdownConstants.IMAGES_DIRECTORY).append('/').append(figureFileName(figure))
                .append(")\n\n").append(label).append("\n\n");
    }

    private void appendEquation(StringBuilder markdown, ReportEquation equation) {
        markdown.append(MarkdownConstants.EQUATION_DELIMITER).append('\n')
                .append(equation.getLatex().strip()).append('\n')
                .append("\\tag{").append(equation.getNumber()).append("}\n")
                .append(MarkdownConstants.EQUATION_DELIMITER).append("\n\n");
        if (!equation.getName().isBlank()) {
            markdown.append(I18n.text("display.equation", equation.getNumber(), equation.getName().strip()))
                    .append("\n\n");
        }
    }

    private void appendReferences(StringBuilder markdown, ReportDocument document) {
        markdown.append(MarkdownConstants.SECTION_PREFIX).append(I18n.text("section.references")).append("\n\n");
        for (int index = 0; index < document.getReferences().size(); index++) {
            Reference reference = document.getReferences().get(index);
            markdown.append(index + 1).append(". ");
            if (reference instanceof WebReference web) {
                markdown.append(web.getAuthor()).append(": \"").append(web.getPageTitle()).append("\"\n   ")
                        .append(web.getUrl()).append("\n   (").append(web.getAccessedDate()).append(")\n\n");
            } else if (reference instanceof BookReference book) {
                markdown.append(book.getAuthor()).append(": \"").append(book.getTitle()).append("\", ")
                        .append(book.getPublisher()).append(" (").append(book.getPublicationYear()).append(")\n\n");
            }
        }
    }

    private Optional<ReportFigure> findFigure(ReportDocument document, FigureBlock block) {
        return document.getFigures().stream().filter(figure -> figure.getId().equals(block.getFigureId())).findFirst();
    }

    private Optional<ReportEquation> findEquation(ReportDocument document, EquationBlock block) {
        return document.getEquations().stream().filter(equation -> equation.getId().equals(block.getEquationId())).findFirst();
    }

    private String escapeCell(String value) {
        return value.replace("|", "\\|").replace("\n", "<br>");
    }

    private String extensionOf(String path) {
        String fileName = Path.of(path).getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(dot).toLowerCase() : ".png";
    }
}
