package org.springframework.data.simpledb.core.entity;


import org.junit.Test;
import org.springframework.data.simpledb.core.entity.util.AttributeUtil;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

import java.util.*;

import static org.junit.Assert.assertTrue;

public class CollectionWrapperTest {

    @Test
    public void serialize_deserialize_sets_of_core_types() {
        SampleCoreCollection sampleCollection = new SampleCoreCollection();
        sampleCollection.setOfIntegers = new HashSet<>(Arrays.asList(Integer.valueOf(20), Integer.valueOf(12)));
        sampleCollection.hashSetOfFloats = new HashSet<>(Arrays.asList(Float.valueOf(23f), Float.valueOf(32f)));

        EntityWrapper<SampleCoreCollection, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class), sampleCollection);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SampleCoreCollection, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class));
        convertedEntity.deserialize(attributes);

        assertTrue(sampleCollection.equals(convertedEntity.getItem()));

    }

    @Test
    public void serialize_deserialize_lists_of_core_types() {
        SampleCoreCollection sampleCollection = new SampleCoreCollection();
        sampleCollection.listOfBytes = new ArrayList<>(Arrays.asList(Byte.valueOf("123"), Byte.valueOf("23")));

        EntityWrapper<SampleCoreCollection, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class), sampleCollection);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SampleCoreCollection, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class));
        convertedEntity.deserialize(attributes);

        assertTrue(sampleCollection.equals(convertedEntity.getItem()));

    }

    @Test
    public void serialize_deserialize_collection_instantiated_as_arrayList_of_core_types() {
        SampleCoreCollection sampleCollection = new SampleCoreCollection();
        sampleCollection.collectionOfLongs = new ArrayList<>(Arrays.asList(Long.valueOf("123"), Long.valueOf("23")));

        EntityWrapper<SampleCoreCollection, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class), sampleCollection);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SampleCoreCollection, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class));
        convertedEntity.deserialize(attributes);

        assertTrue(sampleCollection.equals(convertedEntity.getItem()));

    }

    @Test
    public void deserialize_should_return_null_for_not_instantiated_collections() {
        SampleCoreCollection sampleCollection = new SampleCoreCollection();

        EntityWrapper<SampleCoreCollection, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class), sampleCollection);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SampleCoreCollection, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class));
        convertedEntity.deserialize(attributes);

        assertTrue(sampleCollection.equals(convertedEntity.getItem()));

    }

    @Test
    public void serialize_should_return_attribute_name_key() {
        SampleCoreCollection collection = new SampleCoreCollection();
        collection.collectionOfLongs = new LinkedHashSet<>();
        collection.hashSetOfFloats = new HashSet<>();
        collection.listOfBytes = new ArrayList<>();
        collection.setOfIntegers = new HashSet<>();

        /* ----------------------- Serialize Representation ------------------------ */
        EntityWrapper<SampleCoreCollection, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class), collection);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        assertTrue(attributes.size() == 4);

        for(String attributeName : AttributeUtil.<SampleCoreCollection>getAttributeNamesThroughReflection(SampleCoreCollection.class)) {
            assertTrue(attributes.containsKey(attributeName));
        }

    }

    private <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
        return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E>getMetadata(clazz);
    }


    public static class SampleCoreCollection {
        private Set<Integer> setOfIntegers;
        private HashSet<Float> hashSetOfFloats;
        private List<Byte> listOfBytes;
        private Collection<Long> collectionOfLongs;

        public Set<Integer> getSetOfIntegers() {
            return setOfIntegers;
        }

        public void setSetOfIntegers(Set<Integer> setOfIntegers) {
            this.setOfIntegers = setOfIntegers;
        }

        public HashSet<Float> getHashSetOfFloats() {
            return hashSetOfFloats;
        }

        public void setHashSetOfFloats(HashSet<Float> hashSetOfFloats) {
            this.hashSetOfFloats = hashSetOfFloats;
        }

        public List<Byte> getListOfBytes() {
            return listOfBytes;
        }

        public void setListOfBytes(List<Byte> listOfBytes) {
            this.listOfBytes = listOfBytes;
        }

        public Collection<Long> getCollectionOfLongs() {
            return collectionOfLongs;
        }

        public void setCollectionOfLongs(Collection<Long> collectionOfLongs) {
            this.collectionOfLongs = collectionOfLongs;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SampleCoreCollection)) return false;

            SampleCoreCollection that = (SampleCoreCollection) o;

            if (collectionOfLongs != null ? !collectionOfLongs.equals(that.collectionOfLongs) : that.collectionOfLongs != null)
                return false;
            if (hashSetOfFloats != null ? !hashSetOfFloats.equals(that.hashSetOfFloats) : that.hashSetOfFloats != null)
                return false;
            if (listOfBytes != null ? !listOfBytes.equals(that.listOfBytes) : that.listOfBytes != null) return false;
            if (setOfIntegers != null ? !setOfIntegers.equals(that.setOfIntegers) : that.setOfIntegers != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = setOfIntegers != null ? setOfIntegers.hashCode() : 0;
            result = 31 * result + (hashSetOfFloats != null ? hashSetOfFloats.hashCode() : 0);
            result = 31 * result + (listOfBytes != null ? listOfBytes.hashCode() : 0);
            result = 31 * result + (collectionOfLongs != null ? collectionOfLongs.hashCode() : 0);
            return result;
        }
    }
}
