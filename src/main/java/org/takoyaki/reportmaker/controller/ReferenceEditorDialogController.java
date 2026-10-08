package org.takoyaki.reportmaker.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.model.BookReference;
import org.takoyaki.reportmaker.model.Reference;
import org.takoyaki.reportmaker.model.ReferenceType;
import org.takoyaki.reportmaker.model.WebReference;
import org.takoyaki.reportmaker.util.EditorDialogController;

import java.util.Optional;

public final class ReferenceEditorDialogController implements EditorDialogController<Reference> {
    @FXML private ComboBox<ReferenceType> typeCombo;
    @FXML private GridPane webPane;
    @FXML private GridPane bookPane;
    @FXML private TextField webAuthorField;
    @FXML private TextField pageTitleField;
    @FXML private TextField urlField;
    @FXML private TextField accessedDateField;
    @FXML private TextField bookAuthorField;
    @FXML private TextField bookTitleField;
    @FXML private TextField publisherField;
    @FXML private TextField yearField;
    private ApplicationContext context;

    @FXML
    private void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(ReferenceType.values()));
        typeCombo.valueProperty().addListener((observable, oldValue, value) -> updateFields());
        typeCombo.setValue(ReferenceType.WEB);
    }

    public void configure(ApplicationContext context, Reference original) {
        this.context = context;
        if (original instanceof WebReference web) {
            typeCombo.setValue(ReferenceType.WEB); webAuthorField.setText(web.getAuthor());
            pageTitleField.setText(web.getPageTitle()); urlField.setText(web.getUrl()); accessedDateField.setText(web.getAccessedDate());
        } else if (original instanceof BookReference book) {
            typeCombo.setValue(ReferenceType.BOOK); bookAuthorField.setText(book.getAuthor());
            bookTitleField.setText(book.getTitle()); publisherField.setText(book.getPublisher()); yearField.setText(book.getPublicationYear());
        }
        updateFields();
    }

    @Override public Optional<String> validateInput() { return context.getValidationService().reference(buildResult()); }

    @Override
    public Reference buildResult() {
        return typeCombo.getValue() == ReferenceType.WEB
                ? new WebReference(webAuthorField.getText(), pageTitleField.getText(), urlField.getText(), accessedDateField.getText())
                : new BookReference(bookAuthorField.getText(), bookTitleField.getText(), publisherField.getText(), yearField.getText());
    }

    private void updateFields() {
        boolean web = typeCombo.getValue() == ReferenceType.WEB;
        show(webPane, web); show(bookPane, !web);
    }

    private void show(Node node, boolean visible) { node.setVisible(visible); node.setManaged(visible); }
}
