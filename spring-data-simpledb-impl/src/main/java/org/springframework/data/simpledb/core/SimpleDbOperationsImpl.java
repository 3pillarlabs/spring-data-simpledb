package org.springframework.data.simpledb.core;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.util.Assert;

/**
 *
 */
public class SimpleDbOperationsImpl<T, ID extends Serializable> implements SimpleDbOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbOperationsImpl.class);
    private final DDL ddl;
    private final AmazonSimpleDB sdb;
    private final String accessID;
    private final String secretKey;

    public enum DDL {

        drop_create,
        update,
        nothing
    }

    public SimpleDbOperationsImpl(String accessID, String secretKey) {
        this(accessID, secretKey, DDL.nothing.name());
    }

    public SimpleDbOperationsImpl(final String accessID, final String secretKey, String ddl) {
        Assert.notNull(accessID);
        Assert.notNull(secretKey);
        sdb = new AmazonSimpleDBClient(new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return accessID;
            }

            @Override
            public String getAWSSecretKey() {
                return secretKey;
            }
        });
        this.accessID = accessID;
        this.secretKey = secretKey;
        this.ddl = DDL.valueOf(ddl);
    }

    public String getAccessID() {
        return accessID;
    }

    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public Object addItem(SimpleDbEntity entity) {
        logOperation("Add  ", entity);
        Assert.notNull(entity.getDomain(), "Domain name should not be null");
        Assert.notNull(entity.getItemName(), "Item name should not be null");
        sdb.putAttributes(new PutAttributesRequest(entity.getDomain(), entity.getItemName(), toReplaceableAttributeList(entity.getAttributes(), false)));
        return entity.getItem();
    }

    @Override
    public Object updateItem(SimpleDbEntity entity) {
        logOperation("Update", entity);
        Assert.notNull(entity.getDomain(), "Domain name should not be null");
        Assert.notNull(entity.getItemName(), "Item name should not be null");
        sdb.putAttributes(new PutAttributesRequest(entity.getDomain(), entity.getItemName(), toReplaceableAttributeList(entity.getAttributes(), true)));
        return entity.getItem();
    }

    @Override
    public void delete(SimpleDbEntity entity) {
        logOperation("Delete", entity);
        Assert.notNull(entity.getDomain(), "Domain name should not be null");
        Assert.notNull(entity.getItemName(), "Item name should not be null");
        sdb.deleteAttributes(new DeleteAttributesRequest(entity.getDomain(), entity.getItemName(), toAttributeList(entity.getAttributes())));
    }

    @Override
    public Object findOne(SimpleDbEntityInformation entityInformation, Serializable id) {
        LOGGER.info("FindOne ItemName \"{}\"\"", id);
//        entityInformation.getIdType();
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

    private List<ReplaceableAttribute> toReplaceableAttributeList(Map<String, String> attributes, boolean replace) {
        List<ReplaceableAttribute> result = new ArrayList<>();
        for (Iterator<String> it = attributes.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            result.add(new ReplaceableAttribute(key, attributes.get(key), replace));
        }
        return result;
    }

    private List<Attribute> toAttributeList(Map<String, String> attributes) {
        List<Attribute> result = new ArrayList<>();
        for (Iterator<String> it = attributes.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            result.add(new Attribute(key, attributes.get(key)));
        }
        return result;
    }

    private void logOperation(String operation, SimpleDbEntity<T, ID> entity) {
        LOGGER.info(operation + " \"{}\" ItemName \"{}\"\"", entity.getDomain(), entity.getItemName());
    }
}
