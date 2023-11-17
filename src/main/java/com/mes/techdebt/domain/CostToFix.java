package com.mes.techdebt.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A CostToFix.
 */
@Entity
@Table(name = "cost_to_fix")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class CostToFix implements Serializable {

    private static final long serialVersionUID = -6285953761890880383L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = { "investmentCriticality" }, allowSetters = true)
    private Site site;

    @ManyToOne
    private CostRange costRange;

    @ManyToOne
    @JsonIgnoreProperties(value = { "techArea" }, allowSetters = true)
    private Category category;

    public CostToFix id(Long id) {
        this.setId(id);
        return this;
    }

    public CostToFix site(Site site) {
        this.setSite(site);
        return this;
    }

    public CostToFix costRange(CostRange costRange) {
        this.setCostRange(costRange);
        return this;
    }

    public CostToFix category(Category category) {
        this.setCategory(category);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CostToFix)) {
            return false;
        }
        return id != null && id.equals(((CostToFix) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
