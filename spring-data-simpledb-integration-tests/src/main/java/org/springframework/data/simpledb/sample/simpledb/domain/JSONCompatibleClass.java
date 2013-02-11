package org.springframework.data.simpledb.sample.simpledb.domain;

/**
 * Created by: mgrozea
 */
public class JSONCompatibleClass {
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JSONCompatibleClass)) return false;

        JSONCompatibleClass that = (JSONCompatibleClass) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
