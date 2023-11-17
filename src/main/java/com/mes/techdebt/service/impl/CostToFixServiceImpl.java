package com.mes.techdebt.service.impl;

import com.mes.techdebt.domain.CostToFix;
import com.mes.techdebt.repository.CostToFixRepository;
import com.mes.techdebt.service.CostToFixService;
import com.mes.techdebt.service.dto.CostToFixDTO;
import com.mes.techdebt.service.mapper.CostToFixMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link CostToFix}.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CostToFixServiceImpl implements CostToFixService {

    private final CostToFixRepository costToFixRepository;

    private final CostToFixMapper costToFixMapper;

    @Override
    public CostToFixDTO save(CostToFixDTO costToFixDTO) {
        log.debug("Request to save CostToFix : {}", costToFixDTO);
        CostToFix costToFix = costToFixMapper.toEntity(costToFixDTO);
        costToFixRepository.save(costToFix);
        return costToFixMapper.toDto(costToFix);
    }

    @Override
    public CostToFixDTO update(CostToFixDTO costToFixDTO) {
        log.debug("Request to save CostToFix : {}", costToFixDTO);
        CostToFix costToFix = costToFixMapper.toEntity(costToFixDTO);
        costToFix = costToFixRepository.save(costToFix);
        return costToFixMapper.toDto(costToFix);
    }

    @Override
    public Optional<CostToFixDTO> partialUpdate(CostToFixDTO costToFixDTO) {
        log.debug("Request to partially update CostToFix : {}", costToFixDTO);

        return costToFixRepository
            .findById(costToFixDTO.getId())
            .map(existingCostToFix -> {

                costToFixMapper.partialUpdate(existingCostToFix, costToFixDTO);

                return existingCostToFix;
            })
            .map(costToFixRepository::save)
            .map(costToFixMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CostToFixDTO> findAll() {
        log.debug("Request to get all CostToFixes");
        return costToFixRepository.findAll().stream().map(costToFixMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CostToFixDTO> findOne(Long id) {
        log.debug("Request to get CostToFix : {}", id);
        return costToFixRepository.findById(id).map(costToFixMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CostToFix : {}", id);
        costToFixRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Request to check CostToFix exists by id: {}", id);
        return costToFixRepository.existsById(id);
    }

    @Override
    public Optional<CostToFixDTO> findBySiteIdAndCategoryIdAndCostRangeId(Long siteId, Long categoryId, Long costRangeId) {
        log.debug("Request to check CostToFix Cost Range exists by site id: {} and category id: {} and costRange id: {}",
                siteId, categoryId, costRangeId);
        return costToFixRepository.findBySite_Id_AndCategory_IdAndCostRange_Id(siteId, categoryId, costRangeId)
                .stream().map(costToFixMapper::toDto).findFirst();
    }

    @Override
    public boolean existsBySiteId(Long siteId) {
        log.debug("Request to check CostToFix entry exists by site id: {}", siteId);
        return costToFixRepository.existsBySite_Id(siteId);
    }

    @Override
    public boolean existsBySiteIdAndCategoryId(Long siteId, Long categoryId) {
        log.debug("Request to check CostToFix entry exists by site id: {} and category id: {}", siteId, categoryId);
        return costToFixRepository.existsBySite_IdAndCategory_Id(siteId, categoryId);
    }
}
