package org.softuni.broccolina.solet;

public interface SoletConfig {
    Object getAttribute(String attributeName);

    void setAttribute(String key, Object value);
}
