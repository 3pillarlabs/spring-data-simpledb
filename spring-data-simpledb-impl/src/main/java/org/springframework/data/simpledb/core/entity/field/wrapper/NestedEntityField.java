package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

public class NestedEntityField<T, ID extends Serializable> extends AbstractField<T, ID> {

	private EntityWrapper<T, ID> wrappedNestedEntity;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	NestedEntityField(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
		
        final SimpleDbEntityInformation entityMetadata = SimpleDbEntityInformationSupport.getMetadata(getField().getType());
        
        /* if it was already created in createNewInstance */
        if(! isNewParent) {
        	/* recursive call */
        	wrappedNestedEntity = new EntityWrapper(entityMetadata, getValue());
        }
	}

	@Override
	public Map<String, List<String>> serialize(String prefix) {
		final Map<String, List<String>> result = new HashMap<>();
		
        final String nestedEntityFieldName = getName();
        final String nestedEntityAttributePrefix = prefix.isEmpty() ? nestedEntityFieldName : prefix + "." + nestedEntityFieldName;

        /* recursive call */
        final Map<String, List<String>> serializedNestedEntity = wrappedNestedEntity.serialize(nestedEntityAttributePrefix);

        result.putAll(serializedNestedEntity);
		
		return result;
	}

	@Override
	public void deserialize(List<String> value) {
	}
	
	@Override
	public void createInstance() {
		/* instantiation is handled by the EntityWrapper */
		final SimpleDbEntityInformation entityMetadata = SimpleDbEntityInformationSupport.getMetadata(getField().getType());
        wrappedNestedEntity = new EntityWrapper(entityMetadata);
        
        try {
			getField().set(getEntity(), wrappedNestedEntity.getItem());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new MappingException("Could not instantiate object", e);
		}
	}

}
