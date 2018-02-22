package org.softuni.summer.core;

import org.softuni.summer.api.Model;

public final class TemplateEngine {
    private final String templatesFolderPath;

    private Model model;

    public TemplateEngine(String templatesFolderPath) {
        this.templatesFolderPath = templatesFolderPath;
    }
}
