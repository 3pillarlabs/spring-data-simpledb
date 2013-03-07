package org.springframework.data.simpledb.sample.simpledb.repository;

import org.springframework.data.simpledb.repository.SimpleDbPagingAndSortingRepository;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

public interface SimpleDbUserRepositoryConsistent extends SimpleDbPagingAndSortingRepository<SimpleDbUser, String> {

}
