package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.simpledb.core.domain.DomainManager;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

public interface SimpleDbOperations<T, ID extends Serializable> {
    //TDOO here implement all there is to know about SimpleDB

    T createItem(SimpleDbEntity<T, ID> entity);

    T updateItem(SimpleDbEntity<T, ID> entity);

    void deleteItem(SimpleDbEntity<T, ID> sdbEntity);

    T readItem(SimpleDbEntityInformation<T, ID> entityInformation, ID id);

    long count(SimpleDbEntityInformation<T, ?> entityInformation);

    List<T> find(SimpleDbEntityInformation<T, ID> entityInformation, Iterable<ID> ids, Sort sort, Pageable pageable);
}
