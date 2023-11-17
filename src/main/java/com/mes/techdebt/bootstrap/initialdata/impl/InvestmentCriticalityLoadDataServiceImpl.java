package com.mes.techdebt.bootstrap.initialdata.impl;

import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.domain.InvestmentCriticality;
import com.mes.techdebt.repository.InvestmentCriticalityRepository;
import com.mes.techdebt.service.dto.InvestmentCriticalityDTO;
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
 * Migration Data Loader for {@link InvestmentCriticality}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class InvestmentCriticalityLoadDataServiceImpl implements GenericLoadDataService<InvestmentCriticalityDTO> {

    private final JdbcTemplate jdbcTemplate;

    private final InvestmentCriticalityRepository investmentCriticalityRepository;

    @Override
    public Long saveWithSpecificId(InvestmentCriticalityDTO investmentCriticalityDTO) {
        String sqlQuery = "insert into investment_criticality(id, description) " +
                "values (?, ?)";

        InvestmentCriticality investmentCriticality = investmentCriticalityRepository
                .findById(investmentCriticalityDTO.getId())
                .orElse(null);

        if(investmentCriticality == null){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setLong(1, investmentCriticalityDTO.getId());
                stmt.setString(2, investmentCriticalityDTO.getDescription());
                return stmt;
            }, keyHolder);
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        }else{
            return investmentCriticalityDTO.getId();
        }
    }

    @Override
    public List<Long> saveAllWithSpecificId(List<InvestmentCriticalityDTO> investmentCriticalityDTOs) {
        log.debug("Request to save investmentCriticalityRepository.count(): {}", investmentCriticalityRepository.count());
        return investmentCriticalityRepository.count()>0  ? null : investmentCriticalityDTOs.stream()
                .map(this::saveWithSpecificId)
                .toList();
    }
}
