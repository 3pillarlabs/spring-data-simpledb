package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

public interface SimpleDbOperations<T, ID extends Serializable> {

    T updateItem(EntityWrapper<T, ID> entity);

    void deleteItem(String domainName, String itemName);

    T readItem(SimpleDbEntityInformation<T, ID> entityInformation, ID id, boolean consistentRead);

    long count(SimpleDbEntityInformation<T, ?> entityInformation, boolean consistentRead);

    List<T> find(SimpleDbEntityInformation<T, ID> entityInformation, QueryBuilder queryBuilder, boolean consistentRead);

    List<T> find(SimpleDbEntityInformation<T, ID> entityInformation, String query, boolean consistentRead);

    long count(String query, boolean consistentRead);
    
    Page<T> executePagedQuery(SimpleDbEntityInformation<T, ID> entityInformation, String query, Pageable pageable, boolean consistentRead);
}
