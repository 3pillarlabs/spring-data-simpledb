package org.springframework.data.simpledb.core.domain;

/**
 * Use DROP_CREATE - to drop the existing domain and create a new one UPDATE - to create the domain if not existing in
 * simpledb NON - if you prefer to create domains manually
 */
public enum DomainManagementPolicy {
	DROP_CREATE, UPDATE, NONE
}
