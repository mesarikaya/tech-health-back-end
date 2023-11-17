package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.service.AssessmentCriteriaService;
import com.mes.techdebt.service.AttachmentService;
import com.mes.techdebt.service.SiteService;
import com.mes.techdebt.service.dto.AttachmentDTO;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.techdebt.domain.AssessmentResult;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.web.util.HeaderUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link AssessmentResult}.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Read') or " +
        "hasAuthority('APPROLE_TechHealth_User_Write') or " +
        "hasAuthority('APPROLE_TechHealth_User_Admin')")
public class AttachmentController {

    private static final String ENTITY_NAME = "attachment";
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private ObjectMapper objectMapper;

    private final AttachmentService attachmentService;
    private final SiteService siteService;
    private final AssessmentCriteriaService assessmentCriteriaService;

    /**
     * {@code GET  /attachments} : get all the attachment.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attachment in body.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @GetMapping(path="/attachments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AttachmentDTO>> getAllAttachments() {
        log.debug("REST request to get a page of Attachment");
        List<AttachmentDTO> attachmentDTOs = attachmentService.findAll();
        return ResponseEntity
                .ok()
                .body(attachmentDTOs);
    }
	
    /**
     * {@code GET  /attachments/:id} : get the "id" attachment.
     *
     * @param id the id of the attachmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attachmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/attachments/{id}")
    public ResponseEntity<byte[]> getAttachmentById(@PathVariable Long id) {
        log.debug("REST request to get Attachment: {}", id);
        Optional<AttachmentDTO> attachmentDTOOptional = attachmentService.findOne(id);
        if (!attachmentDTOOptional.isPresent()) {
            return ResponseEntity.notFound()
                    .build();
        }

        AttachmentDTO attachmentDTO = attachmentDTOOptional.get();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachmentDTO.getFileName() + "\"")
                .contentType(MediaType.valueOf(attachmentDTO.getFileType()))
                .body(attachmentDTO.getData());
    }

    /**
     * {@code GET  /attachments/site/:id} : get the site "id" of attachment.
     *
     * @param siteId the siteId of the attachmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attachmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/attachments/site/{siteId}")
    public ResponseEntity<List<AttachmentDTO>> getAttachmentBySiteId(@PathVariable Long siteId) {
        log.debug("REST request to get Attachment siteId: {}", siteId);
        Optional<List<AttachmentDTO>> attachmentDTOOptional = attachmentService.findBySiteId(siteId);
        if (!attachmentDTOOptional.isPresent()) {
            return ResponseEntity.notFound()
                    .build();
        }

        List<AttachmentDTO> attachmentDTOs = attachmentDTOOptional.get();
        return ResponseEntity
                .ok()
                .body(attachmentDTOs);
    }

    /**
     * {@code GET  /attachments/site/:id} : get the site "id" of attachment.
     *
     * @param siteId the siteId of the attachmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attachmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/attachments/site/{siteId}/criteria/{assessmentCriteriaId}")
    public ResponseEntity<List<AttachmentDTO>> getAttachmentBySiteIdAndCriteriaId(@PathVariable Long siteId, @PathVariable Long assessmentCriteriaId) {
        log.debug("REST request to get Attachment siteid: {}, assessmentCriteriaId: {}", siteId, assessmentCriteriaId);
        Optional<List<AttachmentDTO>> attachmentDTOOptional = attachmentService.findBySiteIdAndAssessmentCriteriaId(siteId, assessmentCriteriaId);
        if (!attachmentDTOOptional.isPresent()) {
            return ResponseEntity.notFound()
                    .build();
        }

        List<AttachmentDTO> attachmentDTOs = attachmentDTOOptional.get();
        return ResponseEntity
                .ok()
                .body(attachmentDTOs);
    }


    /**
     * {@code POST  /attachments} : Create a new attachment.
     *
     * @param file the multipart file to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new multipart file,
     * or with status {@code 400 (Bad Request)} if the assessmentResult
     * has already an ID.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/attachments", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadAttachment(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("siteId") Long siteId,
                                                   @RequestParam(value = "assessmentCriteriaId", required = false) Long assessmentCriteriaId,
                                                   @RequestParam("createdBy") String createdBy) {

        log.debug("REST request to save Attachment filename: {} " +
                        "for fileContentType: {}, " +
                        "for fileSize: {}, " +
                        "for siteID: {}, " +
                        "assessmentCriteriaId: {},  " +
                        "createdBy: {}",
                file.getOriginalFilename(), file.getContentType(), file.getSize(),
                siteId, assessmentCriteriaId, createdBy);

        if (!siteService.existsById(siteId)) {
            throw new BadRequestAlertException("Entity related site not found", ENTITY_NAME, "id not found");
        }

        try {
            attachmentService.save(file, siteId, assessmentCriteriaId, createdBy);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("File uploaded successfully: %s", file.getOriginalFilename()));
        } catch (Exception e) {
            log.debug("Error with upload: {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Could not upload the file: %s!", file.getOriginalFilename()));
        }
    }

    /**
     * {@code POST  /attachments/multiple} : Create a new attachment.
     *
     * @param files the list of multipart files to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new multipart files list,
     * or with status {@code 400 (Bad Request)} if the assessmentResult
     * has already an ID.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/attachments/multiple", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadAttachments(@RequestParam("files") MultipartFile[] files,
                                                   @RequestParam("siteId") Long siteId,
                                                   @RequestParam(value = "assessmentCriteriaId", required = false) Long assessmentCriteriaId,
                                                   @RequestParam("createdBy") String createdBy) {

        log.debug("REST request to save Attachment multiple attachments " +
                "for siteID: {}, " +
                "assessmentCriteriaId: {},  " +
                "createdBy: {}", siteId, assessmentCriteriaId, createdBy);

        if (!siteService.existsById(siteId)) {
            throw new BadRequestAlertException("Entity related site not found", ENTITY_NAME, "id not found");
        }

        try {
            attachmentService.saveMultiple(files, siteId, assessmentCriteriaId, createdBy);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("Files uploaded successfully: %s",
                            Arrays.stream(files).map(MultipartFile::getOriginalFilename).toList()));
        } catch (Exception e) {
            log.debug("Error with upload: {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Could not upload the files: %s!",
                            Arrays.stream(files).map(MultipartFile::getOriginalFilename).toList()));
        }
    }


    /**
     * {@code DELETE  /attachments/:id} : delete the "id" attachment.
     *
     * @param id the id of the attachment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
    @DeleteMapping(path="/attachments/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        log.debug("REST request to delete Attachment for id: {}", id);
        attachmentService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }
}
