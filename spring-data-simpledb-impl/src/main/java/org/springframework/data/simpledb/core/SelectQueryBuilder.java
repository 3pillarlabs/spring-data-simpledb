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
        //TODO change itemName() to ID field from domain object
        //TODO refactor
        //select * from Gigi where Age is not null order by Age desc
        //select * from Gigi where itemName()='Item_01' or itemName()='Item_02'
        //select * from Gigi where (itemName()='Item_01' or itemName()='Item_02') and Age is not null order by Age desc
        StringBuilder query = new StringBuilder();
        query.append("select * from ").append(entityInformation.getDomain());
        if (ids != null && ids.iterator().hasNext()) {
            Iterator<ID> iterator = ids.iterator();
            query.append(" where (");
            while (iterator.hasNext()) {
                query.append("itemName()='").append(iterator.next().toString()).append("'");
                if (iterator.hasNext()) {
                    query.append(" or ");
                }
            }
            query.append(")");
        }
        if (pageable != null) {
            Sort sort = pageable.getSort();
            if (sort != null) {
                Iterator<Sort.Order> sortIt = sort.iterator();
                if (sortIt.hasNext()) {
                    Sort.Order order = sortIt.next();
                    if(query.indexOf("where") > 0 ) {
                        query.append(" and ");
                    } else {
                        query.append(" where ");
                    }
                    query.append(order.getProperty()).append(" is not null ").append(" order by ");
                    query.append(order.getProperty()).append(" ").append(order.getDirection().name().toLowerCase());
                }
                if(sortIt.hasNext()) {
                    throw new IllegalArgumentException("SimpleDb does not support multiple sorting");
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
