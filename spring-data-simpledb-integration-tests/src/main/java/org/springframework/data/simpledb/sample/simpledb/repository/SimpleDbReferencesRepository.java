package org.springframework.data.simpledb.sample.simpledb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbReferences;

public interface SimpleDbReferencesRepository extends CrudRepository<SimpleDbReferences, String> {
}
