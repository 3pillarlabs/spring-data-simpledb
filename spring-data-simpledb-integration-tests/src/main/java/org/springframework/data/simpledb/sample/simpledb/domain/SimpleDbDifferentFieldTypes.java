package org.springframework.data.simpledb.sample.simpledb.domain;

import org.springframework.data.annotation.Id;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: extend with other types to be tested as other type handlers are implemented.
 * Also update equals!
 * One sample for each supported type. Each particular instance and other tests should be included as Regular Junit Tests
 */
public class SimpleDbDifferentFieldTypes {

    @Id
    private String itemName;

    private float primitiveField;

    private String coreField;

    private Object objectField;

    private List<Integer> coreTypeList;

    private List<JSONCompatibleClass> objectList;

    private Set<String> coreTypeSet;

    private Map<String, String> coreTypeMap;

    private JSONCompatibleClass jsonCompatibleClass;

    private long[] primitiveArrayField;

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }


    public float getPrimitiveField() {
        return primitiveField;
    }

    public void setPrimitiveField(float primitiveField) {
        this.primitiveField = primitiveField;
    }

    public String getCoreField() {
        return coreField;
    }

    public void setCoreField(String coreField) {
        this.coreField = coreField;
    }

    public Object getObjectField() {
        return objectField;
    }

    public void setObjectField(Object objectField) {
        this.objectField = objectField;
    }

    public List<Integer> getCoreTypeList() {
        return coreTypeList;
    }

    public long[] getPrimitiveArrayField() {
        return primitiveArrayField.clone();
    }

    public void setCoreTypeList(List<Integer> coreTypeList) {
        this.coreTypeList = coreTypeList;
    }

    public void setPrimitiveArrayField(long[] primitiveArrayField) {
        this.primitiveArrayField = primitiveArrayField.clone();
    }

    public List<JSONCompatibleClass> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<JSONCompatibleClass> objectList) {
        this.objectList = objectList;
    }

    public JSONCompatibleClass getJsonCompatibleClass() {
        return jsonCompatibleClass;
    }

    public void setJsonCompatibleClass(JSONCompatibleClass jsonCompatibleClass) {
        this.jsonCompatibleClass = jsonCompatibleClass;
    }

    public Set<String> getCoreTypeSet() {
        return coreTypeSet;
    }

    public void setCoreTypeSet(Set<String> coreTypeSet) {
        this.coreTypeSet = coreTypeSet;
    }

    public Map<String, String> getCoreTypeMap() {
        return coreTypeMap;
    }

    public void setCoreTypeMap(Map<String, String> coreTypeMap) {
        this.coreTypeMap = coreTypeMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleDbDifferentFieldTypes)) return false;

        SimpleDbDifferentFieldTypes that = (SimpleDbDifferentFieldTypes) o;

        if (Float.compare(that.primitiveField, primitiveField) != 0) return false;
        if (coreField != null ? !coreField.equals(that.coreField) : that.coreField != null) return false;
        if (coreTypeList != null ? !coreTypeList.equals(that.coreTypeList) : that.coreTypeList != null) return false;
        if (coreTypeMap != null ? !coreTypeMap.equals(that.coreTypeMap) : that.coreTypeMap != null) return false;
        if (coreTypeSet != null ? !coreTypeSet.equals(that.coreTypeSet) : that.coreTypeSet != null) return false;
        if (itemName != null ? !itemName.equals(that.itemName) : that.itemName != null) return false;
        if (jsonCompatibleClass != null ? !jsonCompatibleClass.equals(that.jsonCompatibleClass) : that.jsonCompatibleClass != null)
            return false;
        if (objectField != null ? !objectField.equals(that.objectField) : that.objectField != null) return false;
        if (objectList != null ? !objectList.equals(that.objectList) : that.objectList != null) return false;
        if (!Arrays.equals(primitiveArrayField, that.primitiveArrayField)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemName != null ? itemName.hashCode() : 0;
        result = 31 * result + (primitiveField != +0.0f ? Float.floatToIntBits(primitiveField) : 0);
        result = 31 * result + (coreField != null ? coreField.hashCode() : 0);
        result = 31 * result + (objectField != null ? objectField.hashCode() : 0);
        result = 31 * result + (coreTypeList != null ? coreTypeList.hashCode() : 0);
        result = 31 * result + (objectList != null ? objectList.hashCode() : 0);
        result = 31 * result + (coreTypeSet != null ? coreTypeSet.hashCode() : 0);
        result = 31 * result + (coreTypeMap != null ? coreTypeMap.hashCode() : 0);
        result = 31 * result + (jsonCompatibleClass != null ? jsonCompatibleClass.hashCode() : 0);
        result = 31 * result + (primitiveArrayField != null ? Arrays.hashCode(primitiveArrayField) : 0);
        return result;
    }
}
