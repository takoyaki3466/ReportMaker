package org.takoyaki.reportmaker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.context.ContextAware;

public final class PreviewController implements ContextAware {
    @FXML private TextArea markdownArea;
    private ApplicationContext context;

    @Override public void setApplicationContext(ApplicationContext context) { this.context = context; refresh(); }
    @FXML private void refresh() { markdownArea.setText(context.getMarkdownRenderer().render(context.getDocument())); }

    @FXML
    private void copy() {
        ClipboardContent content = new ClipboardContent();
        content.putString(markdownArea.getText());
        Clipboard.getSystemClipboard().setContent(content);
    }
}
