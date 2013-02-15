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


Setup Spring Data SimpleDB repository support:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:simpledb="http://www.springframework.org/schema/data/simpledb"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        https://raw.github.com/ThreePillarGlobal/spring-data-simpledb/dev/spring-data-simpledb-impl/src/main/resources/META-INF/spring-simpledb.xsd?login=cmester&amp;token=0e3a2a9b21a0daa3044b09c3ecdd59d4">
    
        <simpledb:repositories base-package="org.springframework.data.simpledb.sample.simpledb.repository" />
    
        <simpledb:config>
            <simpledb:property name="accessID" value="$AWS_ACCESS_ID" />
            <simpledb:property name="secretKey" value="$AWS_SECRET_KEY"/>
            <simpledb:property name="domainPrefix" value="$DOMAIN_PREFIX"/>
            <simpledb:property name="domainManagementPolicy" value="$DOMAIN_MANAGEMENT_POLICY"/>
            <simpledb:property name="consistentRead" value="$CONSISTENT_READ_VALUE"/>
        </simpledb:config>
    
    </beans>

For SimpleDB specific configurations,  the __config__ tag must be used.

The following can be configured here:

**SimpleDB access credentials** via __accessID__ and __secretKey__ tags.

**SimpleDB domain prefixes** via __domainPrefix__ tag.

If a value is specified here each SimpleDB domain name created by the application will be prefixed with this value.

Ex:
<simpledb:property name="domainPrefix" value="testDB"/>
Persisted class has Name "UserJob"

The generated SimpleDB domain will be testDB.userJob

**SimpleDB Domain management policies** via __domainManagementPolicy__ tag.

If a value is specified here, at application startup Amazon SimpleDB domains are created/updated accordingly.
$DOMAIN_MANAGEMENT_POLICY possible values:

* **DROP_CREATE**	-  Amazon simple db domains will be dropped and recreated at startup; recommended for testing purposes.
* **UPDATE**	        -  Amazon simple db domains will be created only if they are not already existing.
* **NONE**            -  This option implies that all domains are created in simple db manually.

_Default value_: **UPDATE**

**SimpleDB default read behaviour**

If a value is specified here, the default read operations performed to SimpleDB will be performed accordingly.
$CONSISTENT_READ_VALUE possible values:

 * **true** - All operations in CRUDRepository or PaginatingAndSortingRepository will be made *with* consistent reads.
 * **false** - All operations in CRUDRepository or PaginatingAndSortingRepository will be made *without* consistent reads.

If some operations need consistent reads and inconsistent reads at the same time, $CONSISTENT_READ_VALUE should not be declared or set to false, and repository classes should extend SimpleDbPagingAndSortingRepository.

This repository has an additional parameter __readConsistent__ on each repository method.

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
    


Write a test client:

    @RunWith(SpringJUnit4TestRunner.class)
    @ContextConfiguration("classpath:your-config-file.xml")
    public class BasicSimpleDbUserRepositoryTest {

        @Autowired BasicSimpleDbUserRepository repository;

        @Test
        public void sampleTestCase() {
            SimpleDbUser user = new SimpleDbUser();
            repository.save(user);
    
            user.setItemName("TestItemName");
    
            user = repository.save(user);
    
            Assert.notNull(repository.findAll());
        }
    }


## Known Limitations ##

### Serialization limitations

When serializing fields of type List, Set or Map, a json object is created and is stored in database.
This json object contains the actual values and also class information about the serialized field.
Serializing/deserializing an object of type Map<Object, Object> is not supported by jackson, so no field of this type will correctly be serialized/deserialized.
(JSON object data structure is a map, a collection of name/value pairs, where the element names must be strings.)
From the reasons mentioned about Map<String, Object>, Map<Integer, Object> are *supported*


### Primitive field conversions ###

The current version supports converting all primitive types but *Character*. More that that, *Float.MIN_VALUE* and *Double.MIN_VALUE* cannot be converted accurately.

### Custom select
Methods annotated with @Query can run custom SimpleDb valid queries.

Since nested object fields are stored in SimpleDb as multiple Items

    public class Company {
        private Customer customer;
    }
    will be stored in SimpleDb as a list of attributes for each customer field:
    customer.name
    customer.age
    ...

A select like the following won't be a SimpleDB valid select statement and __won't be executed__:

    select customer from `testDb.company`
    
Nevertheless the following will be a valid SimpleDb select statement and __will be executed__:

    select customer.name from `testDb.company`

DEV_NOTES: Please use http://dillinger.io/ when editing this file