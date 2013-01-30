package org.springframework.data.simpledb.core.entity;

import org.junit.Test;
import org.springframework.data.annotation.Id;

import java.util.Set;

public class EntityWrapperCollectionTest {

    static class SamplePrimitivesArray {
        @Id private String id;
        private long[] longPrimitives;
    }

    static class SampleCoreCollection {
        @Id private String id;
        private Set<String> simpleDbCollectionAttributes;
    }

}
