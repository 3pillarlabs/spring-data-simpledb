package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.util.Assert;

public class MapSimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public MapSimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}

    @Override
    public List<String> serializeValue() {
        Assert.state(true, "Unimplemented MAP serialization");
        return null;
    }

    @Override
    public Object deserializeValue(List<String> value) {
        Assert.state(true, "Unimplemented MAP serialization");
        return null;
    }
}
