package com.mes.techdebt.repository;

import com.mes.techdebt.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the {@link Attachment} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Optional<List<Attachment>> findBySite_Id(Long siteId);

    Optional<List<Attachment>> findBySite_IdAndAssessmentCriteria_Id(Long siteId, Long assessmentCriteriaId);
}
