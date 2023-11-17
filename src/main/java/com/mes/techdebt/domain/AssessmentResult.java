package com.mes.techdebt.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * A AssessmentResult.
 */
@Entity
@Table(name = "assessment_result")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class AssessmentResult implements Serializable {

    private static final long serialVersionUID = 541257803917232601L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "score")
    private Double score;

    @Column(name = "recommendation_text")
    private String recommendationText;

    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "create_date", updatable = false)
    private Timestamp createDate;

    @Column(name = "created_by")
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "update_date", updatable = true)
    private Timestamp updateDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @ManyToOne
    @JsonIgnoreProperties(value = { "category" }, allowSetters = true)
    private AssessmentCriteria assessmentCriteria;

    @ManyToOne
    @JsonIgnoreProperties(value = { "investmentCriticality" }, allowSetters = true)
    private Site site;

    @ManyToOne
    private RecommendationStatus recommendationStatus;

    public AssessmentResult id(Long id) {
        this.setId(id);
        return this;
    }

    public AssessmentResult score(Double score) {
        this.setScore(score);
        return this;
    }

    public AssessmentResult recommendationText(String recommendationText) {
        this.setRecommendationText(recommendationText);
        return this;
    }

    public AssessmentResult notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public AssessmentResult createDate(Timestamp createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public AssessmentResult createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public AssessmentResult updateDate(Timestamp updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public AssessmentResult updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public AssessmentResult assessmentCriteria(AssessmentCriteria assessmentCriteria) {
        this.setAssessmentCriteria(assessmentCriteria);
        return this;
    }

    public AssessmentResult site(Site site) {
        this.setSite(site);
        return this;
    }

    public AssessmentResult recommendationStatus(RecommendationStatus recommendationStatus) {
        this.setRecommendationStatus(recommendationStatus);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssessmentResult)) {
            return false;
        }
        return id != null && id.equals(((AssessmentResult) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
