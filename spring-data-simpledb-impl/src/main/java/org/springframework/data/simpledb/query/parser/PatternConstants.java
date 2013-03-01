package org.springframework.data.simpledb.query.parser;
/**
 * Based on these patterns the annotated query parameters are parsed.<br/> 
 * Used in {@link QueryParserUtils}. 
 */
public enum PatternConstants {
	SELECT_PATTERN("^(?:\\s*)(.+?)(?:\\s*)$"),
	
	WHERE_PATTERN("^(?:\\s*)(.+?)(?:\\s*)(!=|>|<|=|>=|<=|like|not|between|in|is|every())(?:\\s*)(\\S+)(\\s+)?$"),
	
	UN_ESCAPED_BACKTICKED_PARAMS("[^`](\\w+)[^`]\\s*(?:!=|>|<|=|>=|<=|like|not|between|in|is|every())"),
	
	BACKTICK_CHAR("`");
	
	
	private String pattternString;

	private PatternConstants(String pattternString) {
		this.pattternString = pattternString;
	}

	public String getPattternString() {
		return pattternString;
	}	
}
