package org.springframework.data.simpledb.sample.jpa.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.simpledb.sample.jpa.domain.User;


public interface BasicUserRepository extends CrudRepository<User, Long> {

}
