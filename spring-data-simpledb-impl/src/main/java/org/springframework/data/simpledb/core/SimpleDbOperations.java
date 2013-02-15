package org.springframework.data.simpledb.core;

import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.io.Serializable;
import java.util.List;

public interface SimpleDbOperations<T, ID extends Serializable> {

    T updateItem(EntityWrapper<T, ID> entity);

    void deleteItem(String domainName, String itemName);

    T readItem(SimpleDbEntityInformation<T, ID> entityInformation, ID id, boolean consistentRead);

    long count(SimpleDbEntityInformation<T, ?> entityInformation, boolean consistentRead);

    List<T> find(SimpleDbEntityInformation<T, ID> entityInformation, QueryBuilder queryBuilder, boolean consistentRead);

    List<T> find(SimpleDbEntityInformation<T, ID> entityInformation, String query, boolean consistentRead);

    long count(String query, boolean consistentRead);
}
