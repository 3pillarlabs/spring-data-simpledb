/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springframework.data.simpledb.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.simpledb.domain.SimpleDbUser;

/**
 * 
 * @author fchis
 */
public interface PagingAndSortingUserRepository extends PagingAndSortingRepository<SimpleDbUser, String> {

}
