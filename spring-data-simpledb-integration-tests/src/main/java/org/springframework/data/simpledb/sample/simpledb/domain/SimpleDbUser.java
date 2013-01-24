package org.springframework.data.simpledb.sample.simpledb.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.simpledb.annotation.Attributes;
import org.springframework.data.simpledb.annotation.Database;

import java.util.Map;

@Database(value = "testDB")
public class SimpleDbUser {

    @Id
    private String itemName;


    @Attributes
    private Map<String, String> atts;


    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getItemName() {
		return itemName;
	}


    public void setAtts(Map<String, String> atts) {
        this.atts = atts;
    }
    public Map<String, String> getAtts() {
		return atts;
	}
}
