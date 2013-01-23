package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.Iterator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

public class SelectQueryBuilder<T, ID extends Serializable> {

    private Iterable ids;
    private Pageable pageable;
    SimpleDbEntityInformation<T, ID> entityInformation;

    public SelectQueryBuilder(SimpleDbEntityInformation<T, ID> entityInformation) {
        this.entityInformation = entityInformation;
    }

    public SelectQueryBuilder with(Iterable iterable) {
        this.ids = iterable;
        return this;
    }

    public SelectQueryBuilder with(Sort sort) {
        pageable = new PageableImpl();
        ((PageableImpl) pageable).setSort(sort);
        return this;
    }

    public SelectQueryBuilder with(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }

    @Override
    public String toString() {
        //TODO
        StringBuilder query = new StringBuilder();
        query.append("select * from ").append(entityInformation.getDomain());
        if (ids != null && ids.iterator().hasNext()) {
            Iterator<ID> iterator = ids.iterator();
            query.append(" where ");
            while (iterator.hasNext()) {
                query.append("itemName()='").append(iterator.next().toString()).append("'");
                if (iterator.hasNext()) {
                    query.append(" or ");
                }
            }
        }
        if (pageable != null) {
            Sort sort = pageable.getSort();
            if (sort != null) {
                Iterator<Sort.Order> sortIt = sort.iterator();
                if (sortIt.hasNext()) {
                    query.append(" ");
                }
                while (sortIt.hasNext()) {
                    Sort.Order order = sortIt.next();
                    query.append(order.getProperty()).append(" ").append(order.getDirection().name().toLowerCase());
                }
            }
            if(pageable.getPageSize()>0) {
                query.append(" limit ").append(pageable.getPageSize());
            }
        }
        System.out.println(query.toString());
        return query.toString();
    }

    private static class PageableImpl implements Pageable {

        Sort sort;

        public PageableImpl() {
        }

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
}
