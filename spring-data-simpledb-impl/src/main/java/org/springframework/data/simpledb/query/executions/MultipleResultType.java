package org.springframework.data.simpledb.query.executions;

/**
 * The following multiple result types can be requested: <br/>
 * <ul>
 *     <li>COLLECTION_OF_DOMAIN_ENTITIES - List&lt;Entity&gt; <br/> as returned type for query  <code> select * from entity</code> </li>
 *     <li>LIST_OF_LIST_OF_OBJECT - List&lt; List &lt; Object &gt; &gt;</li> <br/> as returned type for query <code> select aField, bField from entity</code>
 *     <li>FIELD_OF_TYPE_COLLECTION - Collection &lt; ? &gt; </li> <br/> as returned type for query <code> select collectionField from entity where itemName()="1"</code>
 *     <li>LIST_OF_FIELDS - List &lt; ? &gt; </li> <br/> as returned type for query <code> select aField from entity</code>
 *     <li>SET_OF_FIELDS - Set &lt; ? &gt; </li> <br/> as returned type for query <code> select aField from entity</code>
 * </ul>
 */
public enum MultipleResultType {
    COLLECTION_OF_DOMAIN_ENTITIES,
    LIST_OF_LIST_OF_OBJECT,
    FIELD_OF_TYPE_COLLECTION,
    LIST_OF_FIELDS,
    SET_OF_FIELDS;

}
