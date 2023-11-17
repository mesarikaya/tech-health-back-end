package com.mes.techdebt.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A TechArea.
 */
@Entity
@Table(name = "tech_area")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class TechArea implements Serializable {

    private static final long serialVersionUID = -359844002105749021L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne
    private TechDomain domain;

    public TechArea id(Long id) {
        this.setId(id);
        return this;
    }

    public TechArea description(String description) {
        this.setDescription(description);
        return this;
    }

    public TechArea isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public TechArea domain(TechDomain techDomain) {
        this.setDomain(techDomain);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TechArea)) {
            return false;
        }
        return id != null && id.equals(((TechArea) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
