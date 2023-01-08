package org.hibernate.bugs;

import javax.persistence.*;
import java.io.Serializable;

class LinkPK implements Serializable {
    private long v;
    private long s;
}

/**
 * Link entity represents relation between V and S entities.  It contains an attribute of the relationship.
 */
@Entity
@IdClass(LinkPK.class)
public class Link {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "entityv_id", referencedColumnName = "id")
    public EntityV v;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "entitys_id", referencedColumnName = "id")
    public EntityS s;

    @Column
    public int attribute;
}
