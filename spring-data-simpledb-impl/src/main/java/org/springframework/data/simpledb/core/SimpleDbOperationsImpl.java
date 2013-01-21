package org.springframework.data.simpledb.core;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.simpledb.core.domain.DomainManager;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

/**
 *
 */
public class SimpleDbOperationsImpl<T, ID extends Serializable> implements SimpleDbOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbOperationsImpl.class);
    private final AmazonSimpleDB sdb;
    private final DomainManager domainManager;


    public SimpleDbOperationsImpl(final String accessID, final String secretKey, String domainManagementPolicy) {
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

        this.domainManager = new DomainManager(sdb, domainManagementPolicy);
    }

    @Override
    public DomainManager getDomainManager() {
        return domainManager;
    }

    @Override
    public Object createItem(SimpleDbEntity entity) {
        logOperation("Create  ", entity);
        Assert.notNull(entity.getDomain(), "Domain name should not be null");
        Assert.notNull(entity.getItemName(), "Item name should not be null");
        Assert.notNull(entity.getAttributes(), "Attributes should not be null");
        sdb.putAttributes(new PutAttributesRequest(entity.getDomain(), entity.getItemName(), toReplaceableAttributeList(entity.getAttributes(), false)));
        return entity.getItem();
    }

    @Override
    public Object updateItem(SimpleDbEntity entity) {
        logOperation("Update", entity);
        Assert.notNull(entity.getDomain(), "Domain name should not be null");
        Assert.notNull(entity.getItemName(), "Item name should not be null");
        Assert.notNull(entity.getAttributes(), "Attributes should not be null");
        sdb.putAttributes(new PutAttributesRequest(entity.getDomain(), entity.getItemName(), toReplaceableAttributeList(entity.getAttributes(), true)));
        return entity.getItem();
    }

    @Override
    public void deleteItem(SimpleDbEntity entity) {
        logOperation("Delete", entity);
        Assert.notNull(entity.getDomain(), "Domain name should not be null");
        Assert.notNull(entity.getItemName(), "Item name should not be null");
        sdb.deleteAttributes(new DeleteAttributesRequest(entity.getDomain(), entity.getItemName(), toAttributeList(entity.getAttributes())));
    }

    @Override
    public Object readItem(SimpleDbEntityInformation entityInformation, Serializable id) {
        LOGGER.info("Read ItemName \"{}\"\"", id);
//        entityInformation.getIdType();
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

    @SuppressWarnings("unchecked")
	@Override
    public List find(SimpleDbEntityInformation entityInformation, Iterable ids, Sort sort, Pageable pageable) {
        LOGGER.info("Find where domain \"{}\"\"", entityInformation.getDomain());
        final List<T> allItems = new ArrayList<>();

        final SelectRequest selectRequest = new SelectRequest("select * from " + entityInformation.getDomain());

        sdb.select(selectRequest);
        final SelectResult selectResult = sdb.select(selectRequest);

        for(Item item: selectResult.getItems()) {
        	try {
				final T domainItem = (T)entityInformation.getJavaType().newInstance();
				final Field idField = domainItem.getClass().getDeclaredField(entityInformation.getItemNameFieldName(domainItem));
				idField.setAccessible(true);
				idField.set(domainItem, item.getName());

				final Map<String, String> attributes = new HashMap<String, String>();
				for(Attribute attr: item.getAttributes()) {
					attributes.put(attr.getName(), attr.getValue());
				}

				final Field attributesField = domainItem.getClass().getDeclaredField(entityInformation.getAttributesFieldName(domainItem));
				attributesField.setAccessible(true);
				attributesField.set(domainItem, attributes);

				allItems.add(domainItem);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
        }

        return allItems;
    }

    @Override
    public long count(SimpleDbEntityInformation entityInformation) {
        LOGGER.info("Count items from domain \"{}\"\"", entityInformation.getDomain());
        final SelectResult selectResult  = sdb.select(new SelectRequest("select count(*) from "+entityInformation.getDomain()));
        for(Item item: selectResult.getItems()) {
            if(item.getName().equals("Domain")) {
                for(Attribute attribute: item.getAttributes()) {
                    if(attribute.getName().equals("Count")) {
                        return Long.parseLong(attribute.getValue());
                    }
                }
            }
        }
        return 0;
    }

}
