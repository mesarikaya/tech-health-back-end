package com.mes.techdebt.service.impl;

import com.mes.techdebt.repository.SiteRepository;
import com.mes.techdebt.service.mapper.SiteMapper;
import com.mes.techdebt.domain.Site;
import com.mes.techdebt.service.SiteService;
import com.mes.techdebt.service.dto.SiteDTO;
import com.mes.techdebt.web.rest.response.DashboardSiteAndCountryFilterDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Site}.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SiteServiceImpl implements SiteService {
    private final SiteRepository siteRepository;
    private final SiteMapper siteMapper;
    @Override
    public SiteDTO save(SiteDTO siteDTO) {
        log.debug("Request to save Site : {}", siteDTO);
        Site site = siteRepository.findByName(siteDTO.getName())
                .orElse(null);

        if(site != null){
            siteDTO.setId(site.getId());
            siteMapper.partialUpdate(site, siteDTO);
        }else{
            site = siteMapper.toEntity(siteDTO);
            log.debug("New site: {}", site);
        }
        site = siteRepository.save(site);
        return siteMapper.toDto(site);
    }

    @Override
    public SiteDTO update(SiteDTO siteDTO) {
        log.debug("Request to save Site : {}", siteDTO);
        Site site = siteMapper.toEntity(siteDTO);
        site = siteRepository.save(site);
        return siteMapper.toDto(site);
    }

    @Override
    public Optional<SiteDTO> partialUpdate(SiteDTO siteDTO) {
        log.debug("Request to partially update Site : {}", siteDTO);

        return siteRepository
            .findById(siteDTO.getId())
            .map(existingSite -> {
                siteMapper.partialUpdate(existingSite, siteDTO);

                return existingSite;
            })
            .map(siteRepository::save)
            .map(siteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SiteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sites");
        return siteRepository.findAll(pageable).map(siteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SiteDTO> findOne(Long id) {
        log.debug("Request to get Site : {}", id);
        return siteRepository.findById(id).map(siteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<DashboardSiteAndCountryFilterDTO>> findSitesByRegions(Set<String> regions) {
        log.debug("Request to get Site by regions: {}", regions);
        return siteRepository.findSitesByRegionIn(regions)
                .map(sites -> siteMapper.toDto(List.copyOf(sites)))
                .flatMap(siteDTOs -> {
                    siteDTOs.sort(Comparator.comparing(SiteDTO::getName));
                    List<DashboardSiteAndCountryFilterDTO> siteList = siteDTOs.stream()
                            .map(siteDTO ->
                                 DashboardSiteAndCountryFilterDTO.builder()
                                        .site(siteDTO.getName())
                                         .region(siteDTO.getRegion())
                                         .siteId(siteDTO.getId())
                                        .country(siteDTO.getCountry())
                                        .isActive(siteDTO.getIsActive()==null ? false : siteDTO.getIsActive())
                                        .build()
                            )
                            .collect(Collectors.toList());
                    return Optional.of(siteList);
                });
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Site : {}", id);
        siteRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Request to check Site exists by id: {}", id);
        return siteRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        log.debug("Request to check Site exists by name: {}", name);
        Optional<Site> site = siteRepository.findByName(name);
        return site.isPresent() ? true : false;
    }

    @Override
    public boolean existsByMdmSiteId(Long mdmSiteId) {
        log.debug("Request to check Site exists by mdmSiteId: {}", mdmSiteId);
        Optional<Site> site = siteRepository.findByMdmSiteId(mdmSiteId);
        return site.isPresent() ? true : false;
    }

    @Override
    public Long getIdByName(String name) {
        log.debug("Request to get Site id by name: {}", name);
        Optional<Site> site = siteRepository.findByName(name);
        return site.isPresent() ? site.get().getId() : null;
    }

    @Override
    public Long getIdByMdmSiteId(Long mdmSiteId) {
        log.debug("Request to get Site id by mdmSiteId: {}", mdmSiteId);
        Optional<Site> site = siteRepository.findByMdmSiteId(mdmSiteId);
        return site.isPresent() ? site.get().getId() : null;
    }
}
