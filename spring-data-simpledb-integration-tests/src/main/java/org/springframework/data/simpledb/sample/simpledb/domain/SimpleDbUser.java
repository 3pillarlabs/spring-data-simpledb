package org.springframework.data.simpledb.sample.simpledb.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.simpledb.annotation.Attributes;
import org.springframework.data.simpledb.annotation.Domain;

import java.util.Map;

@Domain(value = "Gigi")
public class SimpleDbUser {

    @Id
    private String itemName;


    @Attributes
    private Map<String, String> atts;


}
