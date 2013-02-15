package org.springframework.data.simpledb.core.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.simpledb.core.domain.SimpleDbSampleEntity;
import org.springframework.data.simpledb.core.entity.EntityWrapperTest.AClass.BClass;
import org.springframework.data.simpledb.core.entity.EntityWrapperTest.AClass.BClass.CClass;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class EntityWrapperTest {

    @Test
    public void generateId_should_populate_itemName_of_Item() {
        SimpleDbSampleEntity object = new SimpleDbSampleEntity();
        EntityWrapper sdbEntity = new EntityWrapper(SimpleDbSampleEntity.entityInformation(), object);
        sdbEntity.generateIdIfNotSet();
        assertNotNull(object.getItemName());

    }

    @Test
    public void generateId_should_not_overwrite_existing_id() {
        SimpleDbSampleEntity object = new SimpleDbSampleEntity();
        object.setItemName("gigi");
        EntityWrapper sdbEntity = new EntityWrapper(SimpleDbSampleEntity.entityInformation(), object);
        sdbEntity.generateIdIfNotSet();
        assertEquals("gigi", object.getItemName());
    }

    @Test
    public void generateId_should_create_distinct_values() {
        SimpleDbSampleEntity object1 = new SimpleDbSampleEntity();
        SimpleDbSampleEntity object2 = new SimpleDbSampleEntity();

        EntityWrapper sdbEntity1 = new EntityWrapper(SimpleDbSampleEntity.entityInformation(), object1);
        sdbEntity1.generateIdIfNotSet();
        EntityWrapper sdbEntity2 = new EntityWrapper(SimpleDbSampleEntity.entityInformation(), object2);
        sdbEntity2.generateIdIfNotSet();

        assertNotEquals(object1.getItemName(), object2.getItemName());
    }

    @SuppressWarnings("unchecked")
    private <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
        return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E>getMetadata(clazz);
    }

    @Test
    public void test_getSerializedPrimitiveAttributes() throws ParseException {
        final SampleEntity entity = new SampleEntity();
        entity.setIntField(11);
        entity.setLongField(123);
        entity.setShortField((short) -12);
        entity.setFloatField(-0.01f);
        entity.setDoubleField(1.2d);
        entity.setByteField((byte) 1);
        entity.setBooleanField(Boolean.TRUE);
        entity.setStringField("string");
        entity.setDoubleWrapper(Double.valueOf("2323.32d"));

        EntityWrapper<SampleEntity, String> sdbEntity = new EntityWrapper<>(this.<SampleEntity>readEntityInformation(SampleEntity.class), entity);

        assertNotNull(sdbEntity);

        final Map<String, List<String>> attributes = sdbEntity.serialize();
        assertNotNull(attributes);

		/* test int field */
        List<String> intValues = attributes.get("intField");
        assertNotNull(intValues);
        assertFalse(intValues.isEmpty());
        assertTrue(intValues.size() == 1);
        assertEquals(entity.getIntField(), ((Integer) SimpleDBAttributeConverter.decodeToFieldOfType(intValues.get(0), Integer.class)).intValue());

		/* test long field */
        List<String> longValues = attributes.get("longField");
        assertEquals(entity.getLongField(), ((Long) SimpleDBAttributeConverter.decodeToFieldOfType(longValues.get(0), Long.class)).longValue());

		/* test short field */
        List<String> shortValues = attributes.get("shortField");
        assertEquals(entity.getShortField(), ((Short) SimpleDBAttributeConverter.decodeToFieldOfType(shortValues.get(0), Short.class)).shortValue());

		/* test float field */
        List<String> floatValues = attributes.get("floatField");
        assertTrue(entity.getFloatField() == ((Float) SimpleDBAttributeConverter.decodeToFieldOfType(floatValues.get(0), Float.class)).floatValue());

		/* test double field */
        List<String> doubleValues = attributes.get("doubleField");
        assertTrue(entity.getDoubleField() == ((Double) SimpleDBAttributeConverter.decodeToFieldOfType(doubleValues.get(0), Double.class)).doubleValue());

		/* test byte field */
        List<String> byteValues = attributes.get("byteField");
        assertTrue(entity.getByteField() == ((Byte) SimpleDBAttributeConverter.decodeToFieldOfType(byteValues.get(0), Byte.class)).byteValue());

		/* test boolean field */
        List<String> booleanValues = attributes.get("booleanField");
        assertTrue(entity.getBooleanField() == ((Boolean) SimpleDBAttributeConverter.decodeToFieldOfType(booleanValues.get(0), Boolean.class)).booleanValue());

    }

    /* ***************************** Test serializing nested domain entities ******************* */
    @Test
    public void should_generate_attribute_keys_for_nested_domain_fields() {
        final AClass aDomain = new AClass();
        {
            aDomain.nestedB = new BClass();
            {
                aDomain.nestedB.floatField = 21f;
                aDomain.nestedB.nestedNestedC = new CClass();
                {
                    aDomain.nestedB.nestedNestedC.doubleField = 14d;
                }
            }
        }

        EntityWrapper<AClass, String> sdbEntity = new EntityWrapper<>(this.<AClass>readEntityInformation(AClass.class), aDomain);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        assertNotNull(attributes);
        assertTrue(attributes.size() == 3);

        final Set<String> keySet = attributes.keySet();
        assertTrue(keySet.contains("intField"));
        assertTrue(keySet.contains("nestedB.floatField"));
        assertTrue(keySet.contains("nestedB.nestedNestedC.doubleField"));
    }

    @Test
    public void should_build_entity_with_nested_domain_entities() {
        final AClass aDomain = new AClass();
        {
            aDomain.intField = 13;
            aDomain.nestedB = new BClass();
            {
                aDomain.nestedB.floatField = 21f;
                aDomain.nestedB.nestedNestedC = new CClass();
                {
                    aDomain.nestedB.nestedNestedC.doubleField = 14d;
                }
            }
        }

        EntityWrapper<AClass, String> sdbEntity = new EntityWrapper<>(this.<AClass>readEntityInformation(AClass.class), aDomain);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

		/* convert back */
        final EntityWrapper<AClass, String> convertedEntity = new EntityWrapper<>(this.<AClass>readEntityInformation(AClass.class));
        convertedEntity.deserialize(attributes);

        assertTrue(aDomain.equals(convertedEntity.getItem()));
    }

    @SuppressWarnings("unused")
    public static class AClass {

        @Id
        private String id;
        private int intField;
        private BClass nestedB;
        private Long longField;

        public BClass getNestedB() {
            return nestedB;
        }

        public void setNestedB(BClass nestedB) {
            this.nestedB = nestedB;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getIntField() {
            return intField;
        }

        public void setIntField(int intField) {
            this.intField = intField;
        }

        private Long getLongField() {
            return longField;
        }

        public void setLongField(Long longField) {
            this.longField = longField;
        }

        public static class BClass {

            private float floatField;
            private CClass nestedNestedC;

            public CClass getNestedNestedC() {
                return nestedNestedC;
            }

            public void setNestedNestedC(CClass nestedNestedC) {
                this.nestedNestedC = nestedNestedC;
            }

            public float getFloatField() {
                return floatField;
            }

            public void setFloatField(float floatField) {
                this.floatField = floatField;
            }

            @Override
            public boolean equals(Object obj) {
                return EqualsBuilder.reflectionEquals(this, obj);
            }

            public static class CClass {

                private double doubleField;

                public double getDoubleField() {
                    return doubleField;
                }

                public void setDoubleField(double doubleField) {
                    this.doubleField = doubleField;
                }

                @Override
                public boolean equals(Object obj) {
                    return EqualsBuilder.reflectionEquals(this, obj);
                }
            }
        }

        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj);
        }
    }

    public static class SampleEntity {
        private int intField;
        private float floatField;
        private double doubleField;
        private short shortField;
        private long longField;
        private byte byteField;
        private boolean booleanField;
        private String stringField;
        private Double doubleWrapper;

        public int getIntField() {
            return intField;
        }

        public void setIntField(int intField) {
            this.intField = intField;
        }

        public float getFloatField() {
            return floatField;
        }

        public void setFloatField(float floatField) {
            this.floatField = floatField;
        }

        public double getDoubleField() {
            return doubleField;
        }

        public void setDoubleField(double doubleField) {
            this.doubleField = doubleField;
        }

        public short getShortField() {
            return shortField;
        }

        public void setShortField(short shortField) {
            this.shortField = shortField;
        }

        public long getLongField() {
            return longField;
        }

        public void setLongField(long longField) {
            this.longField = longField;
        }

        public byte getByteField() {
            return byteField;
        }

        public void setByteField(byte byteField) {
            this.byteField = byteField;
        }

        public boolean getBooleanField() {
            return booleanField;
        }

        public void setBooleanField(boolean booleanField) {
            this.booleanField = booleanField;
        }

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }

        public Double getDoubleWrapper() {
            return doubleWrapper;
        }

        public void setDoubleWrapper(Double doubleWrapper) {
            this.doubleWrapper = doubleWrapper;
        }
    }

}
