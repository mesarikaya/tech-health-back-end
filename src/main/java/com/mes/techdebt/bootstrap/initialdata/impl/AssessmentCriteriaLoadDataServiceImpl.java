package com.mes.techdebt.bootstrap.initialdata.impl;

import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.domain.AssessmentCriteria;
import com.mes.techdebt.domain.Category;
import com.mes.techdebt.repository.AssessmentCriteriaRepository;
import com.mes.techdebt.repository.CategoryRepository;
import com.mes.techdebt.service.dto.AssessmentCriteriaDTO;
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
 * Migration Data Loader for {@link AssessmentCriteria}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AssessmentCriteriaLoadDataServiceImpl implements GenericLoadDataService<AssessmentCriteriaDTO> {

    private final JdbcTemplate jdbcTemplate;

    private final AssessmentCriteriaRepository assessmentCriteriaRepository;

    private final CategoryRepository categoryRepository;

    @Override
    public Long saveWithSpecificId(AssessmentCriteriaDTO assessmentCriteriaDTO) {
        String sqlQuery = "insert into assessment_criteria(id, description, is_active, category_id, tech_standard_description) " +
                "values (?, ?, ?, ?, ?)";

        AssessmentCriteria assessmentCriteria = assessmentCriteriaRepository.findById(assessmentCriteriaDTO.getId())
                .orElse(null);
        log.debug("Requested AssessmentCriteria: {}", assessmentCriteriaDTO);
        Category category = categoryRepository
                .findById(assessmentCriteriaDTO.getCategory_id())
                .orElse(null);
        if(assessmentCriteria == null && category != null){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setLong(1, assessmentCriteriaDTO.getId());
                stmt.setString(2, assessmentCriteriaDTO.getDescription());
                stmt.setBoolean(3, assessmentCriteriaDTO.getIsActive());
                stmt.setLong(4, category.getId());
                stmt.setString(5, assessmentCriteriaDTO.getTechStandardDescription());
                return stmt;
            }, keyHolder);
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        }else{
            return assessmentCriteriaDTO.getId();
        }
    }

    @Override
    public List<Long> saveAllWithSpecificId(List<AssessmentCriteriaDTO> assessmentCriteriaDTOs) {
        log.info("Request to save assessmentCriteriaRepository.count() : {}", assessmentCriteriaRepository.count());
        return assessmentCriteriaRepository.count()>0  ? null : assessmentCriteriaDTOs.stream()
                .map(this::saveWithSpecificId)
                .toList();
    }
}
