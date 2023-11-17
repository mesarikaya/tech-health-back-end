package com.mes.techdebt.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A Category.
 */
@Entity
@Table(name = "category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class Category implements Serializable {

    private static final long serialVersionUID = 3314384883399713489L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JsonIgnoreProperties(value = { "domain" }, allowSetters = true)
    private TechArea techArea;

    @Column(name = "is_active")
    private Boolean isActive;

    public Category id(Long id) {
        this.setId(id);
        return this;
    }

    public Category description(String description) {
        this.setDescription(description);
        return this;
    }

    public Category isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public Category techArea(TechArea techArea) {
        this.setTechArea(techArea);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return id != null && id.equals(((Category) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
