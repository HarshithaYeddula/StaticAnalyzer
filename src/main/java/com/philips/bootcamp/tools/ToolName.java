package com.philips.bootcamp.tools;

import com.philips.bootcamp.domain.Tool;

public enum ToolName {
    MAVEN(new Maven()),
    CHECKSTYLE(new Checkstyle()),
    PMD(new PMD());

    private Tool toolInstance;

    private ToolName(Tool toolInstance) {
        this.toolInstance = toolInstance;
    }

    public Tool getInstance() {
        return toolInstance;
    }
}