package org.springframework.data.simpledb.core.entity.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* Created by: mgrozea
*/
public class SampleCoreCollection {
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
