# Spring Data SimpleDB #

The primary goal of the [Spring Data](http://www.springsource.org/spring-data) project is to make it easier to build Spring-powered applications that use new data access technologies such as non-relational databases, map-reduce frameworks, and cloud based data services.

[Amazon SimpleDB](http://aws.amazon.com/simpledb) is a highly available and flexible non-relational data store that offloads the work of domainPrefix administration. When using Amazon SimpleDB, you organize your structured data in *domains* within which you can _put data_, _get data_, or _run queries_. Domains consist of *items* which are described by *attribute* name-value pairs.

The Spring Data SimpleDB module aims to provide a familiar and consistent Spring-based programming model for Amazon SimpleDB while retaining domain-specific features and capabilities. Key functional areas of Spring Data SimpleDB are a POJO centric model for interacting with a SimpleDB domains and easily writing a Repository style data access layer.

## Getting Started ##

Clone the Spring Data SimpleDB module into and define it as a dependency in your project's Maven file:

    <dependency>
        <groupId>org.springframework.data.simpledb</groupId>
        <artifactId>spring-data-simpledb</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

First, set up the SimpleDb configuration.

    <bean id="simpleDb" class="org.springframework.data.simpledb.core.SimpleDb">
        <property name="accessID" value="$AWS_ACCESS_ID" />
        <property name="secretKey" value="$AWS_SECRET_KEY"/>
        <property name="domainPrefix" value="$DOMAIN_PREFIX"/>
        <property name="domainManagementPolicy" value="$DOMAIN_MANAGEMENT_POLICY"/>
        <property name="consistentRead" value="$CONSISTENT_READ_VALUE"/>
    </bean>
The following can be configured here:

**SimpleDB access credentials** via __accessID__ and __secretKey__ tags.

**SimpleDB domain prefixes** via __domainPrefix__ tag.

If a value is specified here, each SimpleDB domain name created by the application will be prefixed with this value.

For:

    <property name="domainPrefix" value="testDB"/>
and persisted class has Name "UserJob", the generated SimpleDB domain will be testDB.userJob

**SimpleDB Domain management policies** via __domainManagementPolicy__ tag.

If a value is specified here, at application startup Amazon SimpleDB domains are created/updated accordingly.
$DOMAIN_MANAGEMENT_POLICY possible values:

* **DROP_CREATE**    -  Amazon simple db domains will be dropped and recreated at startup; recommended for testing purposes.
* **UPDATE**            -  Amazon simple db domains will be created only if they are not already existing.
* **NONE**            -  This option implies that all domains are created in simple db manually.

_Default value_: **UPDATE**

**SimpleDB default read behaviour**

If a value is specified here, the default read operations performed to SimpleDB will be performed accordingly.
$CONSISTENT_READ_VALUE possible values:

 * **true** - All operations in CRUDRepository or PaginatingAndSortingRepository will be made *with* consistent reads.
 * **false** - All operations in CRUDRepository or PaginatingAndSortingRepository will be made *without* consistent reads.

If some operations need consistent reads and inconsistent reads at the same time, $CONSISTENT_READ_VALUE should not be declared or set to false, and repository classes should extend SimpleDbPagingAndSortingRepository.

This repository has an additional parameter __readConsistent__ on each repository method.

Next declare a template bean.

    <bean id="simpleDbTemplate" class="org.springframework.data.simpledb.core.SimpleDbTemplate">
           <constructor-arg name="simpleDb" ref="simpleDb" />
    </bean>
SimpleDbTemplate is the central support class for SimpleDb database operations. To declare a template, a reference to SimpleDb configuration bean is needed.
Several templates can be configured for an application.

Next, optionally, configure a spring data repository.

    <simpledb:repositories base-package="org.springframework.data.simpledb.sample.simpledb.repository" />
A repository can contain a __simpledb-template-ref__ tag which specifies the id of the template to be used for this repository.

    <simpledb:repositories base-package="org.springframework.data.simpledb.sample.simpledb.repository" simpledb-template-ref="template"/>
If no simpledb-template-ref is specified, the default value is __simpleDbTemplate__, so a template with this id must be present in the configuration file.

Here is a .xml configuration file containing a SimpleDb configuration bean, a template and a package for repositories:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:jpa="http://www.springframework.org/schema/data/jpa"
       xmlns:simpledb="http://www.springframework.org/schema/data/simpledb"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"

       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/data/jpa
        http://www.springframework.org/schema/data/jpa/spring-jpa.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop-3.2.xsd
        http://www.springframework.org/schema/data/simpledb
        http://www.springframework.org/schema/data/simpledb/spring-simpledb.xsd">
    
        <simpledb:repositories base-package="org.springframework.data.simpledb.sample.simpledb.repository" />
    
        <bean id="simpleDb" class="org.springframework.data.simpledb.core.SimpleDb">
            <property name="accessID" value="$AWS_ACCESS_ID" />
            <property name="secretKey" value="$AWS_SECRET_KEY"/>
            <property name="domainPrefix" value="$DOMAIN_PREFIX"/>
            <property name="domainManagementPolicy" value="$DOMAIN_MANAGEMENT_POLICY"/>
            <property name="consistentRead" value="$CONSISTENT_READ_VALUE"/>
        </bean>
    
        <bean id="simpleDbTemplate" class="org.springframework.data.simpledb.core.SimpleDbTemplate">
            <constructor-arg name="simpleDb" ref="simpleDb" />
        </bean>
    
        <bean class="org.springframework.data.simpledb.sample.simpledb.logging.LoggingConfiguration"/>

    </beans>
    
Next, create and entity to model your domain:

    public class SimpleDBUser {
        @org.springframework.data.annotation.Id
        private String itemName;
        //... additional properties here
    
        public void setItemName(String itemName) {
            this.itemName = itemName;
        }
        public String getItemName() {
            return itemName;
        }
    
        //... additional getters and setters
    }


To specify the attribute holding the SimpleDB **itemName** you can either annotate one of the class attributes with the standard **org.springframework.data.annotation.Id** annotation, or you can simply define an **id** field as part of your domain class.

In addition to that, any attribute needs to have __getter__ and __setter__ in order to be persisted.
If a domain field does not contain getter and/or setter it will **not** be persisted into simpleDB.

Create a repository interface:

    public interface BasicSimpleDbUserRepository extends CrudRepository<SimpleDbUser, String> { }

Write a test client using repository:

    @RunWith(SpringJUnit4TestRunner.class)
    @ContextConfiguration("classpath:your-config-file.xml")
    public class BasicSimpleDbUserRepositoryTest {

        @Autowired 
        BasicSimpleDbUserRepository repository;

        @Test
        public void sampleTestCase() {
            SimpleDbUser user = new SimpleDbUser();
            repository.save(user);
    
            user.setItemName("TestItemName");
    
            user = repository.save(user);
    
            Assert.notNull(repository.findAll());
        }
    }
    
Or write a test client using the SimpleDb template:

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(locations = "classpath:simpledb-configured-template-context.xml")
    public class SimpleDbTemplateTest {
        @Autowired
        private SimpleDbOperations operations;
    
        @Test
        public void save_should_persist_single_item() {
    		String itemName = "FirstItem";
    
    		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
    		operations.createOrUpdate(user);
    		
    		SimpleDbUser foundUser = operations.read(user.getItemName(), user.getClass());
    
    		assertEquals(user.getItemName(), foundUser.getItemName());
    		assertEquals(user, foundUser);
    	}
    }
If only the template is used for working with SimpleDb, no repository should be defined in .xml configuration file. 

### Using custom queries ###
SimpleDb native queries can be run using a custom **@Query** annotation.

Parameters can be bound to queries using  **@Param** annotation or by their position in method's signature if **?** are presents in queries.

Queries may return Single Values or Multiple values. 
Is it possible for instance to select individual fields of a given Entity, by specifying the field name in the select condition.

    @Query(value = "select primitiveField from `testDB.simpleDbUser`")
    Set<Float> primitiveFieldSelect();

Also, whole entities can be returned as in the following example.

    @Query("select * from `testDB.simpleDbUser` where primitiveField = :primitiveField")
    List<SimpleDbUser> customSelectWithNamedParamsQuery(@Param(value="primitiveField") String primitiveField);

To select multiple fields of a given Entity, a query like the one bellow can be used.

    @Query(value = "select coreField from `testDB.simpleDbUser` where itemName()='Item_0'")
    List<List<Object>> selectCoreFields();

Queries are validated against method's returned type. The following query won't be run.

    @Query(value = "select * from `testDB.simpleDbUser`")
    List<String> customSelectAllWrongReturnType();

####@Query annotation ####

Query annotation may have the fallowing parameters:

* value
* select
* where

A query annotation can contain: 

 * only the value parameter 
 * a select parameter
 * a where parameter
 * a select and where parameter. 

Any other combinations are invalid.

A query annotation with a value parameters must contain a valid simple db query.

    @Query(value = "select * from `testDB.simpleDbUser`")

A query with only a select parameter must contain a valid array of fields, and the domain name will be completed using the repository metadata.

    @Query(select = {"sampleAttribute", "sampleList"})
A query with only where parameter must contain a string containing a valid simpleDb where condition. This query will be generated as a "select * from 'domainName' " fallowed by given where condition.

    @Query(where = "sampleAttribute<='3' or sampleName like '%test'")
A query with select and where parameters must contain an array of fields for select parameter and a string representing a correct simpleDb condition for where parameter.

    @Query(select = {"item_id"}, where = "item_id >= `3` and item_id <= `5`")
The field representing the id of the entity will be replace in select and where clauses with itemName() before the query is executed.
    

### Paging ###
We support pagination by extending the **PagingAndSortingRepository** which provides a `Page<T> findAll(Pagealbe pageable)` method. Otherwise, you can also define the findAll method in any repository. The following repository defines the findAll paged query:

    public interface MyRepository extends Repository<SimpleDbUser, String> {
        Page<SimpleDbUser> findAll(Pageable pageable);
    }

Moreover, any custom annotated query can be paginated by simply adding a **Pagealbe** parameter to the query method's signature. The parameter must be placed after the mandatory parameters and the method's return type can be only `Page<T>` or `List<T>`. The following example depicts a few different query methods:

    @Query(value = "select * from `testDB.simpleDbUser` where primitiveField > ?")
    Page<SimpleDbUser> findUsers(float primitiveField, Pageable page);

    @Query(value = "select * from `testDB.simpleDbUser` where primitiveField > ?")
    List<SimpleDbUser> findUsersList(float primitiveField, Pageable page);

## Known Limitations ##

### Serialization limitations

When serializing fields of type List, Set or Map, a json object is created and is stored in database.
This json object contains the actual values and also class information about the serialized field.
Serializing/deserializing an object of type `Map<Object, Object>` is not supported by jackson, so no field of this type will correctly be serialized/deserialized.
(JSON object data structure is a map, a collection of name/value pairs, where the element names must be strings.)
From the reasons mentioned about `Map<String, Object>, Map<Integer, Object>` are *supported*

Amazon SimpleDB can only store values represented as string having a maximum width of 1024 characters. In the current implementation we can easily exceed this length if we serialize a long list, a long string etc. 
To overcome this issue, the attribute values exceeding the length limit are split into **chunks** of maximum 1024 characters. Hence, an attribute **stringKey** having a value of 1030 characters long would be split into two attributes: **stringKey@1** having as value the first 1024 characters and **stringKey@2** having as value the last 6 characters. This mechanism is implemented both when serializing and deserializing the attributes.

### Primitive field conversions ###

The current version supports converting all primitive types but *Character*. More that that, *Float.MIN_VALUE* and *Double.MIN_VALUE* cannot be converted accurately.

### Custom select
Methods annotated with @Query can run custom SimpleDb valid queries.

Since nested object fields are stored in SimpleDb as multiple Items

    public class Company {
        private Customer customer;
    }
    
will be stored in SimpleDb as a list of attributes for each customer field:
- customer.name
- customer.age
etc.

A select like the following won't be a SimpleDB valid select statement and __won't be executed__:

    select customer from `testDb.company`
    
Nevertheless the following will be a valid SimpleDb select statement and __will be executed__:

    select customer.name from `testDb.company`
    
Since we've implemented the **attribute chunks** mechanism for long values, partial queries are not yet supported on chuncked attributes. For example: if attribute **customer.name** would be chunked into **customer.name@1** and **customer.name@2** the following query will return no results

    select customer.name from `testDb.company`

Nevertheless, querying an entire entity would correctly deserialize the chuncked attributes. Hence, the following query will work:

    select * from `testDb.company`

### Paging
Currently, paginating partial annotated queries will return a collection of the queried entity instead of a collection of the queried partial fields. The following example is a valid partial query and each item in the collection will have the itemName and the requested partial fields populated with values. All the other fields will be empty.

    @Query(value = "select primitiveField from `testDB.simpleDbUser`")
    List<SimpleDbUser> pagedPartialQuery(Pageable page);

### Design notes
[Repository Generation sequence diagram](http://www.websequencediagrams.com/?lz=dGl0bGUgUmVwb3NpdG9yeSBHZW5lcmF0aW9uCgpDbGllbnQtLT5TcHJpbmdEYXRhQ29yZToAHQhlIG15IHIANAhpZXMKbm90ZSByaWdodCBvZiBTaW1wbGVEYk9wAEwHczoAARMgaW50ZXJmYWNlXG5oYXMgYSBzaW5nbGUgY29uY3JldGUgAEIFbWVudACBEwVcbigAUQhUZW1wbGF0ZS5jbGFzcylcbnNpbWlsYXIgd2l0aCBIaWJlcm5hdGUAHwgKCgCBOw4tPgCBDhRpbnN0YW50aQAUHQCCKApGYWN0b3J5AH0GADENAIEeCQCBdgopCgpsb29wIEZvciBlYWNoIGphdmEgZmlsZSBpbgCCQQp5IHBhY2thZ2UAgR8SAIJ7D3BhcnMAgzQMTWV0YWRhdGEAggwGAIEOMmdldFRhcmdldACECQooAEwSKQoAgWkfAIQiEgCCJxJJbXBsCmVuZACDBRAtPgCEcgY6AIRODCByZWFkeSBmb3IgQEF1dG93aXJl&s=rose) 


DEV_NOTES: Please use [Dillinger](http://dillinger.io/) when editing this file