package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

public interface SimpleDbOperations<T, ID extends Serializable> {
    //TDOO here implement all there is to know about SimpleDB

    T addItem(SimpleDbEntity<T, ID> entity);

    T updateItem(SimpleDbEntity<T, ID> entity);

    void delete(SimpleDbEntity sdbEntity);

    T findOne(SimpleDbEntityInformation<T, ?> entityInformation, Serializable id);

    boolean exists(SimpleDbEntityInformation<T, ?> entityInformation, Serializable id);

    List<T> findAll(SimpleDbEntityInformation<T, ?> entityInformation, Iterable<Serializable> ids);
}
