package org.springframework.data.simpledb.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: mgrozea
 */
public class SampleEntity {
    private int sampleAttribute;
    private ArrayList<Integer> sampleList;
    private NestedClass sampleNestedAttribute;
    private List<List<Integer>> listOfListOfInteger;


    public void setSampleAttribute(int sampleAttribute) {

        this.sampleAttribute = sampleAttribute;
    }

    public int getSampleAttribute() {
        return sampleAttribute;
    }

    public void setSampleList(ArrayList<Integer> sampleList) {

        this.sampleList = sampleList;
    }

    public ArrayList<Integer> getSampleList() {
        return sampleList;
    }

    public NestedClass getSampleNestedAttribute() {
        return sampleNestedAttribute;
    }

    public void setSampleNestedAttribute(NestedClass sampleNestedAttribute) {
        this.sampleNestedAttribute = sampleNestedAttribute;
    }

    public List<List<Integer>> getListOfListOfInteger() {
        return listOfListOfInteger;
    }

    public void setListOfListOfInteger(List<List<Integer>> listOfListOfInteger) {
        this.listOfListOfInteger = listOfListOfInteger;
    }

    static class NestedClass{}
}
