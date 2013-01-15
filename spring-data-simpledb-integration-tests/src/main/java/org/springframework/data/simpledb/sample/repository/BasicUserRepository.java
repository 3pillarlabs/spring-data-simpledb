package org.springframework.data.simpledb.sample.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.simpledb.sample.domain.User;


public interface BasicUserRepository extends CrudRepository<User, Long> {

}
