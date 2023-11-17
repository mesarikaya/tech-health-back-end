package com.mes.techdebt.service.impl;

import com.mes.techdebt.domain.TechArea;
import com.mes.techdebt.repository.TechAreaRepository;
import com.mes.techdebt.service.TechAreaService;
import com.mes.techdebt.service.dto.TechAreaDTO;
import com.mes.techdebt.service.mapper.TechAreaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link TechArea}.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TechAreaServiceImpl implements TechAreaService {
    private final TechAreaRepository techAreaRepository;
    private final TechAreaMapper techAreaMapper;
    @Override
    public TechAreaDTO save(TechAreaDTO techAreaDTO) {
        log.debug("Request to save TechArea : {}", techAreaDTO);
        TechArea techArea = techAreaRepository
                .findByDescription(techAreaDTO.getDescription())
                .orElse(null);

        if(techArea != null){
            log.debug("No save needed - Existing TechArea entity: {}", techArea);
            techAreaDTO.setId(techArea.getId());
            techAreaMapper.partialUpdate(techArea, techAreaDTO);
        }else{
            techArea = techAreaMapper.toEntity(techAreaDTO);
            log.debug("New TechArea: {}", techArea);
        }

        techArea = techAreaRepository.save(techArea);
        return techAreaMapper.toDto(techArea);
    }

    @Override
    public TechAreaDTO update(TechAreaDTO techAreaDTO) {
        log.debug("Request to save TechArea : {}", techAreaDTO);
        TechArea techArea = techAreaMapper.toEntity(techAreaDTO);
        techArea = techAreaRepository.save(techArea);
        return techAreaMapper.toDto(techArea);
    }

    @Override
    public Optional<TechAreaDTO> partialUpdate(TechAreaDTO techAreaDTO) {
        log.debug("Request to partially update TechArea : {}", techAreaDTO);

        return techAreaRepository
            .findById(techAreaDTO.getId())
            .map(existingTechArea -> {
                techAreaMapper.partialUpdate(existingTechArea, techAreaDTO);

                return existingTechArea;
            })
            .map(techAreaRepository::save)
            .map(techAreaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TechAreaDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TechAreas");
        return techAreaRepository.findAll(pageable).map(techAreaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TechAreaDTO> findOne(Long id) {
        log.debug("Request to get TechArea : {}", id);
        return techAreaRepository.findById(id).map(techAreaMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TechArea : {}", id);
        techAreaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Request to check TechArea exists by id: {}", id);
        return techAreaRepository.existsById(id);
    }

    @Override
    public boolean existsByDescription(String description) {
        log.debug("Request to check TechArea exists by description: {}", description);
        Optional<TechArea> techArea = techAreaRepository.findByDescription(description);
        return techArea.isPresent() ? true : false;
    }

    @Override
    public Long getIdByDescription(String description) {
        log.debug("Request to get TechArea id by description: {}", description);
        Optional<TechArea> techArea = techAreaRepository.findByDescription(description);
        return techArea.isPresent() ? techArea.get().getId() : null;
    }
}
