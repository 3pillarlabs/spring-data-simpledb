package org.springframework.data.simpledb.query;

import java.util.ArrayList;

/**
 * Created by: mgrozea
 */
public class SampleEntity {
    private int sampleAttribute;
    private ArrayList<Integer> sampleList;
    private NestedClass sampleNestedAttribute;


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

    static class NestedClass{}
}
