package com.mes.techdebt.bootstrap.initialdata.impl;

import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.domain.AssessmentCriteria;
import com.mes.techdebt.domain.Attachment;
import com.mes.techdebt.domain.Site;
import com.mes.techdebt.repository.AssessmentCriteriaRepository;
import com.mes.techdebt.repository.AttachmentRepository;
import com.mes.techdebt.repository.SiteRepository;
import com.mes.techdebt.service.dto.AttachmentDTO;
import com.mes.techdebt.service.mapper.DateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

/**
 * Migration Data Loader for {@link Attachment}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AttachmentLoadDataServiceImpl implements GenericLoadDataService<AttachmentDTO> {

    private final static long UNKNOWN_FILE_SIZE_VALUE = -1;

    private final JdbcTemplate jdbcTemplate;
    private final AttachmentRepository attachmentRepository;
    private final SiteRepository siteRepository;
    private final AssessmentCriteriaRepository assessmentCriteriaRepository;
    private final DateMapper dateMapper;

    @Override
    public Long saveWithSpecificId(AttachmentDTO attachmentDTO) {
        String sqlQuery = "insert into attachment(id, file_name, data, file_type, file_size, " +
                "create_date, created_by, update_date, updated_by, site_id, assessment_criteria_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Attachment attachment = attachmentRepository
                .findById(attachmentDTO.getId())
                .orElse(null);
        Site site = siteRepository
                .findById(attachmentDTO.getSite_id())
                .orElse(null);
        AssessmentCriteria assessmentCriteria = assessmentCriteriaRepository
                .findById(attachmentDTO.getAssessment_criteria_id())
                .orElse(null);

        log.debug("Requested Attachment for site: {}", site);
        log.debug("Requested Attachment for criteria: {}", assessmentCriteria);
        if(attachment == null && site != null  && assessmentCriteria != null){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setLong(1, attachmentDTO.getId());
                stmt.setString(2, attachmentDTO.getFileName());
                // Current data has )x as representation in the beginning of hex value. Hex conversion is done after 2nd value
                stmt.setBytes(3, DatatypeConverter.parseHexBinary(attachmentDTO.getStringData().substring(2)));
                stmt.setString(4, attachmentDTO.getFileType());
                // Unknown file size is indicated as -1
                stmt.setLong(5, attachmentDTO.getFileSize() == null ? UNKNOWN_FILE_SIZE_VALUE : attachmentDTO.getFileSize());
                stmt.setTimestamp(6, dateMapper.asTimestamp(attachmentDTO.getCreateDate()));
                stmt.setString(7, attachmentDTO.getCreatedBy());
                stmt.setTimestamp(8, dateMapper.asTimestamp(attachmentDTO.getUpdateDate()));
                stmt.setString(9, attachmentDTO.getUpdatedBy());
                stmt.setLong(10, site.getId());
                stmt.setLong(11, assessmentCriteria.getId());
                return stmt;
            }, keyHolder);
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        }else{
            return attachmentDTO.getId();
        }
    }

    @Override
    public List<Long> saveAllWithSpecificId(List<AttachmentDTO> attachmentDTOs) {
        log.info("Request to save attachmentRepository.count() : {}", attachmentRepository.count());
        return attachmentRepository.count()>0  ? null : attachmentDTOs.stream()
                .map(this::saveWithSpecificId)
                .toList();
    }
}
