package org.takoyaki.reportmaker.model;

import org.takoyaki.reportmaker.i18n.I18n;

public enum SectionType {
    PURPOSE("section.purpose", 1, ReportCategory.PREPARATION),
    PRINCIPLE("section.principle", 2, ReportCategory.PREPARATION),
    METHOD("section.method", 3, ReportCategory.PREPARATION),
    PRE_ASSIGNMENT("section.preAssignment", 4, ReportCategory.PREPARATION),
    EQUIPMENT("section.equipment", 5, ReportCategory.MAIN),
    RESULT("section.result", 6, ReportCategory.MAIN),
    DISCUSSION_SUMMARY("section.discussionSummary", 7, ReportCategory.MAIN),
    CUSTOM("", 0, ReportCategory.MAIN);

    private final String messageKey;
    private final int number;
    private final ReportCategory category;

    SectionType(String messageKey, int number, ReportCategory category) {
        this.messageKey = messageKey;
        this.number = number;
        this.category = category;
    }

    public String getDisplayName() {
        return messageKey.isEmpty() ? "" : I18n.text(messageKey);
    }

    public int getNumber() {
        return number;
    }

    public ReportCategory getCategory() {
        return category;
    }

    public static SectionType fromTitle(String title) {
        for (SectionType type : values()) {
            if (!type.messageKey.isEmpty() && type.getDisplayName().equals(title)) {
                return type;
            }
        }
        return CUSTOM;
    }
}
