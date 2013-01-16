package org.springframework.data.simpledb.sample.simpledb.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.simpledb.annotation.Attributes;
import org.springframework.data.simpledb.annotation.Domain;
import org.springframework.data.simpledb.annotation.ItemName;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Map;

@Domain(value = "Gigi")
public class SimpleDbUser {

    @ItemName
    private String itemName;


    @Attributes
    private Map<String, String> atts;


}
