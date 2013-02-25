package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.services.simpledb.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.util.Assert;

import com.amazonaws.services.simpledb.AmazonSimpleDB;

/**
 *
 */
public class SimpleDbOperationsImpl<T, ID extends Serializable> implements SimpleDbOperations<T, ID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbOperationsImpl.class);
    private final AmazonSimpleDB sdb;
    private final DomainItemBuilder domainItemBuilder;

    public SimpleDbOperationsImpl(AmazonSimpleDB sdb) {
        this.sdb = sdb;
        domainItemBuilder = new DomainItemBuilder<>();
    }

    @Override
    public Object updateItem(EntityWrapper entity) {
        logOperation("Update", entity);
        Assert.notNull(entity.getDomain(), "Domain name should not be null");
        entity.generateIdIfNotSet();

        Map<String, String> rawAttributes = entity.serialize();
        List<PutAttributesRequest> putAttributesRequests = SimpleDbRequestBuilder.createPutAttributesRequests(entity.getDomain(), entity.getItemName(), rawAttributes);

        for(PutAttributesRequest request: putAttributesRequests){
            sdb.putAttributes(request);
        }

        return entity.getItem();
    }

    @Override
    public void deleteItem(String domainName, String itemName) {
        LOGGER.info("Delete Domain\"{}\" ItemName \"{}\"\"", domainName, itemName);
        Assert.notNull(domainName, "Domain name should not be null");
        Assert.notNull(itemName, "Item name should not be null");
        sdb.deleteAttributes(new DeleteAttributesRequest(domainName, itemName));
    }

    @Override
    public T readItem(SimpleDbEntityInformation<T, ID> entityInformation, ID id, boolean consistentRead) {
        LOGGER.info("Read ItemName \"{}\"\"", id);
        List<ID> ids = new ArrayList<>();
        {
            ids.add(id);
        }
        List<T> results = find(entityInformation, new QueryBuilder(entityInformation).with(ids), consistentRead);
        return results.size() == 1 ? results.get(0) : null;
    }

    @Override
    public List<T> find(SimpleDbEntityInformation<T, ID> entityInformation, QueryBuilder queryBuilder, boolean consistentRead) {
        return find(entityInformation, queryBuilder.toString(), consistentRead);
    }

    @Override
     public List<T> find(SimpleDbEntityInformation<T, ID> entityInformation, String query, boolean consistentRead) {
        LOGGER.info("Find All Domain \"{}\"\" isConsistent=\"{}\"\"", entityInformation.getDomain(), consistentRead);
        final SelectResult selectResult = sdb.select(new SelectRequest(query, consistentRead));
        return domainItemBuilder.populateDomainItems(entityInformation, selectResult);
    }

    @Override
    public long count(SimpleDbEntityInformation entityInformation, boolean consistentRead) {
        LOGGER.info("Count items from domain \"{}\"\" isConsistent=\"{}\"\"", entityInformation.getDomain(), consistentRead);
        return count(new QueryBuilder(entityInformation).withCount().toString(), consistentRead);
    }

    @Override
    public long count(String query, boolean consistentRead){
        LOGGER.info("Count items for query "+query);
        final SelectResult selectResult = sdb.select(new SelectRequest(query, consistentRead));
        for (Item item : selectResult.getItems()) {
            if (item.getName().equals("Domain")) {
                for (Attribute attribute : item.getAttributes()) {
                    if (attribute.getName().equals("Count")) {
                        return Long.parseLong(attribute.getValue());
                    }
                }
            }
        }
        return 0;
    }

    private void logOperation(String operation, EntityWrapper<T, ID> entity) {
        LOGGER.info(operation + " \"{}\" ItemName \"{}\"\"", entity.getDomain(), entity.getItemName());
    }
}
