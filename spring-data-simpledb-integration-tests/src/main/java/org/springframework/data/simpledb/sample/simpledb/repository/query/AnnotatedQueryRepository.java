package org.springframework.data.simpledb.sample.simpledb.repository.query;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.JSONCompatibleClass;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

public interface AnnotatedQueryRepository extends PagingAndSortingRepository<SimpleDbUser, String> {

    @Query(where = "Item_1 = :item1 and primitiveField = :primitiveField")
    List<SimpleDbUser> customSelectWithNamedParamsQuery(@Param(value="primitiveField") String primitiveField, @Param(value="item1") String coreField);

    @Query(where = "coreField = ? and primitiveField = ?")
    List<SimpleDbUser> customSelectWithIndexedParams(String coreField, String primitiveField);

    @Query(select = "coreField", where= "itemName()='Item_0'")
    List<List<Object>> selectCoreFields();

    @Query(select = "objectList", where = "itemName()='Item_0'")
    List<JSONCompatibleClass> partialObjectListSelect();

    @Query(select = "primitiveField")
    Set<Float> primitiveFieldSelect();

    @Query
    List<String> customSelectAllWrongReturnType();

    @Query(where = "itemName()='Item_0' ' ")
    List<SimpleDbUser> malformedQuery();
    
    @Query(where = "itemName = 'Item_0'")
    List<SimpleDbUser> customSelectWithWhereClause();
}
