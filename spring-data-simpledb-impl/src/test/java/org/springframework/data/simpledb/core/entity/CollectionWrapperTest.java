package org.springframework.data.simpledb.core.entity;


import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.data.simpledb.core.entity.util.AttributeUtil;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

public class CollectionWrapperTest {

    @Test
    public void serialize_deserialize_sets_of_core_types() {
        SampleCoreCollection sampleCollection = new SampleCoreCollection();
        sampleCollection.setSetOfIntegers( new HashSet<>(Arrays.asList(Integer.valueOf(20), Integer.valueOf(12))));
        sampleCollection.setHashSetOfFloats(new HashSet<>(Arrays.asList(Float.valueOf(23f), Float.valueOf(32f))));

        EntityWrapper<SampleCoreCollection, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class), sampleCollection);
        final Map<String, String> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SampleCoreCollection, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class));
        convertedEntity.deserialize(attributes);

        assertTrue(sampleCollection.equals(convertedEntity.getItem()));

    }

    @Test
    public void serialize_deserialize_lists_of_core_types() {
        SampleCoreCollection sampleCollection = new SampleCoreCollection();
        sampleCollection.setListOfBytes(new ArrayList<>(Arrays.asList(Byte.valueOf("123"), Byte.valueOf("23"))));

        EntityWrapper<SampleCoreCollection, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class), sampleCollection);
        final Map<String, String> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SampleCoreCollection, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class));
        convertedEntity.deserialize(attributes);

        assertTrue(sampleCollection.equals(convertedEntity.getItem()));

    }

    @Test
    public void serialize_deserialize_lists_of_Objects() {
        SampleCoreCollection sampleCollection = new SampleCoreCollection();
        sampleCollection.setListOfObjects(new ArrayList<User>());
        User user = new User();
        user.setName("Simple");
        sampleCollection.getListOfObjects().add(user);

        EntityWrapper<SampleCoreCollection, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class), sampleCollection);
        final Map<String, String> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SampleCoreCollection, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class));
        convertedEntity.deserialize(attributes);

        assertTrue(sampleCollection.equals(convertedEntity.getItem()));

    }

    @Test
    public void serialize_deserialize_collection_instantiated_as_arrayList_of_core_types() {
        SampleCoreCollection sampleCollection = new SampleCoreCollection();
        sampleCollection.setCollectionOfLongs(new ArrayList<>(Arrays.asList(Long.valueOf("123"), Long.valueOf("23"))));

        EntityWrapper<SampleCoreCollection, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class), sampleCollection);
        final Map<String, String> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SampleCoreCollection, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class));
        convertedEntity.deserialize(attributes);

        assertTrue(sampleCollection.equals(convertedEntity.getItem()));

    }

    @Test
    public void deserialize_should_return_null_for_not_instantiated_collections() {
        SampleCoreCollection sampleCollection = new SampleCoreCollection();

        EntityWrapper<SampleCoreCollection, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class), sampleCollection);
        final Map<String, String> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SampleCoreCollection, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class));
        convertedEntity.deserialize(attributes);

        assertTrue(sampleCollection.equals(convertedEntity.getItem()));

    }

    @Test
    public void serialize_should_return_attribute_name_key() {
        SampleCoreCollection collection = new SampleCoreCollection();
        collection.setCollectionOfLongs(new LinkedHashSet<Long>());
        collection.setHashSetOfFloats(new HashSet<Float>());
        collection.setListOfBytes(new ArrayList<Byte>());
        collection.setListOfObjects(new ArrayList<User>());
        collection.setSetOfIntegers(new HashSet<Integer>());

        /* ----------------------- Serialize Representation ------------------------ */
        EntityWrapper<SampleCoreCollection, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class), collection);
        final Map<String, String> attributes = sdbEntity.serialize();

        assertTrue(attributes.size() == 5);

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
        private List<User> listOfObjects;

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

        public <T extends Collection<Long>> void setCollectionOfLongs(T collectionOfLongs) {
            this.collectionOfLongs = collectionOfLongs;
        }

        public List<User> getListOfObjects() {
            return listOfObjects;
        }

        public void setListOfObjects(List<User> listOfObjects) {
            this.listOfObjects = listOfObjects;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SampleCoreCollection that = (SampleCoreCollection) o;

            if (collectionOfLongs != null ? !collectionOfLongs.equals(that.collectionOfLongs) : that.collectionOfLongs != null)
                return false;
            if (hashSetOfFloats != null ? !hashSetOfFloats.equals(that.hashSetOfFloats) : that.hashSetOfFloats != null)
                return false;
            if (listOfBytes != null ? !listOfBytes.equals(that.listOfBytes) : that.listOfBytes != null) return false;
            if (setOfIntegers != null ? !setOfIntegers.equals(that.setOfIntegers) : that.setOfIntegers != null)
                return false;
            if (listOfObjects != null ? !listOfObjects.equals(that.listOfObjects) : that.listOfObjects != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = setOfIntegers != null ? setOfIntegers.hashCode() : 0;
            result = 31 * result + (hashSetOfFloats != null ? hashSetOfFloats.hashCode() : 0);
            result = 31 * result + (listOfBytes != null ? listOfBytes.hashCode() : 0);
            result = 31 * result + (collectionOfLongs != null ? collectionOfLongs.hashCode() : 0);
            result = 31 * result + (listOfObjects != null ? listOfObjects.hashCode() : 0);
            return result;
        }
    }

    public static class User {
        public String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof User)) return false;

            User user = (User) o;

            if (name != null ? !name.equals(user.name) : user.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }
}
