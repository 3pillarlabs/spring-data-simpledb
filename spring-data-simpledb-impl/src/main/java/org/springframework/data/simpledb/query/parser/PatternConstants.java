package org.springframework.data.simpledb.query.parser;

public enum PatternConstants {
	SELECT_PATTERN("(?:\\s*)(.+)(?:\\s*)"),
	WHERE_PATTERN("(?:\\s*)(.+?)(?:\\s*)(=|!=|>|<|\\slike|\\snot|\\sbetween\\sin|\\sis|\\severy())");
	
	private String pattternString;

	private PatternConstants(String pattternString) {
		this.pattternString = pattternString;
	}

	public String getPattternString() {
		return pattternString;
	}	
}
