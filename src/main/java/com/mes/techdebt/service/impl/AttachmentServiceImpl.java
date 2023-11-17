package com.mes.techdebt.service.impl;

import com.mes.techdebt.domain.AssessmentCriteria;
import com.mes.techdebt.domain.Attachment;
import com.mes.techdebt.domain.Site;
import com.mes.techdebt.repository.AssessmentCriteriaRepository;
import com.mes.techdebt.repository.AttachmentRepository;
import com.mes.techdebt.repository.SiteRepository;
import com.mes.techdebt.service.AttachmentService;
import com.mes.techdebt.service.dto.AttachmentDTO;
import com.mes.techdebt.service.mapper.AttachmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final SiteRepository siteRepository;
    private final AssessmentCriteriaRepository assessmentCriteriaRepository;
    private final AttachmentMapper attachmentMapper;

    @Override
    public void save(MultipartFile file, Long siteId, Long assessmentCriteriaId, String createdBy) throws IOException {
        log.debug("Request to save Attachment file: {}", file.getOriginalFilename());
        Attachment attachment = setAttachment(file, siteId, assessmentCriteriaId, createdBy);
        attachmentRepository.save(attachment);
    }

    @Override
    public void saveMultiple(MultipartFile[] files, Long siteId, Long assessmentCriteriaId, String createdBy) throws IOException {

        List<Attachment> attachments = Arrays.stream(files)
                .map(file -> {
                    try {
                        return setAttachment(file, siteId, assessmentCriteriaId, createdBy);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        attachmentRepository.saveAll(attachments);
    }

    private Attachment setAttachment(MultipartFile file, Long siteId, Long assessmentCriteriaId, String createdBy) throws IOException {
        Attachment attachment = new Attachment();
        Site site = siteRepository.findById(siteId).orElse(null);
        if (assessmentCriteriaId !=null) {
            AssessmentCriteria assessmentCriteria = assessmentCriteriaRepository
                    .findById(assessmentCriteriaId)
                    .orElse(null);
            attachment.setAssessmentCriteria(assessmentCriteria);
        }
        attachment.setSite(site);
        attachment.setUpdatedBy(createdBy);
        attachment.setCreatedBy(createdBy);
        attachment.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
        attachment.setFileType(file.getContentType());
        attachment.setData(file.getBytes());
        attachment.setFileSize(file.getSize());
        return attachment;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttachmentDTO> findOne(Long id) {
        log.debug("Request to get Attachment with id: {}", id);
        return attachmentRepository.findById(id).map(attachmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentDTO> findAll() {
        log.debug("Request to get all Attachments");
        return attachmentRepository.findAll().stream().map(attachmentMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<AttachmentDTO>> findBySiteId(Long siteId) {
        log.debug("Request to get Attachment with siteid: {}", siteId);
        return attachmentRepository.findBySite_Id(siteId).map(attachmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<AttachmentDTO>> findBySiteIdAndAssessmentCriteriaId(Long siteId, Long assessmentCriteriaId) {
        log.debug("Request to get Attachment with siteid: {} and assessmentCriteriaId", siteId, assessmentCriteriaId);
        return attachmentRepository.findBySite_IdAndAssessmentCriteria_Id(siteId, assessmentCriteriaId).map(attachmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to get Attachment with id: {}", id);
        attachmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        log.debug("Request to check Attachment exists by id: {}", id);
        return attachmentRepository.existsById(id);
    }
}
