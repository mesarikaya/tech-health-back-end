package com.mes.techdebt.bootstrap.initialdata.impl;

import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.domain.TechDomain;
import com.mes.techdebt.repository.TechDomainRepository;
import com.mes.techdebt.service.dto.TechDomainDTO;
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
 * Migration Data Loader for {@link TechDomain}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TechDomainLoadDataServiceImpl implements GenericLoadDataService<TechDomainDTO> {

    private final JdbcTemplate jdbcTemplate;

    private final TechDomainRepository techDomainRepository;

    @Override
    public Long saveWithSpecificId(TechDomainDTO techDomainDTO) {
        String sqlQuery = "insert into tech_domain(id, description, is_active) " +
                "values (?, ?, ?)";

        TechDomain techDomain = techDomainRepository
                .findById(techDomainDTO.getId())
                .orElse(null);

        if(techDomain == null){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setLong(1, techDomainDTO.getId());
                stmt.setString(2, techDomainDTO.getDescription());
                stmt.setBoolean(3, techDomainDTO.getIsActive());
                return stmt;
            }, keyHolder);
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        }else{
            return techDomainDTO.getId();
        }

    }

    @Override
    public List<Long> saveAllWithSpecificId(List<TechDomainDTO> techDomainDTOs) {
        log.info("techDomainRepository.count(): {}", techDomainRepository.count());
        return techDomainRepository.count()>0  ? null : techDomainDTOs.stream()
                .map(this::saveWithSpecificId)
                .toList();
    }
}
