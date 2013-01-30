package org.springframework.data.simpledb.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.io.Serializable;
import java.util.Iterator;

public class QueryBuilder<T, ID extends Serializable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryBuilder.class);

    private SimpleDbEntityInformation<T, ID> entityInformation;
    private StringBuilder query;

    public QueryBuilder(SimpleDbEntityInformation<T, ID> entityInformation) {
    	query = new StringBuilder();
        this.entityInformation = entityInformation;
        query.append("select * from ").append(quote(entityInformation.getDomain()));
    }

    public QueryBuilder withCount() {
    	query = new StringBuilder();
    	
        //replace select statement
        query.append("select count(*) from ").append(quote(entityInformation.getDomain()));
        return this;
    }

    public QueryBuilder with(Iterable iterable) {
        Iterator<ID> iterator = iterable.iterator();
        appendWhereOrEndClause(query);

        query.append("(");
        while (iterator.hasNext()) {
            query.append("itemName()='").append(iterator.next().toString()).append("'");
            if (iterator.hasNext()) {
                query.append(" or ");
            }
        }
        query.append(")");
        return this;
    }

    public QueryBuilder with(Sort sort) {
        Pageable pageable = new PageableImpl();
        ((PageableImpl) pageable).setSort(sort);
        return with(pageable);
    }

    public QueryBuilder with(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (sort != null) {
            Iterator<Sort.Order> sortIt = sort.iterator();
            if (sortIt.hasNext()) {
                Sort.Order order = sortIt.next();
                appendWhereOrEndClause(query);
                query.append(order.getProperty()).append(" is not null order by ");
                query.append(order.getProperty()).append(" ").append(order.getDirection().name().toLowerCase());
            }
            if (sortIt.hasNext()) {
                throw new IllegalArgumentException("SimpleDb does not support multiple sorting");
            }
        }
        if (pageable.getPageSize() > 0) {
            query.append(" limit ").append(pageable.getPageSize());
        }

        return this;
    }

    @Override
    public String toString() {
        //TODO change itemName() to ID field from domain object
        String result = query.toString();
        LOGGER.debug("Created query: {}", result);
        return result;
    }


    private static class PageableImpl implements Pageable {

        private Sort sort;

        @Override
        public int getPageNumber() {
            return -1;
        }

        @Override
        public int getPageSize() {
            return -1;
        }

        @Override
        public int getOffset() {
            return -1;
        }

        @Override
        public Sort getSort() {
            return sort;
        }

        public void setSort(Sort sort) {
            this.sort = sort;
        }
    }

    private void appendWhereOrEndClause(StringBuilder query) {
        if (query.indexOf("where") > 0) {
            query.append(" and ");
        } else {
            query.append(" where ");
        }
    }

    private String quote(String simpleDbName) {
        return "`" + simpleDbName + "`";
    }

}
