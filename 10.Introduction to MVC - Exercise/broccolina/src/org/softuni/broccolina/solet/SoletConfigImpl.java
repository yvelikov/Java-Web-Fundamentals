package org.softuni.broccolina.solet;

import java.util.HashMap;
import java.util.Map;

public class SoletConfigImpl implements SoletConfig {
    private Map<String, Object> attributes;

    public SoletConfigImpl() {
        this.attributes = new HashMap<>();
    }

    @Override
    public Object getAttribute(String attributeName) {
        return this.attributes.getOrDefault(attributeName, null);
    }

    @Override
    public void setAttribute(String key, Object value){
        this.attributes.putIfAbsent(key, value);
    }

}
