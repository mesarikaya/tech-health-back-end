package com.mes.techdebt.bootstrap.initialdata.impl;

import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.domain.CostRange;
import com.mes.techdebt.repository.CostRangeRepository;
import com.mes.techdebt.service.dto.CostRangeDTO;
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
 * Migration Data Loader for {@link CostRange}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CostRangeLoadDataServiceImpl implements GenericLoadDataService<CostRangeDTO> {

    private final JdbcTemplate jdbcTemplate;

    private final CostRangeRepository costRangeRepository;

    @Override
    public Long saveWithSpecificId(CostRangeDTO costRangeDTO) {
        String sqlQuery = "insert into cost_range(id, description) " +
                "values (?, ?)";

        CostRange costRange = costRangeRepository.findById(costRangeDTO.getId())
                .orElse(null);

        if(costRange == null){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setLong(1, costRangeDTO.getId());
                stmt.setString(2, costRangeDTO.getDescription());
                return stmt;
            }, keyHolder);
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        }else{
            return costRangeDTO.getId();
        }

    }

    @Override
    public List<Long> saveAllWithSpecificId(List<CostRangeDTO> costRangeDTOs) {
        log.debug("Request to save costRangeRepository.count() : {}", costRangeRepository.count());
        return costRangeRepository.count()>0  ? null : costRangeDTOs.stream()
                .map(this::saveWithSpecificId)
                .toList();
    }
}
