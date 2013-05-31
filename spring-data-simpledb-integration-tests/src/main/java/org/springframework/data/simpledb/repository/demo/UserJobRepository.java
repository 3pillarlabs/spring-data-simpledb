package org.springframework.data.simpledb.repository.demo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.domain.demo.UserJob;

public interface UserJobRepository extends CrudRepository<UserJob, String> {

	@Query(where = "`source.token` = :token")
	public List<UserJob> findBySourceToken(@Param("token") String token);
}
