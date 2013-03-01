
package org.springframework.data.simpledb.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.simpledb.query.parser.PatternConstants;
import org.springframework.data.simpledb.util.MetadataParser;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;
import org.springframework.data.simpledb.util.SupportedCoreTypes;

/**
 *  Main Responsibility of QueryUtils is to work with: <br/>
 *  <b>Query Strings</b> <br/> 
 *  <b>Build Indexed-By Queries</b> <br/> 
 *  <b>Named Queries</b> <br/>
 */
 
public final class QueryUtils {

	private static final String BIND_PARAMETER_REGEX = "(\\?)";
	private static final String SINGLE_QUOTE = "'";

	private QueryUtils() {
	}

	public static String bindQueryParameters(SimpleDbRepositoryQuery query, Class<?> domainClazz, Object... parameterValues) {
		final String rawQuery = query.getAnnotatedQuery();
		String completedQuery = null;

		if (hasNamedParameter(query)) {
			completedQuery = bindNamedParameters(query, parameterValues);

		} else if (hasBindParameter(rawQuery)) {
			completedQuery = bindIndexPositionParameters(rawQuery, parameterValues);

		} else {
			completedQuery = rawQuery;
		}
		
		return escapeQueryAttributes(completedQuery, MetadataParser.getIdField(domainClazz).getName());
	}
	
	public static boolean hasNamedParameter(SimpleDbRepositoryQuery query) {
		for (Parameter param : query.getQueryMethod().getParameters()) {
			if (param.isNamedParameter()) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public static boolean hasBindParameter(String query) {
		final Pattern regexPattern = Pattern.compile(BIND_PARAMETER_REGEX);
		final Matcher matcher = regexPattern.matcher(query);
		return matcher.find();
	}
	
	public static void validateBindParametersCount(Parameters parameters, Object... parameterValues) {
		int numOfParameters = parameters.getNumberOfParameters();

		if (numOfParameters != parameterValues.length) {
			throw new MappingException("Wrong Number of Parameters to bind in Query! Parameter Values size=" + parameterValues.length + ", Method Bind Parameters size=" + numOfParameters);
		}
	}

	/**
	 * Supported types: primitives & core java types (Date, primitive arrays, primitive wrappers) 
	 */
	public static void validateBindParametersTypes(Parameters parameters, Object... parameterValues) {
		final Iterator<Parameter> it = parameters.iterator();

		while(it.hasNext()) {
			final Parameter param = it.next();
			final Class<?> paramType = param.getType();

			if(! (param.isSpecialParameter() || SupportedCoreTypes.isSupported(paramType))) {
				throw(new IllegalArgumentException("Type " + paramType + " not supported as an annotated query parameter!"));
			}
		}
	}

	public static List<String> getQueryPartialFieldNames(String query) {
		List<String> result = new ArrayList<String>();
		String[] vals = query.split(",|\\s");
		boolean isSelect = false;
		for (String val : vals) {
			String trimVal = val.trim();

			if (trimVal.toLowerCase().contains("from")) {
				break;
			}
			if (isSelect && trimVal.length() > 0) {
				result.add(val.trim());
			}
			if (trimVal.toLowerCase().contains("select")) {
				isSelect = true;
			}
		}
		return result;
	}

	public static boolean isCountQuery(String query) {
		return query.toLowerCase().contains("count(");
	}

	static String bindNamedParameters(SimpleDbRepositoryQuery query, Object... parameterValues) {
		final Parameters parameters = query.getQueryMethod().getParameters();
		final String rawQuery = query.getAnnotatedQuery();

		return buildQueryConditionsWithParameters(rawQuery, parameters, parameterValues);
	}

	static String buildQueryConditionsWithParameters(String rawQuery, Parameters params, Object... parameterValues) {

		Map<String, String> parameterPlaceholderValues = buildPlaceholderValues(params, parameterValues);

		return replaceParameterHolders(rawQuery, parameterPlaceholderValues);
	}
	
	static String bindIndexPositionParameters(String queryString, Object... values) {

		final Pattern pattern = Pattern.compile(BIND_PARAMETER_REGEX);
		final StringBuilder builder = new StringBuilder();

		final List<String> dividedQuery = Arrays.asList(queryString.trim().split(pattern.toString()));
		int idx = 0;

		try {
			for (Iterator<String> iterator = dividedQuery.iterator(); iterator.hasNext(); ++idx) {
				builder.append(iterator.next()).append(SINGLE_QUOTE).append(SimpleDBAttributeConverter.encode(values[idx])).append(SINGLE_QUOTE);
			}

		} catch (RuntimeException e) {
			throw new MappingException("Invalid Query! Number of binding parameters in method must match number of query binding parameters", e);
		}

		return builder.toString();
	}

	private static String replaceParameterHolders(String rawQuery, Map<String, String> parameterPlaceholderValues) {
		final StringBuilder completedQueryBuilder = new StringBuilder();

		for (int idx = 0; idx < rawQuery.length(); ++idx) {
			String parameterPlaceholder = null;

			if (rawQuery.charAt(idx) == ':') {
				parameterPlaceholder = readUntilChar(rawQuery, idx, ' ');
			}

			if (parameterPlaceholderValues.containsKey(parameterPlaceholder)) {

				completedQueryBuilder.append(SINGLE_QUOTE).append(parameterPlaceholderValues.get(parameterPlaceholder)).append(SINGLE_QUOTE + " ");
				idx += parameterPlaceholder.length();

			} else {
				completedQueryBuilder.append(rawQuery.charAt(idx));
			}
		}

		return completedQueryBuilder.toString().trim();
	}

	private static Map<String, String> buildPlaceholderValues(Parameters parameters, Object... parameterValues) {
		Map<String, String> map = new LinkedHashMap<String, String>();

		for (Iterator<Parameter> iterator = parameters.iterator(); iterator.hasNext(); ) {
			Parameter eachParameter = iterator.next();
			map.put(eachParameter.getPlaceholder(), SimpleDBAttributeConverter.encode(parameterValues[eachParameter.getIndex()]));
		}
		return map;
	}

	private static String readUntilChar(String rawQuery, int idx, char endChar) {
		StringBuilder buffer = new StringBuilder();

		try {
			for (int i = rawQuery.indexOf(rawQuery.charAt(idx), idx); i <= rawQuery.length() && rawQuery.charAt(i) != endChar; ++i) {
				buffer.append(rawQuery.charAt(i));
			}
		} catch (StringIndexOutOfBoundsException stringOOBound) {
			// Query String is ended
		}
		return buffer.toString();
	}

    private static String escapeQueryAttributes(String rawQuery, String idFieldName) {
    	String escapedQuery = rawQuery.replaceAll(idFieldName, "itemName()");
    	return insertBackticksOnRawParameters(escapedQuery);
    }

	private static String insertBackticksOnRawParameters(String rawQuery) {
		String returnedQuery = rawQuery;
		Pattern pattern = Pattern.compile(PatternConstants.UN_ESCAPED_BACKTICKED_PARAMS.getPattternString());
		Matcher matcher = pattern.matcher(rawQuery);
		
		while(matcher.find()) {
			returnedQuery = returnedQuery.replaceFirst(matcher.group(1), "`" + matcher.group(1) + "`");
		}
		
		if(!insertBackticksOnRawParameters(returnedQuery).isEmpty()) {
			throw new IllegalArgumentException("INVALID Query Parameters!");
		}
		
		return returnedQuery;
	}
}
