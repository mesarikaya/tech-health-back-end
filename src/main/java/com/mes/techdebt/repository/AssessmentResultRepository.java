package com.mes.techdebt.repository;

import com.mes.techdebt.domain.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the {@link AssessmentResult} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {

    Optional<List<AssessmentResult>> findBySite_NameOrderByUpdateDateDesc(String siteName);

    long countByAssessmentCriteria_Id(Long id);

    long countByAssessmentCriteria_Category_Id(Long id);

    long countByAssessmentCriteria_Category_TechArea_Id(Long id);

    long countByAssessmentCriteria_Category_TechArea_Domain_Id(Long id);

}
