package com.mes.techdebt.bootstrap.initialdata.impl;

import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.domain.Category;
import com.mes.techdebt.domain.CostRange;
import com.mes.techdebt.domain.CostToFix;
import com.mes.techdebt.domain.Site;
import com.mes.techdebt.repository.CategoryRepository;
import com.mes.techdebt.repository.CostRangeRepository;
import com.mes.techdebt.repository.CostToFixRepository;
import com.mes.techdebt.repository.SiteRepository;
import com.mes.techdebt.service.dto.CostToFixDTO;
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
 * Migration Data Loader for {@link CostToFix}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CostToFixLoadDataServiceImpl implements GenericLoadDataService<CostToFixDTO> {

    private final JdbcTemplate jdbcTemplate;

    private final CostToFixRepository costToFixRepository;

    private final CategoryRepository categoryRepository;

    private final SiteRepository siteRepository;

    private final CostRangeRepository costRangeRepository;

    @Override
    public Long saveWithSpecificId(CostToFixDTO costToFixDTO) {
        String sqlQuery = "insert into cost_to_fix(id, site_id, category_id, cost_range_id) " +
                "values (?, ?, ?, ?)";

        CostToFix costToFix = costToFixRepository.findById(costToFixDTO.getId())
                .orElse(null);

        Site site = siteRepository.findById(costToFixDTO.getSite_id()).orElse(null);
        Category category = categoryRepository.findById(costToFixDTO.getCategory_id()).orElse(null);
        CostRange costRange = costRangeRepository.findById(costToFixDTO.getCost_range_id()).orElse(null);
        if(costToFix == null && site != null && category != null && costRange != null){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setLong(1, costToFixDTO.getId());
                stmt.setLong(2, site.getId());
                stmt.setLong(3, category.getId());
                stmt.setLong(4, costRange.getId());
                return stmt;
            }, keyHolder);
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        }else{
            return costToFixDTO.getId();
        }

    }

    @Override
    public List<Long> saveAllWithSpecificId(List<CostToFixDTO> costToFixDTOs) {
        log.debug("Request to save costToFixRepository.count() : {}", costToFixRepository.count());
        return costToFixRepository.count()>0  ? null : costToFixDTOs.stream()
                .map(this::saveWithSpecificId)
                .toList();
    }
}
