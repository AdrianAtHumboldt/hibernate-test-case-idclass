# Two JPA Regressions in Hibernate 5 ##

Both test cases rely on a join table with attributes, and a reference from an entity to a specific join.
The test cases pass with Hibernate 5.3.27, but fail for later versions.

### Fetch By Entity ###

This case uses the query `select x from EntityX x where x.link.attribute = ?1 and x.link.s = ?2`.  
In working versions parameter 2 is of type `EntityS`, but failing versions expect a parameter of type `Long`:

```
java.lang.IllegalArgumentException: Parameter value [org.hibernate.bugs.EntityS@2d22d3b1] did not match expected type [java.lang.Long (n/a)]
	at org.hibernate.query.spi.QueryParameterBindingValidator.validate(QueryParameterBindingValidator.java:54)
	at org.hibernate.query.spi.QueryParameterBindingValidator.validate(QueryParameterBindingValidator.java:27)
	at org.hibernate.query.internal.QueryParameterBindingImpl.validate(QueryParameterBindingImpl.java:90)
	at org.hibernate.query.internal.QueryParameterBindingImpl.setBindValue(QueryParameterBindingImpl.java:55)
	at org.hibernate.query.internal.AbstractProducedQuery.setParameter(AbstractProducedQuery.java:519)
	at org.hibernate.query.internal.AbstractProducedQuery.setParameter(AbstractProducedQuery.java:122)
	at org.hibernate.bugs.JPAUnitTestCase.lookupByEntityTest(JPAUnitTestCase.java:65)
```

### Fetch By Id ###

Coding an explicit fetch by the ID field causes Hibernate to generate incorrect SQL (tested with H2 and PostgreSQL).
Given the query `select x from EntityX x where x.link.attribute = ?1 and x.link.s.id = ?2` Hibernate 5.6
generates the incorrect SQL here:

```
    select
        entityx0_.id as id1_2_,
        entityx0_.link_entitys_id as link_ent2_2_,
        entityx0_.link_entityv_id as link_ent3_2_ 
    from
        EntityX entityx0_ cross 
    join
        Link link1_ 
    where
        entityx0_.link_entitys_id=link1_.entitys_id 
        and entityx0_.link_entityv_id=link1_.entityv_id 
        and link1_.attribute=? 
        and entityx0_.id=?
```

Hibernate 5.3 generates the correct SQL.  The difference is in the final line, where the comparison is to 
`link1_.entitys_id` rather than `entityx0_.id`.

```
    select
        entityx0_.id as id1_2_,
        entityx0_.link_entitys_id as link_ent2_2_,
        entityx0_.link_entityv_id as link_ent3_2_ 
    from
        EntityX entityx0_ cross 
    join
        Link link1_ 
    where
        entityx0_.link_entitys_id=link1_.entitys_id 
        and entityx0_.link_entityv_id=link1_.entityv_id 
        and link1_.attribute=? 
        and link1_.entitys_id=?
```