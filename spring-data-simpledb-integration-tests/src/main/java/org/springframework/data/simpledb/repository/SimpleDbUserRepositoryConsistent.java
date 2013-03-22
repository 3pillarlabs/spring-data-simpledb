package org.springframework.data.simpledb.repository;

import org.springframework.data.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.repository.SimpleDbPagingAndSortingRepository;

public interface SimpleDbUserRepositoryConsistent extends SimpleDbPagingAndSortingRepository<SimpleDbUser, String> {

}
