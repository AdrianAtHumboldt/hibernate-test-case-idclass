package org.hibernate.bugs;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * An entity that references a link between S and V.
 */
@Entity
public class EntityX {

    @GeneratedValue
    @Id
    public long id;

    @ManyToOne
    public Link link;
}
