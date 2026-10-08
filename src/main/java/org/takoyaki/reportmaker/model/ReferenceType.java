package org.takoyaki.reportmaker.model;

import org.takoyaki.reportmaker.i18n.I18n;

public enum ReferenceType {
    WEB("reference.web"),
    BOOK("reference.book");

    private final String messageKey;

    ReferenceType(String messageKey) {
        this.messageKey = messageKey;
    }

    @Override
    public String toString() {
        return I18n.text(messageKey);
    }
}
