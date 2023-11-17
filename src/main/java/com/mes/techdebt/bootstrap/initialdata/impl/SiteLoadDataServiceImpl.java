package com.mes.techdebt.bootstrap.initialdata.impl;

import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.domain.InvestmentCriticality;
import com.mes.techdebt.domain.Site;
import com.mes.techdebt.repository.InvestmentCriticalityRepository;
import com.mes.techdebt.repository.SiteRepository;
import com.mes.techdebt.service.dto.SiteDTO;
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
 * Migration Data Loader for {@link Site}.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SiteLoadDataServiceImpl implements GenericLoadDataService<SiteDTO> {

    private final JdbcTemplate jdbcTemplate;

    private final SiteRepository siteRepository;
    private final InvestmentCriticalityRepository investmentCriticalityRepository;

    @Override
    public Long saveWithSpecificId(SiteDTO siteDTO) {
        log.debug("Requested Site: {}", siteDTO);
        String sqlQuery = "insert into site(id, name, mdm_site_id, mdm_site_name, comment, is_active, " +
                "region, enterprise, business_group, reporting_unit, address, country, country_code, city, state, " +
                "latitude, longitude, investment_criticality_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        log.debug("Requested Site: {}", siteDTO);
        Site site = siteRepository.findById(siteDTO.getId())
                .orElse(null);

        InvestmentCriticality investmentCriticality = investmentCriticalityRepository
                .findById(siteDTO.getInvestment_criticality_id())
                .orElse(null);
        if(site == null && investmentCriticality != null){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setLong(1, siteDTO.getId());
                stmt.setString(2, siteDTO.getName());
                stmt.setLong(3, siteDTO.getMdmSiteId());
                stmt.setString(4, siteDTO.getMdmSiteName());
                stmt.setString(5, siteDTO.getComment());
                stmt.setBoolean(6, siteDTO.getIsActive());
                stmt.setString(7, siteDTO.getRegion());
                stmt.setString(8, siteDTO.getEnterprise());
                stmt.setString(9, siteDTO.getBusinessGroup());
                stmt.setString(10, siteDTO.getReportingUnit());
                stmt.setString(11, siteDTO.getAddress());
                stmt.setString(12, siteDTO.getCountry());
                stmt.setString(13, siteDTO.getCountryCode());
                stmt.setString(14, siteDTO.getCity());
                stmt.setString(15, siteDTO.getState());
                stmt.setDouble(16, siteDTO.getLatitude());
                stmt.setDouble(17, siteDTO.getLongitude());
                stmt.setLong(18, investmentCriticality.getId());
                return stmt;
            }, keyHolder);
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        }else{
            return siteDTO.getId();
        }
    }

    @Override
    public List<Long> saveAllWithSpecificId(List<SiteDTO> siteDTOs) {
        log.debug("Request to save siteRepository.count()>0: {}", siteRepository.count());
        return siteRepository.count()>0  ? null : siteDTOs.stream()
                .map(this::saveWithSpecificId)
                .toList();
    }
}
