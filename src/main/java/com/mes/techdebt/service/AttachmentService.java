package com.mes.techdebt.service;

import com.mes.techdebt.service.dto.AttachmentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AttachmentService {

    /**
     * Save a attachment.
     *
     * @param file the multipart file to save.
     * @param siteId,  with related site id
     * @param assessmentCriteriaId with related assessmentCriteriaId
     * @param createdBy with related creator details
     * @return void
     */
    void save(MultipartFile file, Long siteId, Long assessmentCriteriaId, String createdBy) throws IOException;

    /**
     * Save multiple attachments.
     *
     * @param files the multipart files to save.
     * @param siteId,  with related site id
     * @param assessmentCriteriaId with related assessmentCriteriaId
     * @param createdBy with related creator details
     * @return void
     */
    void saveMultiple(MultipartFile[] files, Long siteId, Long assessmentCriteriaId, String createdBy) throws IOException;

    /**
     * Get the "id" assessmentResult.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AttachmentDTO> findOne(Long id);

    /**
     * Get all the attachments.
     *
     * @return the list of entities.
     */
    List<AttachmentDTO> findAll();

    /**
     * Get all the attachments based on siteName.
     *
     * @param siteId the site name of the searched entity.
     * @return the list of entities.
     */
    Optional<List<AttachmentDTO>> findBySiteId(Long siteId);

    /**
     * Get all the attachments based on siteId an AssessmentCriteriaId.
     *
     * @param siteId with related site id.
     * @param assessmentCriteriaId with related assessmentCriteriaId.
     * @return the list of entities.
     */
    Optional<List<AttachmentDTO>> findBySiteIdAndAssessmentCriteriaId(Long siteId, Long assessmentCriteriaId);

    /**
     * Delete the attachment for "id".
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    boolean existsById(Long id);
}
