package com.mes.techdebt.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A TechDomain.
 */
@Entity
@Table(name = "tech_domain")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class TechDomain implements Serializable {

    private static final long serialVersionUID = 4155986549170453357L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    public Long getId() {
        return this.id;
    }

    public TechDomain id(Long id) {
        this.setId(id);
        return this;
    }

    public TechDomain description(String description) {
        this.setDescription(description);
        return this;
    }

    public TechDomain isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TechDomain)) {
            return false;
        }
        return id != null && id.equals(((TechDomain) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
