package org.springframework.data.simpledb.core.entity.domain;

import java.util.Map;

/**
* Created by: mgrozea
*/
public class SampleCoreMap {
    private Map<String, String> mapOfStrings;
    private Map<Byte, Byte> mapOfByte;

    public Map<String, String> getMapOfStrings() {
        return mapOfStrings;
    }

    public void setMapOfStrings(Map<String, String> mapOfStrings) {
        this.mapOfStrings = mapOfStrings;
    }

    public Map<Byte, Byte> getMapOfByte() {
        return mapOfByte;
    }

    public void setMapOfByte(Map<Byte, Byte> mapOfByte) {
        this.mapOfByte = mapOfByte;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SampleCoreMap that = (SampleCoreMap) o;

        if (mapOfByte != null ? !mapOfByte.equals(that.mapOfByte) : that.mapOfByte != null) return false;
        if (mapOfStrings != null ? !mapOfStrings.equals(that.mapOfStrings) : that.mapOfStrings != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mapOfStrings != null ? mapOfStrings.hashCode() : 0;
        result = 31 * result + (mapOfByte != null ? mapOfByte.hashCode() : 0);
        return result;
    }
}
