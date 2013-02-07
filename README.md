# Spring Data SimpleDB #

The primary goal of the [Spring Data](http://www.springsource.org/spring-data) project is to make it easier to build Spring-powered applications that use new data access technologies such as non-relational databases, map-reduce frameworks, and cloud based data services.

[Amazon SimpleDB](http://aws.amazon.com/simpledb) is a highly available and flexible non-relational data store that offloads the work of domainPrefix administration. When using Amazon SimpleDB, you organize your structured data in *domains* within which you can _put data_, _get data_, or _run queries_. Domains consist of *items* which are described by *attribute* name-value pairs.

The Spring Data SimpleDB module aims to provide a familiar and consistent Spring-based programming model for Amazon SimpleDB while retaining domain-specific features and capabilities. Key functional areas of Spring Data SimpleDB are a POJO centric model for interacting with a SimpleDB domains and easily writing a Repository style data access layer.

## Getting Started ##

Clone the Spring Data SimpleDB module into and define it as a dependency in your project's Maven file:

```xml
<dependency>
    <groupId>org.springframework.data.simpledb</groupId>
    <artifactId>spring-data-simpledb</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Setup Spring Data SimpleDB repository support:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:simpledb="http://www.springframework.org/schema/data/simpledb"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
				http://www.springframework.org/schema/data/simpledb https://raw.github.com/ThreePillarGlobal/spring-data-simpledb/dev/spring-data-simpledb-impl/src/main/resources/META-INF/spring-simpledb.xsd?login=cmester&amp;token=09d23a215953a601e5698b5dbfde6f99">

    <simpledb:repositories base-package="org.springframework.data.simpledb.sample.simpledb.repository" />

    <simpledb:config>
        <simpledb:property name="accessID" value="$AWS_ACCESS_ID" />
        <simpledb:property name="secretKey" value="$AWS_SECRET_KEY"/>
        <simpledb:property name="domainManagementPolicy" value="$DOMAIN_MANAGEMENT_POLICY"/>
        <simpledb:property name="consistentRead" value="$CONSISTENT_READ_VALUE"/>
    </simpledb:config>

</beans>
```

You must use the _config_ tag in order to customize the _$AWS_ACCESS_ID_ and _$AWS_SECRET_KEY_ credentials for access to your Amazon SimpleDB instance.  "$CONSISTENT_READ_VALUE if true ensures consistency. If do not want consistency on all operations only on some then make sure your repository extends SimpleDbPagingAndSortingRepository. In this case the consistentRead property must not be declared or set to false. The *$DOMAIN_MANAGEMENT_POLICY* specifies how to handle the domains you have; possible values are:

* *DROP_CREATE*		-  When a repository instance is created. The corresponding amazon simple db domain will be dropped and recreated; recommended for testing purposes.
* *UPDATE*	        -  When a repository instance is created. The corresponding amazon simple db domain will be created only if it's not already existing.
* *NONE*            -  No simple db domain gets created. This option implies that all domains are created in simple db manually.

Next, create and entity to model your domain:

```java
public class SimpleDBUser {
    @org.springframework.data.annotation.Id
    private String itemName;

    @org.springframework.data.simpledb.annotation.Attributes
    private Map<String, String> atts;

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getItemName() {
        return itemName;
    }
    public void setAtts(Map<String, String> atts) {
        this.atts = atts;
    }
    public Map<String, String> getAtts() {
        return atts;
    }
}
```

The domain name is inferred from the class name. Therefore, the class name is separated into words, using the camel-case convention; each word is further transformed into lower-case letters and separated by "_". This way the domain name inferred from our entity's class name (SimpleDBUser) will be *simple_db_user*.

To specify the attribute holding the *item* name you can either annotate one of the class attributes with the standard *org.springframework.data.annotation.Id* annotation, or you can simply define an *id* field as part of your domain class.

To define the *attributes* (and their values) of the *item*, define Map<String, String> property in your domain class and annotate it with the *org.springframework.data.simpledb.annotation.Attributes* annotation defined in the Spring Data SimpleDB module. Each key in the hash represents an attribute's name and each value associated to a specific key represents the value assigned to that specific attribute.

The domain class _must_ contain a property which can be used as *item name* (either use name _id name_ convention or @Id annotation) and a property which can be used a hash of attributes. NOTE: _In the instances of the domain class, none of these properties can be null or empty!_

The domain class _must_ contain getter and setter method for each of field included in the serialization process. If a domain field does not contain getter and/or setter it will **not** be persisted into simpleDB.

Create a repository interface:

```java
public interface BasicSimpleDbUserRepository extends CrudRepository<SimpleDbUser, String> { }
```

Write a test client:

```java
@RunWith(SpringJUnit4TestRunner.class)
@ContextConfiguration("classpath:your-config-file.xml")
public class BasicSimpleDbUserRepositoryTest {

    @Autowired BasicSimpleDbUserRepository repository;

    @Test
    public void sampleTestCase() {
        SimpleDbUser user = new SimpleDbUser();
        repository.save(user);

        user.setItemName("TestItemName");
        Map<String, String> atts = new LinkedHashMap<>();
        atts.put("name", "John Doe");
        atts.put("age", "27");

        user.setAtts(atts);

        user = repository.save(user);

        Assert.notNull(repository.findAll());
    }
}
```

## Know Limitations ##

### Primitive field conversions ###
The current version supports converting all primitive types but *Character*. More that that, *Float.MIN_VALUE* and *Double.MIN_VALUE* cannot be converted accurately.