package com.mes.techdebt.service.impl;

import com.mes.techdebt.domain.CostRange;
import com.mes.techdebt.repository.CostRangeRepository;
import com.mes.techdebt.service.CostRangeService;
import com.mes.techdebt.service.dto.CostRangeDTO;
import com.mes.techdebt.service.mapper.CostRangeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link CostRange}.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CostRangeServiceImpl implements CostRangeService {
    private final CostRangeRepository costRangeRepository;
    private final CostRangeMapper costRangeMapper;

    @Override
    public CostRangeDTO save(CostRangeDTO costRangeDTO) {
        log.debug("Request to save CostRange : {}", costRangeDTO);
        CostRange costRange = costRangeRepository
                .findByDescription(costRangeDTO.getDescription())
                .orElse(null);

        if(costRange != null){
            log.debug("No save needed - Existing CostRange entity: {}", costRange);
            costRangeDTO.setId(costRange.getId());
            costRangeMapper.partialUpdate(costRange, costRangeDTO);
        }else{
            costRange = costRangeMapper.toEntity(costRangeDTO);
            log.debug("New CostRange: {}", costRange);
        }

        costRange = costRangeRepository.save(costRange);
        return costRangeMapper.toDto(costRange);
    }

    @Override
    public CostRangeDTO update(CostRangeDTO costRangeDTO) {
        log.debug("Request to save CostRange : {}", costRangeDTO);
        CostRange costRange = costRangeMapper.toEntity(costRangeDTO);
        costRange = costRangeRepository.save(costRange);
        return costRangeMapper.toDto(costRange);
    }

    @Override
    public Optional<CostRangeDTO> partialUpdate(CostRangeDTO costRangeDTO) {
        log.debug("Request to partially update CostRange : {}", costRangeDTO);

        return costRangeRepository
            .findById(costRangeDTO.getId())
            .map(existingCostRange -> {
                costRangeMapper.partialUpdate(existingCostRange, costRangeDTO);

                return existingCostRange;
            })
            .map(costRangeRepository::save)
            .map(costRangeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CostRangeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CostRanges");
        return costRangeRepository.findAll(pageable).map(costRangeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CostRangeDTO> findOne(Long id) {
        log.debug("Request to get CostRange : {}", id);
        return costRangeRepository.findById(id).map(costRangeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CostRange : {}", id);
        costRangeRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Request to check CostRange exists by id: {}", id);
        return costRangeRepository.existsById(id);
    }

    @Override
    public boolean existsByDescription(String description) {
        log.debug("Request to check CostRange exists by description: {}", description);
        Optional<CostRange> costRange = costRangeRepository.findByDescription(description);
        return costRange.isPresent();
    }

    @Override
    public Long getIdByDescription(String description) {
        log.debug("Request to get CostRange id by description: {}", description);
        Optional<CostRange> costRange = costRangeRepository.findByDescription(description);
        return costRange.isPresent() ? costRange.get().getId() : null;
    }
}
