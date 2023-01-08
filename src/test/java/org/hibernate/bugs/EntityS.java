package org.hibernate.bugs;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * A minimal entity
 */
@Entity
public class EntityS {

    @GeneratedValue
    @Id
    public long id;
}
