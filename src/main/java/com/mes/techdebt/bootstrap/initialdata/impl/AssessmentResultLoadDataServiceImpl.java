package com.mes.techdebt.bootstrap.initialdata.impl;

import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.cargill.techdebt.domain.*;
import com.mes.techdebt.domain.AssessmentCriteria;
import com.mes.techdebt.domain.AssessmentResult;
import com.mes.techdebt.domain.RecommendationStatus;
import com.mes.techdebt.domain.Site;
import com.mes.techdebt.repository.AssessmentCriteriaRepository;
import com.mes.techdebt.repository.AssessmentResultRepository;
import com.mes.techdebt.repository.RecommendationStatusRepository;
import com.mes.techdebt.repository.SiteRepository;
import com.mes.techdebt.service.dto.AssessmentResultDTO;
import com.mes.techdebt.service.mapper.DateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

/**
 * Migration Data Loader for {@link AssessmentResult}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AssessmentResultLoadDataServiceImpl implements GenericLoadDataService<AssessmentResultDTO> {

    private final JdbcTemplate jdbcTemplate;

    private final AssessmentResultRepository assessmentResultRepository;

    private final SiteRepository siteRepository;
    private final AssessmentCriteriaRepository assessmentCriteriaRepository;
    private final RecommendationStatusRepository recommendationStatusRepository;
    private final DateMapper dateMapper;

    @Override
    public Long saveWithSpecificId(AssessmentResultDTO assessmentResultDTO) {
        String sqlQuery = "insert into assessment_result(id, score, recommendation_text, notes, " +
                "create_date, created_by, update_date, updated_by, site_id, assessment_criteria_id," +
                "recommendation_status_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        AssessmentResult assessmentResult = assessmentResultRepository
                .findById(assessmentResultDTO.getId())
                .orElse(null);
        log.debug("Requested AssessmentResult: {}", assessmentResultDTO);
        Site site = siteRepository
                .findById(assessmentResultDTO.getSite_id())
                .orElse(null);
        AssessmentCriteria assessmentCriteria = assessmentCriteriaRepository
                .findById(assessmentResultDTO.getAssessment_criteria_id())
                .orElse(null);
        RecommendationStatus recommendationStatus = recommendationStatusRepository
                .findById(assessmentResultDTO.getRecommendation_status_id())
                .orElse(null);
        log.debug("Requested AssessmentResult for site: {}", site);
        log.debug("Requested AssessmentResult for criteria: {}", assessmentCriteria);
        log.debug("Requested AssessmentResult for recommendation status: {}", recommendationStatus);


        log.debug("Requested AssessmentResult mapped create date time: {} for {}",
                dateMapper.asTimestamp(assessmentResultDTO.getCreateDate()),
                assessmentResultDTO.getCreateDate());

        log.debug("Requested AssessmentResult mapped update date time: {} for {}",
                dateMapper.asTimestamp(assessmentResultDTO.getUpdateDate()),
                assessmentResultDTO.getUpdateDate());

        if(assessmentResult == null && site != null  && assessmentCriteria != null
                && recommendationStatus != null){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setLong(1, assessmentResultDTO.getId());
                stmt.setDouble(2, assessmentResultDTO.getScore());
                stmt.setString(3, assessmentResultDTO.getRecommendationText());
                stmt.setString(4, assessmentResultDTO.getNotes());
                stmt.setTimestamp(5, dateMapper.asTimestamp(assessmentResultDTO.getCreateDate()));
                stmt.setString(6, assessmentResultDTO.getCreatedBy());
                stmt.setTimestamp(7, dateMapper.asTimestamp(assessmentResultDTO.getUpdateDate()));
                stmt.setString(8, assessmentResultDTO.getUpdatedBy());
                stmt.setLong(9, site.getId());
                stmt.setLong(10, assessmentCriteria.getId());
                stmt.setLong(11, recommendationStatus.getId());
                return stmt;
            }, keyHolder);
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        }else{
            return assessmentResultDTO.getId();
        }
    }

    @Override
    public List<Long> saveAllWithSpecificId(List<AssessmentResultDTO> assessmentResultDTOs) {
        log.info("Request to save assessmentResultRepository.count() : {}", assessmentResultRepository.count());
        return assessmentResultRepository.count()>0  ? null : assessmentResultDTOs.stream()
                .map(this::saveWithSpecificId)
                .toList();
    }
}
