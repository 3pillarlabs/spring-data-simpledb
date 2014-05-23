package org.springframework.data.simpledb.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.simpledb.attributeutil.SimpleDBAttributeConverter;
import org.springframework.data.simpledb.reflection.SupportedCoreTypes;
import org.springframework.util.Assert;

/**
 * Main Responsibility of QueryUtils is to work with: <br/>
 * <b>Query Strings</b> <br/>
 * <b>Build Indexed-By Queries</b> <br/>
 * <b>Named Queries</b> <br/>
 */

public final class QueryUtils {

	private static final String BIND_PARAMETER_REGEX = "(\\?)";
	private static final String SINGLE_QUOTE = "'";

	private QueryUtils() {
	}

	public static String bindQueryParameters(SimpleDbQueryMethod queryMethod,
			Object... parameterValues) {
		final String rawQuery = queryMethod.getAnnotatedQuery();

		if (hasNamedParameter(queryMethod) || hasBindParameter(rawQuery)) {
			final Parameters parameters = queryMethod.getParameters();
			return buildQuery(rawQuery, parameters, parameterValues);

		}

		return rawQuery;
	}

	public static boolean hasNamedParameter(SimpleDbQueryMethod queryMethod) {
		for (Parameter param : queryMethod.getParameters()) {
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

	public static void validateBindParametersCount(Parameters parameters,
			Object... parameterValues) {
		int numOfParameters = parameters.getNumberOfParameters();

		if (numOfParameters != parameterValues.length) {
			throw new MappingException(
					"Wrong Number of Parameters to bind in Query! Parameter Values size="
							+ parameterValues.length
							+ ", Method Bind Parameters size="
							+ numOfParameters);
		}
	}

	/**
	 * Supported types: primitives & core java types (Date, primitive arrays,
	 * primitive wrappers)
	 */
	public static void validateBindParametersTypes(Parameters parameters) {
		final Iterator<Parameter> it = parameters.iterator();

		while (it.hasNext()) {
			final Parameter param = it.next();
			final Class<?> paramType = param.getType();

			if (!(param.isSpecialParameter() || SupportedCoreTypes
					.isSupported(paramType) || paramType.isEnum())) {
				throw (new IllegalArgumentException("Type " + paramType
						+ " not supported as an annotated query parameter!"));
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

	static String replaceOneParameterInQuery(String rawQuery,
			Parameter parameter, Object parameterValue) {
		final String bindEndCharacter = "\\b";

		String namedParameter;
		if (parameter.isNamedParameter()) {
			namedParameter = parameter.getPlaceholder() + bindEndCharacter;
		} else {
			namedParameter = "\\?";
		}

		Assert.isTrue(rawQuery.matches("(.*)(" + namedParameter + ")(.*)"));
//		String rawQuery = aRawQuery;

//		if (isInOperation(rawQuery, namedParameter)) {
//			String replacedValue = createInOperatorStatement(parameterValue);
//			rawQuery = rawQuery.replaceFirst(namedParameter, replacedValue);
//		} else {
//			// handle LIKE queries & quoting together
//			String encodedValue = SimpleDBAttributeConverter.encode(parameterValue);
//			StringBuilder namedParamBuilder = new StringBuilder();
//			StringBuilder paramValueBuilder = new StringBuilder();
//			Pattern likeOperatorPattern = Pattern.compile("(%?)" + namedParameter + "(%?)");
//			Matcher m = likeOperatorPattern.matcher(rawQuery);
//			if (m.find()) {
//				paramValueBuilder.append(SINGLE_QUOTE);
//				if (!m.group(1).isEmpty()) {
//					namedParamBuilder.append("%");
//					paramValueBuilder.append("%");
//				}
//				namedParamBuilder.append(namedParameter);
//				paramValueBuilder.append(encodedValue);
//				if (!m.group(2).isEmpty()) {
//					namedParamBuilder.append("%");
//					paramValueBuilder.append("%");
//				}
//				paramValueBuilder.append(SINGLE_QUOTE);
//				rawQuery = rawQuery.replaceFirst(namedParamBuilder.toString(), paramValueBuilder.toString());
//			}
//		}
		return replaceOneParameterInQuery(rawQuery, namedParameter, parameterValue);
	}

	public static String replaceOneParameterInQuery(String rawQuery, 
			String paramPlaceholder, Object paramValue) {

		String replacedQuery = null;
		if (paramValue.getClass().isArray() && isInOperation(rawQuery, paramPlaceholder)) {
			String replacedValue = createInOperatorStatement(paramValue);
			replacedQuery = rawQuery.replaceFirst(paramPlaceholder, replacedValue);
		} else {
			// handle LIKE queries & quoting together
			String encodedValue = SimpleDBAttributeConverter.encode(paramValue);
			StringBuilder namedParamBuilder = new StringBuilder();
			StringBuilder paramValueBuilder = new StringBuilder();
			Pattern likeOperatorPattern = Pattern.compile("(%?)" + paramPlaceholder + "(%?)");
			Matcher m = likeOperatorPattern.matcher(rawQuery);
			if (m.find()) {
				paramValueBuilder.append(SINGLE_QUOTE);
				if (!m.group(1).isEmpty()) {
					namedParamBuilder.append("%");
					paramValueBuilder.append("%");
				}
				namedParamBuilder.append(paramPlaceholder);
				paramValueBuilder.append(encodedValue);
				if (!m.group(2).isEmpty()) {
					namedParamBuilder.append("%");
					paramValueBuilder.append("%");
				}
				paramValueBuilder.append(SINGLE_QUOTE);
				replacedQuery = rawQuery.replaceFirst(
						namedParamBuilder.toString(), paramValueBuilder.toString());
			}
		}
		
		return replacedQuery;
	}
	
	static String buildQuery(final String rawQuery, Parameters parameters,
			Object... parameterValues) {
		String replacedRawQuery = rawQuery;
		for (Iterator<Parameter> iterator = parameters.iterator(); iterator
				.hasNext();) {

			Parameter eachParameter = iterator.next();
			if (Pageable.class.isAssignableFrom(eachParameter.getType())
					|| Sort.class.isAssignableFrom(eachParameter.getType())) {
				continue;
			}

			replacedRawQuery = replaceOneParameterInQuery(replacedRawQuery,
					eachParameter, parameterValues[eachParameter.getIndex()]);
		}

		return replacedRawQuery.trim();
	}

	private static boolean isInOperation(String rawQuery, String parameterName) {
		final String matchingString = "(.*)\\s(in)((\\s)*)" + parameterName
				+ "(.*)";
		return rawQuery.toLowerCase().matches(matchingString);
	}

	private static String createInOperatorStatement(Object parameterValue) {
		List<String> encodedArray = SimpleDBAttributeConverter
				.encodeArray(parameterValue);
		StringBuilder stringBuilder = new StringBuilder("(");
		int numberOfParameters = encodedArray.size() - 1;
		for (int i = 0; i < numberOfParameters; i++) {
			stringBuilder.append("'").append(encodedArray.get(i)).append("',");
		}
		stringBuilder.append("'").append(encodedArray.get(numberOfParameters))
				.append("')");
		return stringBuilder.toString();
	}

	public static String escapeQueryAttributes(String rawQuery,
			String idFieldName) {
		String escapedQuery = rawQuery.replaceAll("\\s" + idFieldName + "\\s",
				" itemName() ");

		if (escapedQuery.endsWith(idFieldName)) {
			escapedQuery = escapedQuery.substring(0, escapedQuery.length()
					- idFieldName.length());
			escapedQuery += "itemName()";
		}

		return escapedQuery;
	}
}
