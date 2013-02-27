package org.springframework.data.simpledb.query.parser;

public enum PatternConstants {
	SELECT_PATTERN("^(?:\\s+)?(.+?)(?:\\s+)?$"),
	WHERE_PATTERN("^(?:\\s*)(.+?)(?:\\s*)(=|!=|>|<|like|not|between|in|is|every())(?:\\s*)(\\S+)(\\s+)?$");
	
	private String pattternString;

	private PatternConstants(String pattternString) {
		this.pattternString = pattternString;
	}

	public String getPattternString() {
		return pattternString;
	}	
}
