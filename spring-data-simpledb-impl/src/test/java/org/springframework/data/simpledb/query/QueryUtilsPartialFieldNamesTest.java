package org.springframework.data.simpledb.query;

import java.util.List;
import org.junit.Test;
import org.springframework.data.simpledb.util.QueryUtils;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class QueryUtilsPartialFieldNamesTest {

    @Test
    public void obtain_partial_field_names_from_query() {
        String query = "select primitiveField, coreField from `testDB.simpleDbUser`";
        List<String> result = QueryUtils.getQueryPartialFieldNames(query);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), is("primitiveField"));
        assertThat(result.get(1), is("coreField"));
    }

    @Test
    public void obtain_partial_field_names_from_query_edge_case() {
        String query = " Select  \tprimitiveField\t,\ncoreField\t from `testDB.simpleDbUser`";
        List<String> result = QueryUtils.getQueryPartialFieldNames(query);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), is("primitiveField"));
        assertThat(result.get(1), is("coreField"));
    }
}
