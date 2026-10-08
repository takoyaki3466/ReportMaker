package org.takoyaki.reportmaker.model;

import org.takoyaki.reportmaker.i18n.I18n;

public enum BlockType {
    TEXT("block.text"),
    SUBSECTION("block.subsection"),
    FIGURE("block.figure"),
    EQUATION("block.equation");

    private final String messageKey;

    BlockType(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getDisplayName() {
        return I18n.text(messageKey);
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
