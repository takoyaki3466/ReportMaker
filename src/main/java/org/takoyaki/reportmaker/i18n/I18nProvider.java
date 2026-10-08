package org.takoyaki.reportmaker.i18n;

import java.util.ResourceBundle;

public interface I18nProvider {
    String text(String key, Object... arguments);
    ResourceBundle bundle();
}
