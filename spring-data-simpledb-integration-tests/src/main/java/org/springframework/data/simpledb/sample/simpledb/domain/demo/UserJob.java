package org.springframework.data.simpledb.sample.simpledb.domain.demo;

import org.springframework.data.annotation.Id;

public class UserJob {

    @Id private String itemId;
    private Source source;
    private String stringField;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

	public String getStringField() {
		return stringField;
	}

	public void setStringField(String stringField) {
		this.stringField = stringField;
	}
    
}
