package org.springframework.data.simpledb.repository.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.simpledb.repository.support.SimpleDbRepositoryFactoryBean;

import java.lang.annotation.*;

/**
 * Annotation to activate SimpleDB repositories. If no base package is configured through either {@link #value()},
 * {@link #basePackages()} or {@link #basePackageClasses()} it will trigger scanning of the package of annotated class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(SimpleDBRepositoriesRegistrar.class)
public @interface EnableSimpleDBRepositories {


    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation declarations e.g.:
     * {@code @EnableSimpleDBRepositories("org.my.pkg")} instead of {@code @EnableSimpleDBRepositories(basePackages="org.my.pkg")}.
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated components. {@link #value()} is an alias for (and mutually exclusive with) this
     * attribute. Use {@link #basePackageClasses()} for a type-safe alternative to String-based package names.
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan for annotated components. The
     * package of each class specified will be scanned. Consider creating a special no-op marker class or interface in
     * each package that serves no purpose other than being referenced by this attribute.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Specifies which types are eligible for component scanning. Further narrows the set of candidate components from
     * everything in {@link #basePackages()} to everything in the base packages that matches the given filter or filters.
     */
    ComponentScan.Filter[] includeFilters() default {};

    /**
     * Specifies which types are not eligible for component scanning.
     */
    ComponentScan.Filter[] excludeFilters() default {};

    /**
     * Returns the postfix to be used when looking up custom repository implementations. Defaults to {@literal Impl}. So
     * for a repository named {@code PersonRepository} the corresponding implementation class will be looked up scanning
     * for {@code PersonRepositoryImpl}.
     *
     * @return
     */
    String repositoryImplementationPostfix() default "";



    /**
     * Configures the location of where to find the Spring Data named queries properties file. Will default to
     * {@code META-INFO/simple-db-named-queries.properties}.
     *
     * @return
     */
    String namedQueriesLocation() default "";


    /**
     * Returns the key of the {@link org.springframework.data.repository.query.QueryLookupStrategy} to be used for lookup queries for query methods. Defaults to
     * {@link org.springframework.data.repository.query.QueryLookupStrategy.Key#CREATE_IF_NOT_FOUND}.
     *
     * @return
     */
    QueryLookupStrategy.Key queryLookupStrategy() default QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;

    /**
     * Returns the {@link org.springframework.beans.factory.FactoryBean} class to be used for each repository instance. Defaults to
     * {@link org.springframework.data.simpledb.repository.support.SimpleDbRepositoryFactoryBean}.
     *
     * @return
     */
    Class<?> repositoryFactoryBeanClass() default SimpleDbRepositoryFactoryBean.class;

    /**
     * Configures the name of the {@link org.springframework.data.simpledb.core.SimpleDBTemplate} bean to be used with the repositories detected.
     *
     * @return
     */
    String simpledbTemplateRef() default "simpleDBTemplate";
//
//    /**
//     * Whether to automatically create indexes for query methods defined in the repository interface.
//     *
//     * @return
//     */
//    boolean createIndexesForQueryMethods() default false;
}
