package com.mes.techdebt.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A CostRange.
 */
@Entity
@Table(name = "cost_range")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class CostRange implements Serializable {

    private static final long serialVersionUID = -8624659417985597290L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    public CostRange id(Long id) {
        this.setId(id);
        return this;
    }

    public CostRange description(String description) {
        this.setDescription(description);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CostRange)) {
            return false;
        }
        return id != null && id.equals(((CostRange) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
