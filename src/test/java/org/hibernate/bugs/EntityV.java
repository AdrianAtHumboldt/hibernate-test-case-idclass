package org.hibernate.bugs;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * A minimal entity
 */
@Entity
public class EntityV {

    @GeneratedValue
    @Id
    public long id;
}
