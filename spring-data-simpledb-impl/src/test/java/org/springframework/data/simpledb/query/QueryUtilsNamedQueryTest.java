package org.springframework.data.simpledb.query;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;

public class QueryUtilsNamedQueryTest {

    @Test
    public void buildQueryConditionsWithParameters_should_work_with_WHERE_clause() {
        final String expectedQuery = "select * from spring_data where type = 'spring-type'";
        final String rawQuery = "select * from spring_data where type = :type";
        final Parameters parameters = getMockParameters(":type");

        String resultedQuery = QueryUtils.buildQueryConditionsWithParameters(rawQuery, parameters, "spring-type");

        assertThat(resultedQuery, is(expectedQuery));
    }

    @Test
    public void buildQueryConditionsWithParameters_should_return_a_formatted_query() {
        final String expectedQuery = "select * from spring_data where name = 'spring-name' and type = 'spring-type' or location = 'Timisoara'";
        final String rawQuery = "select * from spring_data where name = :name and type = :type or location = :location ";
        final Parameters parameters = getMockParameters(":name", ":type", ":location");

        String resultedQuery = QueryUtils.buildQueryConditionsWithParameters(rawQuery, parameters, "spring-name", "spring-type", "Timisoara");

        assertThat(resultedQuery, is(expectedQuery));
    }

    @Test(expected = MappingException.class)
    public void validateBindParameters_should_fail_if_wrong_number_of_parameters_and_values() {
        QueryUtils.validateBindParameters(getMockParameters(":param_1", ":param_2", ":param_3"), "value1");
    }

    @Test
    public void buildQueryConditionsWithParameters_should_work_with_complex_parameters() {
        final String expectedQuery = "select * from spring_data where name = 'spring-name' and type = 'spring-type'";
        final String rawQuery = "select * from spring_data where name = ::name and type = :";
        final Parameters parameters = getMockParameters("::name", ":");

        String resultedQuery = QueryUtils.buildQueryConditionsWithParameters(rawQuery, parameters, "spring-name", "spring-type");

        assertThat(resultedQuery, is(expectedQuery));
    }


    private Parameter getMockParameter(String placeHolder, Integer idx){
        Parameter mockParameter = Mockito.mock(Parameter.class);

        Mockito.when(mockParameter.getPlaceholder()).thenReturn(placeHolder);
        Mockito.when(mockParameter.isNamedParameter()).thenReturn(Boolean.TRUE);
        Mockito.when(mockParameter.getIndex()).thenReturn(idx);

        return mockParameter;
    }

    private Parameters getMockParameters(String... placeHolders){
        Parameters mockParameters = Mockito.mock(Parameters.class);

        List<Parameter> parameters = new ArrayList<>(placeHolders.length);
        for(int idx = 0; idx < placeHolders.length; ++idx) {
            parameters.add(getMockParameter(placeHolders[idx], idx));
        }

        Mockito.when(mockParameters.iterator()).thenReturn(parameters.iterator());
        Mockito.when(mockParameters.getNumberOfParameters()).thenReturn(parameters.size());

        return mockParameters;
    }
}
