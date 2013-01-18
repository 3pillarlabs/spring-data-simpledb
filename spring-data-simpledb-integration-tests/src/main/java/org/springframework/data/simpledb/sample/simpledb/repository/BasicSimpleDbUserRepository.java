package org.springframework.data.simpledb.sample.simpledb.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;


public interface BasicSimpleDbUserRepository extends CrudRepository<SimpleDbUser, String> {

}
