package org.springframework.data.simpledb.repository.demo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.simpledb.domain.demo.UserJob;

public interface UserJobRepository extends CrudRepository<UserJob, String> {
}
