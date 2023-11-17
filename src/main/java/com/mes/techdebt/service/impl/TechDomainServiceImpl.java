package com.mes.techdebt.service.impl;

import com.mes.techdebt.domain.TechDomain;
import com.mes.techdebt.repository.TechDomainRepository;
import com.mes.techdebt.service.TechDomainService;
import com.mes.techdebt.service.dto.TechDomainDTO;
import com.mes.techdebt.service.mapper.TechDomainMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link TechDomain}.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TechDomainServiceImpl implements TechDomainService {

    private final TechDomainRepository techDomainRepository;
    private final TechDomainMapper techDomainMapper;

    @Override
    public TechDomainDTO save(TechDomainDTO techDomainDTO) {
        log.debug("Request to save TechDomain : {}", techDomainDTO);
        TechDomain techDomain = techDomainRepository
                .findByDescription(techDomainDTO.getDescription())
                .orElse(null);

        if(techDomain != null){
            log.debug("No save needed - Existing TechDomain entity: {}", techDomain);
            techDomainDTO.setId(techDomain.getId());
            techDomainMapper.partialUpdate(techDomain, techDomainDTO);
        }else{
            techDomain = techDomainMapper.toEntity(techDomainDTO);
            log.debug("New TechDomain: {}", techDomain);
        }

        techDomain = techDomainRepository.save(techDomain);
        return techDomainMapper.toDto(techDomain);
    }

    @Override
    public TechDomainDTO update(TechDomainDTO techDomainDTO) {
        log.debug("Request to save TechDomain : {}", techDomainDTO);
        TechDomain techDomain = techDomainMapper.toEntity(techDomainDTO);
        techDomain = techDomainRepository.save(techDomain);
        return techDomainMapper.toDto(techDomain);
    }

    @Override
    public Optional<TechDomainDTO> partialUpdate(TechDomainDTO techDomainDTO) {
        log.debug("Request to partially update TechDomain : {}", techDomainDTO);

        return techDomainRepository
            .findById(techDomainDTO.getId())
            .map(existingTechDomain -> {
                techDomainMapper.partialUpdate(existingTechDomain, techDomainDTO);

                return existingTechDomain;
            })
            .map(techDomainRepository::save)
            .map(techDomainMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TechDomainDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TechDomains");
        return techDomainRepository.findAll(pageable).map(techDomainMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TechDomainDTO> findOne(Long id) {
        log.debug("Request to get TechDomain : {}", id);
        return techDomainRepository.findById(id).map(techDomainMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TechDomain : {}", id);
        techDomainRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Request to check TechDomain exists by id: {}", id);
        return techDomainRepository.existsById(id);
    }

    @Override
    public boolean existsByDescription(String description) {
        log.debug("Request to check TechDomain exists by description: {}", description);
        Optional<TechDomain> techDomain = techDomainRepository.findByDescription(description);
        return techDomain.isPresent();
    }

    @Override
    public Long getIdByDescription(String description) {
        log.debug("Request to get TechDomain id by description: {}", description);
        Optional<TechDomain> techDomain = techDomainRepository.findByDescription(description);
        return techDomain.isPresent() ? techDomain.get().getId() : null;
    }
}
