package org.takoyaki.reportmaker.util;

import java.util.Optional;

public interface EditorDialogController<T> {
    Optional<String> validateInput();
    T buildResult();
}
