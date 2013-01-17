package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

public interface SimpleDbOperations<T, ID extends Serializable> {
    //TDOO here implement all there is to know about SimpleDB

    public T addItem(SimpleDbEntity<T, ID> entity);

    public T updateItem(SimpleDbEntity<T, ID> entity);

    public void delete(SimpleDbEntity sdbEntity);

    public T findOne(SimpleDbEntityInformation<T, ?> entityInformation, Serializable id);

    public boolean exists(SimpleDbEntityInformation<T, ?> entityInformation, Serializable id);

    public List<T> findAll(SimpleDbEntityInformation<T, ?> entityInformation, Iterable<Serializable> ids);
}
