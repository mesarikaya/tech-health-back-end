package com.mes.techdebt.repository;

import com.mes.techdebt.domain.AssessmentCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the {@link AssessmentCriteria} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssessmentCriteriaRepository extends JpaRepository<AssessmentCriteria, Long> {
    Optional<AssessmentCriteria> findByDescription(String description);
}
