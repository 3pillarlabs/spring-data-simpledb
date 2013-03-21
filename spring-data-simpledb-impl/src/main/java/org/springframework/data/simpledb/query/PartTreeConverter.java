package org.springframework.data.simpledb.query;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.repository.query.parser.PartTree.OrPart;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.util.StringUtil;

import java.util.Iterator;

public final class PartTreeConverter {

	private PartTreeConverter() {
		/* utility class */
	}
	
	/**
	 * Convert a {@link PartTree} into a where query alike to the one present in the
	 * {@link Query}'s where property.  
	 */
	public static String toIndexedQuery(final PartTree tree) {
		final StringBuilder result = new StringBuilder();
		
		final Iterator<OrPart> orIt = tree.iterator();
		while(orIt.hasNext()) {
			
			final OrPart orPart = orIt.next();
			
			final Iterator<Part> partIt = orPart.iterator();
			while(partIt.hasNext()) {
				final Part part = partIt.next();
				
				result.append(" " + part.getProperty().getSegment() + " ");
				result.append(convertOperator(part.getType()));
				
				if(partIt.hasNext()) {
					result.append(" AND ");
				}
			}
			
			if(orIt.hasNext()) {
				result.append(" OR ");
			}
		}
		
		return StringUtil.removeExtraSpaces(result.toString());
	}
	
	private static String convertOperator(final Part.Type type) {
		String result = "";
		
		switch(type) {
			case SIMPLE_PROPERTY: {
				result = " = ? ";
				break;
			}
			
			case NEGATING_SIMPLE_PROPERTY: {
				result = " != ? ";
				break;
			}
			
			case GREATER_THAN: {
				result = " > ? ";
				break;
			}
			
			case GREATER_THAN_EQUAL: {
				result = " >= ? ";
				break;
			}
			
			case LESS_THAN: {
				result = " < ? ";
				break;
			}
			
			case LESS_THAN_EQUAL: {
				result = " <= ? ";
				break;
			}
			
			case LIKE: {
				result = " LIKE ? ";
				break;
			}
			
			case NOT_LIKE: {
				result = " NOT LIKE ? ";
				break;
			}
			
			case BETWEEN: {
				result = " BETWEEN ? and ? ";
				break;
			}
			
			case IS_NOT_NULL: {
				result = " IS NOT NULL ";
				break;
			}
			
			case IS_NULL: {
				result = " IS NULL ";
				break;
			}
			
			case IN: {
				result = " IN ? ";
				break;
			}
			
			default: {
				throw new MappingException("No matching simpleDB operator for " + type);
			}
		}
		
		return result;
	}
}
