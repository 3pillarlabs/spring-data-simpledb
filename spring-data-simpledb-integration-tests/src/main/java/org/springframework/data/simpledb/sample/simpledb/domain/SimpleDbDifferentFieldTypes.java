package org.springframework.data.simpledb.sample.simpledb.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;

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
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
