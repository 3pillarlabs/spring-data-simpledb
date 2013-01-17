package org.springframework.data.simpledb.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SimpleDbTemplate<T, ID extends Serializable> implements SimpleDbOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbTemplate.class);
    private final String accessID;
    private final String secretKey;

    public SimpleDbTemplate(String accessID, String secretKey) {
        this.accessID = accessID;
        this.secretKey = secretKey;
    }

    public String getAccessID() {
        return accessID;
    }

    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public void delete(SimpleDbEntity sdbEntity) {
        logOperation("Delete", sdbEntity);
    }

    @Override
    public Object addItem(SimpleDbEntity entity) {
        logOperation("Add  ", entity);
        return entity.getItem();
    }

    @Override
    public Object updateItem(SimpleDbEntity entity) {
        logOperation("Update", entity);
        return entity.getItem();
    }

    @Override
    public Object findOne(SimpleDbEntityInformation entityInformation, Serializable id) {
        LOGGER.info("FindOne ItemName \"{}\"\"", id);
        return null;
    }

    @Override
    public boolean exists(SimpleDbEntityInformation entityInformation, Serializable id) {
        LOGGER.info("Exists ItemName \"{}\"\"", id);
        return false;
    }

    @Override
    public List findAll(SimpleDbEntityInformation entityInformation, Iterable ids) {
        LOGGER.info("Find All Domain \"{}\"\"", entityInformation.getDomain());
        return null;
    }

    private void logOperation(String operation, SimpleDbEntity<T, ID> entity) {
        LOGGER.info(operation + " \"{}\" ItemName \"{}\"\"", entity.getDomain(), entity.getItemName());
    }
}
