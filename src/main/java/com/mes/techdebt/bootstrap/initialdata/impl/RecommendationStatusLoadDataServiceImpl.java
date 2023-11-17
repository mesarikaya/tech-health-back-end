package com.mes.techdebt.bootstrap.initialdata.impl;

import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.domain.RecommendationStatus;
import com.mes.techdebt.repository.RecommendationStatusRepository;
import com.mes.techdebt.service.dto.RecommendationStatusDTO;
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
 * Migration Data Loader for {@link RecommendationStatus}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RecommendationStatusLoadDataServiceImpl implements GenericLoadDataService<RecommendationStatusDTO> {

    private final JdbcTemplate jdbcTemplate;

    private final RecommendationStatusRepository recommendationStatusRepository;

    @Override
    public Long saveWithSpecificId(RecommendationStatusDTO recommendationStatusDTO) {
        String sqlQuery = "insert into recommendation_status(id, description) " +
                "values (?, ?)";

        RecommendationStatus recommendationStatus = recommendationStatusRepository
                .findById(recommendationStatusDTO.getId())
                .orElse(null);

        if(recommendationStatus == null){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setLong(1, recommendationStatusDTO.getId());
                stmt.setString(2, recommendationStatusDTO.getDescription());
                return stmt;
            }, keyHolder);
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        }else{
            return recommendationStatusDTO.getId();
        }

    }

    @Override
    public List<Long> saveAllWithSpecificId(List<RecommendationStatusDTO> recommendationStatusDTOs) {
        log.debug("Request to save recommendationStatusRepository.count(): {}", recommendationStatusRepository.count());
        return recommendationStatusRepository.count()>0  ? null : recommendationStatusDTOs.stream()
                .map(this::saveWithSpecificId)
                .toList();
    }
}
