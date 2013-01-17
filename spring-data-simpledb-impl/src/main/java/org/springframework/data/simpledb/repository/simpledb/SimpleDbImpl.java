package org.springframework.data.simpledb.repository.simpledb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SimpleDbImpl <T, ID extends Serializable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbImpl.class);

    public T addItem(SimpleDbEntity<T, ID> entity){
        logOperation("Add  ", entity);
        return entity.getItem();
    }

    public T updateItem(SimpleDbEntity<T, ID> entity) {
        logOperation("Update", entity);
        return entity.getItem();
    }

    public void delete(SimpleDbEntity sdbEntity) {
        logOperation("Delete", sdbEntity);
    }

    public T findOne(SimpleDbEntityInformation<T, ?> entityInformation, Serializable id) {
        LOGGER.info("FindOne ItemName \"{}\"\"", id);
        return null;
    }


    private void logOperation(String operation, SimpleDbEntity<T, ID> entity){
        LOGGER.info(operation + " \"{}\" ItemName \"{}\"\"",  entity.getDomain(), entity.getItemName());
    }

    public boolean exists(SimpleDbEntityInformation<T, ?> entityInformation, Serializable id) {
        LOGGER.info("Exists ItemName \"{}\"\"", id);
        return false;
    }

    public List<T> findAll(SimpleDbEntityInformation<T, ?> entityInformation, Iterable<Serializable> ids) {
        LOGGER.info("Find All Domain \"{}\"\"", entityInformation.getDomain());
        return null;
    }
}
