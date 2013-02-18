package org.springframework.data.simpledb.query;

import org.junit.Test;
import org.springframework.data.mapping.model.MappingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by: mgrozea
 */
public class SimpleDbResultConverterTest {

    public static final int SAMPLE_INT_VALUE = 5;

    @Test
    public void filterNamedAttributesAsList_should_return_list_of_named_attributes() throws Exception {
        List<SampleEntity> entities = new ArrayList<>();
        SampleEntity entity = new SampleEntity();
        entity.setSampleAttribute(SAMPLE_INT_VALUE);
        entities.add(entity);

        List<Object> filteredAttributes = SimpleDbResultConverter.filterNamedAttributesAsList(entities, "sampleAttribute");

        assertEquals(1, filteredAttributes.size());

        Object firstElement = filteredAttributes.get(0);

        assertEquals(SAMPLE_INT_VALUE, firstElement);
    }

    @Test
    public void filterNamedAttributesAsList_should_work_for_list_attributes() throws Exception {
        List<SampleEntity> entities = new ArrayList<>();
        SampleEntity entity = new SampleEntity();
        entity.setSampleList(new ArrayList<Integer>());
        entities.add(entity);

        List<Object> filteredAttributes = SimpleDbResultConverter.filterNamedAttributesAsList(entities, "sampleList");

        assertEquals(1, filteredAttributes.size());

        Object firstElement = filteredAttributes.get(0);

        assertTrue(firstElement instanceof List);
    }



    @Test(expected = MappingException.class)
    public void filterNamedAttributesAsList_should_not_return_list_of_named_attributes_for_wrong_att() throws Exception {
        List<SampleEntity> entities = new ArrayList<>();
        SampleEntity entity = new SampleEntity();
        entities.add(entity);

        SimpleDbResultConverter.filterNamedAttributesAsList(entities, "wrongAttribute");
    }


    @Test
    public void filterNamedAttributesAsSet_should_return_Set_of_named_attributes() throws Exception {
        List<SampleEntity> entities = new ArrayList<>();
        SampleEntity entity = new SampleEntity();
        entity.setSampleAttribute(SAMPLE_INT_VALUE);
        entities.add(entity);

        Set<Object> filteredAttributes = SimpleDbResultConverter.filterNamedAttributesAsSet(entities, "sampleAttribute");

        assertEquals(1, filteredAttributes.size());

        Object firstElement = filteredAttributes.iterator().next();

        assertEquals(SAMPLE_INT_VALUE, firstElement);
    }


    @Test
    public void toListOfListOfObject_should_return_List_of_Lists_containing_requested_attributes(){
        List<SampleEntity> entities = new ArrayList<>();
        SampleEntity entity = new SampleEntity();
        entity.setSampleAttribute(SAMPLE_INT_VALUE);
        entity.setSampleList(new ArrayList<Integer>());
        entities.add(entity);

        List<String> attributes = Arrays.asList("sampleAttribute", "sampleList");

        List<List<Object>> filteredAttributes = SimpleDbResultConverter.toListOfListOfObject(entities, attributes);

        //one row
        assertEquals(1, filteredAttributes.size());

        //two columns
        List<Object> columns = filteredAttributes.get(0);
        assertEquals(2, columns.size());

        assertEquals(SAMPLE_INT_VALUE, columns.get(0));

    }

}
