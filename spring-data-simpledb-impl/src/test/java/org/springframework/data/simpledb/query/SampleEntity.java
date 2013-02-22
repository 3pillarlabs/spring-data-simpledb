package org.springframework.data.simpledb.query;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class SampleEntity {
    @Id
    private String item_id;
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

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    static class NestedClass{}
}
