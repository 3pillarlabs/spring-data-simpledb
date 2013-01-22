# Spring Data SimpleDB #

The primary goal of the [Spring Data](http://www.springsource.org/spring-data) project is to make it easier to build Spring-powered applications that use new data access technologies such as non-relational databases, map-reduce frameworks, and cloud based data services. 

[Amazon SimpleDB](http://aws.amazon.com/simpledb) is a highly available and flexible non-relational data store that offloads the work of database administration. When using Amazon SimpleDB, you organize your structured data in *domains* within which you can _put data_, _get data_, or _run queries_. Domains consist of *items* which are described by *attribute* name-value pairs.

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
        <simpledb:property name="accessID" value="AWS Access Key ID" />
        <simpledb:property name="secretKey" value="AWS Secret Access Key"/>
        <simpledb:property name="domainManagementPolicy" value="DOMAIN_MANAGEMENT_POLICY"/>
    </simpledb:config>

</beans>
```

You must use the _config_ tag in order to customize the _accessID_ and _secretKey_ credentials for access to your Amazon SimpleDB instance.  The *DOMAIN_MANAGEMENT_POLICY* specifies how to handle the domains you have; possible values are:

* *DROP_CREATE*		-  to drop the existing domain and create a new one; recommended for testing purposes
* *UPDATE*	            	-  to create the domain if not existing in simpleDB
* *NONE*                      	-  to create domains manually

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

The domain name is inferred from the class name. Therefore, the class name is separated into words, using the camel-case convention; each word is further transformed into lower-case letters and separated by "_". This way the domain name inferred from our entity's class name will be *simple_db_user*.

To specify the attribute holding the *item* name (the elements contained in Simple DB domains) you can either annotate one of the class attributes with the standard *org.springframework.data.annotation.Id* annotation, or you can simply define an *id* field as part of your domain class.

To define the *attributes* (and their values) of the *item*, define Map<String, String> property in your domain class and annotate it with the *org.springframework.data.simpledb.annotation.Attributes* annotation defined in the Spring Data SimpleDB module. Each key in the hash represents an attribute's name and each value associated to a specific key represents the value assigned to that specific attribute.

The domain class _must_ contain a property which can be used as *item name* (either use name _id name_ convention or @Id annotation) and a property which can be used a hash of attributes. NOTE: _In the instances of the domain class, none of these properties can be null or empty!_

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

