package io.quarkus.extensions.catalog.model;

import java.util.Map;
import java.util.Objects;

public class Extension {
    public String name;
    public Map<String,String> coords;
    public Map<String, Object> metadata;

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Extension extension = (Extension) o;
        return name.equals(extension.name);
    }

    @Override public int hashCode() {
        return Objects.hash(name);
    }
}
