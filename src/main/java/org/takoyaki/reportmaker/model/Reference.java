package org.takoyaki.reportmaker.model;

import java.io.Serializable;

public sealed interface Reference extends Serializable permits WebReference, BookReference {
    ReferenceType getType();
    String getDisplayText();
}
