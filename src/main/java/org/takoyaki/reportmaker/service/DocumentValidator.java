package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.Reference;

import java.nio.file.Path;
import java.util.Optional;

public interface DocumentValidator {
    Optional<String> required(String value, String label);
    Optional<String> image(String name, Path path);
    Optional<String> equation(String latex);
    Optional<String> reference(Reference reference);
}
