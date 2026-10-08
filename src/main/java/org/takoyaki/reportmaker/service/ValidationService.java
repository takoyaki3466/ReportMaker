package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.BookReference;
import org.takoyaki.reportmaker.model.Reference;
import org.takoyaki.reportmaker.model.WebReference;
import org.takoyaki.reportmaker.i18n.I18n;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class ValidationService implements DocumentValidator {
    public Optional<String> required(String value, String label) {
        return value == null || value.isBlank()
                ? Optional.of(I18n.text("validation.required", label)) : Optional.empty();
    }

    public Optional<String> image(String name, Path path) {
        Optional<String> nameError = required(name, I18n.text("validation.figureName"));
        if (nameError.isPresent()) {
            return nameError;
        }
        if (path == null || !Files.isRegularFile(path)) {
            return Optional.of(I18n.text("validation.image"));
        }
        return Optional.empty();
    }

    public Optional<String> equation(String latex) {
        return required(latex, I18n.text("validation.equation"));
    }

    public Optional<String> reference(Reference reference) {
        if (reference instanceof WebReference web) {
            if (web.getAuthor().isBlank() || web.getPageTitle().isBlank()
                    || web.getUrl().isBlank() || web.getAccessedDate().isBlank()) {
                return Optional.of(I18n.text("validation.webReference"));
            }
            try {
                URI uri = new URI(web.getUrl());
                if (uri.getScheme() == null || uri.getHost() == null) {
                    return Optional.of(I18n.text("validation.url"));
                }
            } catch (URISyntaxException exception) {
                return Optional.of(I18n.text("validation.url"));
            }
        } else if (reference instanceof BookReference book) {
            if (book.getAuthor().isBlank() || book.getTitle().isBlank()
                    || book.getPublisher().isBlank() || book.getPublicationYear().isBlank()) {
                return Optional.of(I18n.text("validation.bookReference"));
            }
        }
        return Optional.empty();
    }
}
