package org.springframework.data.simpledb.exception;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;

import com.amazonaws.services.simpledb.model.DuplicateItemNameException;

public class SimpleDbExceptionTranslatorTest {

    private final SimpleDbExceptionTranslator translator = SimpleDbExceptionTranslator.getTranslatorInstance();

    @Test
    public void translateExceptionIfPossible_should_translate_DuplicateItemNameException_into_DuplicateKeyException() {
        DuplicateItemNameException duplicateItemException = new DuplicateItemNameException("Duplicate Item");

        assertThat(duplicateItemException, is(notNullValue()));
        assertThat(duplicateItemException.getLocalizedMessage(), is("Duplicate Item"));

        DataAccessException dataAccessException = translator.translateExceptionIfPossible(duplicateItemException);
        assertThat(dataAccessException, is(instanceOf(DuplicateKeyException.class)));
    }
}
