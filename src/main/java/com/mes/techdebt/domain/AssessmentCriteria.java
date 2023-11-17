package com.mes.techdebt.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * An AssessmentCriteria.
 */
@Entity
@Table(name = "assessment_criteria")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class AssessmentCriteria implements Serializable {

    private static final long serialVersionUID = -1539353069526653523L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "tech_standard_description")
    private String techStandardDescription;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne
    @JsonIgnoreProperties(value = { "techArea" }, allowSetters = true)
    private Category category;

    public AssessmentCriteria id(Long id) {
        this.setId(id);
        return this;
    }

    public AssessmentCriteria description(String description) {
        this.setDescription(description);
        return this;
    }

    public AssessmentCriteria isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public AssessmentCriteria category(Category category) {
        this.setCategory(category);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssessmentCriteria)) {
            return false;
        }
        return id != null && id.equals(((AssessmentCriteria) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
