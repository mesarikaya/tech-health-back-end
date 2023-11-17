package com.mes.techdebt.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A RecommendationStatus.
 */
@Entity
@Table(name = "recommendation_status")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class RecommendationStatus implements Serializable {

    private static final long serialVersionUID = -691699943026848517L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    public RecommendationStatus id(Long id) {
        this.setId(id);
        return this;
    }

    public RecommendationStatus description(String description) {
        this.setDescription(description);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecommendationStatus)) {
            return false;
        }
        return id != null && id.equals(((RecommendationStatus) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
