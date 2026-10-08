package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.BookReference;
import org.takoyaki.reportmaker.model.EquationBlock;
import org.takoyaki.reportmaker.model.Equipment;
import org.takoyaki.reportmaker.model.FigureBlock;
import org.takoyaki.reportmaker.model.Reference;
import org.takoyaki.reportmaker.model.ReferenceType;
import org.takoyaki.reportmaker.model.ReportBlock;
import org.takoyaki.reportmaker.model.ReportDocument;
import org.takoyaki.reportmaker.model.ReportEquation;
import org.takoyaki.reportmaker.model.ReportFigure;
import org.takoyaki.reportmaker.model.ReportSection;
import org.takoyaki.reportmaker.model.SectionType;
import org.takoyaki.reportmaker.model.SubsectionBlock;
import org.takoyaki.reportmaker.model.TextBlock;
import org.takoyaki.reportmaker.model.WebReference;
import org.takoyaki.reportmaker.util.json.JsonArray;
import org.takoyaki.reportmaker.util.json.JsonObject;
import org.takoyaki.reportmaker.util.json.JsonParser;
import org.takoyaki.reportmaker.util.json.JsonValue;
import org.takoyaki.reportmaker.util.json.JsonWriter;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public final class ProjectJsonCodec implements ProjectDocumentCodec {
    private static final int FORMAT_VERSION = 1;

    @Override
    public String encode(ReportDocument document) {
        JsonObject root = new JsonObject()
                .put("formatVersion", FORMAT_VERSION)
                .put("title", document.getTitle())
                .put("englishTitle", document.getEnglishTitle())
                .put("preparationSections", sections(document.getPreparationSections()))
                .put("mainSections", sections(document.getMainSections()))
                .put("equipment", equipment(document.getEquipment()))
                .put("figures", figures(document.getFigures()))
                .put("equations", equations(document.getEquations()))
                .put("references", references(document.getReferences()));
        return JsonWriter.write(root);
    }

    @Override
    public ReportDocument decode(String json) {
        JsonObject root = (JsonObject) JsonParser.parse(json);
        if (root.integer("formatVersion") != FORMAT_VERSION) {
            throw new IllegalArgumentException("Unsupported project format version");
        }
        ReportDocument document = new ReportDocument();
        document.setTitle(root.string("title"));
        document.setEnglishTitle(root.string("englishTitle"));
        document.getPreparationSections().clear();
        document.getMainSections().clear();
        readSections(root.array("preparationSections"), document.getPreparationSections());
        readSections(root.array("mainSections"), document.getMainSections());
        readEquipment(root.array("equipment"), document);
        readFigures(root.array("figures"), document);
        readEquations(root.array("equations"), document);
        readReferences(root.array("references"), document);
        return document;
    }

    private JsonArray sections(List<ReportSection> sections) {
        JsonArray values = new JsonArray();
        for (ReportSection section : sections) {
            JsonArray blocks = new JsonArray();
            section.getBlocks().forEach(block -> blocks.add(block(block)));
            values.add(new JsonObject()
                    .put("type", section.getType().name())
                    .put("title", section.getTitle())
                    .put("blocks", blocks));
        }
        return values;
    }

    private JsonObject block(ReportBlock block) {
        JsonObject value = new JsonObject().put("type", block.getType().name());
        if (block instanceof TextBlock text) value.put("text", text.getText());
        else if (block instanceof SubsectionBlock subsection) {
            value.put("title", subsection.getTitle()).put("importedNumber", subsection.getImportedNumber());
        } else if (block instanceof FigureBlock figure) value.put("figureId", figure.getFigureId().toString());
        else if (block instanceof EquationBlock equation) value.put("equationId", equation.getEquationId().toString());
        return value;
    }

    private JsonArray equipment(List<Equipment> items) {
        JsonArray values = new JsonArray();
        for (Equipment item : items) {
            values.add(new JsonObject().put("name", item.getName()).put("rating", item.getRating())
                    .put("class", item.getEquipmentClass()).put("manufacturer", item.getManufacturer())
                    .put("model", item.getModel()).put("serialNumber", item.getSerialNumber())
                    .put("assetNumber", item.getAssetNumber()));
        }
        return values;
    }

    private JsonArray figures(List<ReportFigure> items) {
        JsonArray values = new JsonArray();
        for (ReportFigure item : items) {
            values.add(new JsonObject().put("id", item.getId().toString()).put("number", item.getNumber())
                    .put("name", item.getName()).put("imageEntry", imageEntryName(item)));
        }
        return values;
    }

    private JsonArray equations(List<ReportEquation> items) {
        JsonArray values = new JsonArray();
        for (ReportEquation item : items) {
            values.add(new JsonObject().put("id", item.getId().toString()).put("number", item.getNumber())
                    .put("name", item.getName()).put("latex", item.getLatex()));
        }
        return values;
    }

    private JsonArray references(List<Reference> items) {
        JsonArray values = new JsonArray();
        for (Reference item : items) {
            JsonObject value = new JsonObject().put("type", item.getType().name());
            if (item instanceof WebReference web) {
                value.put("author", web.getAuthor()).put("pageTitle", web.getPageTitle())
                        .put("url", web.getUrl()).put("accessedDate", web.getAccessedDate());
            } else if (item instanceof BookReference book) {
                value.put("author", book.getAuthor()).put("title", book.getTitle())
                        .put("publisher", book.getPublisher()).put("publicationYear", book.getPublicationYear());
            }
            values.add(value);
        }
        return values;
    }

    private void readSections(JsonArray values, List<ReportSection> target) {
        for (JsonValue value : values.values()) {
            JsonObject object = (JsonObject) value;
            ReportSection section = new ReportSection(
                    SectionType.valueOf(object.string("type")), object.string("title"));
            for (JsonValue block : object.array("blocks").values()) {
                section.getBlocks().add(readBlock((JsonObject) block));
            }
            target.add(section);
        }
    }

    private ReportBlock readBlock(JsonObject object) {
        return switch (org.takoyaki.reportmaker.model.BlockType.valueOf(object.string("type"))) {
            case TEXT -> new TextBlock(object.string("text"));
            case SUBSECTION -> new SubsectionBlock(object.string("title"), object.string("importedNumber"));
            case FIGURE -> new FigureBlock(UUID.fromString(object.string("figureId")));
            case EQUATION -> new EquationBlock(UUID.fromString(object.string("equationId")));
        };
    }

    private void readEquipment(JsonArray values, ReportDocument document) {
        for (JsonValue value : values.values()) {
            JsonObject object = (JsonObject) value;
            Equipment item = new Equipment();
            item.setName(object.string("name")); item.setRating(object.string("rating"));
            item.setEquipmentClass(object.string("class")); item.setManufacturer(object.string("manufacturer"));
            item.setModel(object.string("model")); item.setSerialNumber(object.string("serialNumber"));
            item.setAssetNumber(object.string("assetNumber")); document.getEquipment().add(item);
        }
    }

    private void readFigures(JsonArray values, ReportDocument document) {
        for (JsonValue value : values.values()) {
            JsonObject object = (JsonObject) value;
            document.getFigures().add(new ReportFigure(UUID.fromString(object.string("id")), object.integer("number"),
                    object.string("name"), Path.of(object.string("imageEntry"))));
        }
    }

    private void readEquations(JsonArray values, ReportDocument document) {
        for (JsonValue value : values.values()) {
            JsonObject object = (JsonObject) value;
            document.getEquations().add(new ReportEquation(UUID.fromString(object.string("id")), object.integer("number"),
                    object.string("name"), object.string("latex")));
        }
    }

    private void readReferences(JsonArray values, ReportDocument document) {
        for (JsonValue value : values.values()) {
            JsonObject object = (JsonObject) value;
            ReferenceType type = ReferenceType.valueOf(object.string("type"));
            Reference reference = type == ReferenceType.WEB
                    ? new WebReference(object.string("author"), object.string("pageTitle"),
                    object.string("url"), object.string("accessedDate"))
                    : new BookReference(object.string("author"), object.string("title"),
                    object.string("publisher"), object.string("publicationYear"));
            document.getReferences().add(reference);
        }
    }

    private String imageEntryName(ReportFigure figure) {
        String fileName = Path.of(figure.getImagePath()).getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String extension = dot >= 0 ? fileName.substring(dot) : ".png";
        return "images/" + figure.getId() + extension;
    }
}
