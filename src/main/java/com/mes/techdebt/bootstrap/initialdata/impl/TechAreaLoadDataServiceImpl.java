package com.mes.techdebt.bootstrap.initialdata.impl;

import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.domain.CostRange;
import com.mes.techdebt.domain.TechArea;
import com.mes.techdebt.domain.TechDomain;
import com.mes.techdebt.repository.TechAreaRepository;
import com.mes.techdebt.repository.TechDomainRepository;
import com.mes.techdebt.service.dto.TechAreaDTO;
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
public class TechAreaLoadDataServiceImpl implements GenericLoadDataService<TechAreaDTO> {

    private final JdbcTemplate jdbcTemplate;

    private final TechAreaRepository techAreaRepository;

    private final TechDomainRepository techDomainRepository;

    @Override
    public Long saveWithSpecificId(TechAreaDTO techAreaDTO) {
        String sqlQuery = "insert into tech_area(id, description, domain_id, is_active) " +
                "values (?, ?, ?, ?)";

        TechArea techArea = techAreaRepository.findById(techAreaDTO.getId()).orElse(null);
        log.debug("Requested TechArea: {}", techArea);

        TechDomain techDomain = techDomainRepository.findById(techAreaDTO.getDomain_id())
                .orElse(null);

        if(techArea == null && techDomain != null){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setLong(1, techAreaDTO.getId());
                stmt.setString(2, techAreaDTO.getDescription());
                stmt.setLong(3, techDomain.getId());
                stmt.setBoolean(4, techAreaDTO.getIsActive());
                log.debug("Saving TechArea prepared statement: {}", stmt);
                return stmt;
            }, keyHolder);
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        }else{
            return techAreaDTO.getId();
        }
    }

    @Override
    public List<Long> saveAllWithSpecificId(List<TechAreaDTO> techAreaDTOs) {
        log.debug("Request to save techAreaRepository.count()>0: {}", techAreaRepository.count());
        return techAreaRepository.count()>0  ? null : techAreaDTOs.stream()
                .map(this::saveWithSpecificId)
                .toList();
    }
}
