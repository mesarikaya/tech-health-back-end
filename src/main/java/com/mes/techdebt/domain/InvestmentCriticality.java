package com.mes.techdebt.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A InvestmentCriticality.
 */
@Entity
@Table(name = "investment_criticality")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class InvestmentCriticality implements Serializable {

    private static final long serialVersionUID = -2757680778266195222L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    public InvestmentCriticality id(Long id) {
        this.setId(id);
        return this;
    }

    public InvestmentCriticality description(String description) {
        this.setDescription(description);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvestmentCriticality)) {
            return false;
        }
        return id != null && id.equals(((InvestmentCriticality) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
