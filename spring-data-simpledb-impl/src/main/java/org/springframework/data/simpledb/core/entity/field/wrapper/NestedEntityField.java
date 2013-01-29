package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

public class NestedEntityField<T, ID extends Serializable> extends AbstractField<T, ID> {

	private final EntityWrapper<T, ID> wrappedNestedEntity;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	NestedEntityField(Field field, EntityWrapper<T, ID> parent) {
		super(field, parent);
		
		final Object nestedEntityInstance = getValue();
        final SimpleDbEntityInformation entityMetadata = SimpleDbEntityInformationSupport.getMetadata(nestedEntityInstance.getClass());
        wrappedNestedEntity = new EntityWrapper(entityMetadata, nestedEntityInstance);
	}

	@Override
	public Map<String, List<String>> serialize(String prefix) {
		final Map<String, List<String>> result = new HashMap<>();
		
        final String nestedEntityFieldName = getName();
        final String nestedEntityAttributePrefix = prefix.isEmpty() ? nestedEntityFieldName : prefix + "." + nestedEntityFieldName;

        /* recursive call */
        final Map<String, List<String>> serializedNestedEntity = wrappedNestedEntity.toAttributes(nestedEntityAttributePrefix);

        result.putAll(serializedNestedEntity);
		
		return result;
	}

	@Override
	public void deserialize(List<String> value) {
	}

}
