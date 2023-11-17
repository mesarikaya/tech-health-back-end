package com.mes.techdebt.service.impl;

import com.mes.techdebt.domain.InvestmentCriticality;
import com.mes.techdebt.repository.InvestmentCriticalityRepository;
import com.mes.techdebt.service.InvestmentCriticalityService;
import com.mes.techdebt.service.dto.InvestmentCriticalityDTO;
import com.mes.techdebt.service.mapper.InvestmentCriticalityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link InvestmentCriticality}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class InvestmentCriticalityServiceImpl implements InvestmentCriticalityService {
    private final InvestmentCriticalityRepository investmentCriticalityRepository;
    private final InvestmentCriticalityMapper investmentCriticalityMapper;
    @Override
    public InvestmentCriticalityDTO save(InvestmentCriticalityDTO investmentCriticalityDTO) {
        log.debug("Request to save InvestmentCriticality : {}", investmentCriticalityDTO);
        InvestmentCriticality investmentCriticality = investmentCriticalityRepository
                .findByDescription(investmentCriticalityDTO.getDescription())
                .orElse(null);

        if(investmentCriticality != null){
            log.debug("No save needed - Existing investment criticality entity: {}", investmentCriticality);
            investmentCriticalityDTO.setId(investmentCriticality.getId());
            investmentCriticalityMapper.partialUpdate(investmentCriticality, investmentCriticalityDTO);
        }else{
            investmentCriticality = investmentCriticalityMapper.toEntity(investmentCriticalityDTO);
            log.debug("New InvestmentCriticality: {}", investmentCriticality);
        }

        investmentCriticality = investmentCriticalityRepository.save(investmentCriticality);
        return investmentCriticalityMapper.toDto(investmentCriticality);
    }

    @Override
    public InvestmentCriticalityDTO update(InvestmentCriticalityDTO investmentCriticalityDTO) {
        log.debug("Request to save InvestmentCriticality : {}", investmentCriticalityDTO);
        InvestmentCriticality investmentCriticality = investmentCriticalityMapper.toEntity(investmentCriticalityDTO);
        investmentCriticality = investmentCriticalityRepository.save(investmentCriticality);
        return investmentCriticalityMapper.toDto(investmentCriticality);
    }

    @Override
    public Optional<InvestmentCriticalityDTO> partialUpdate(InvestmentCriticalityDTO investmentCriticalityDTO) {
        log.debug("Request to partially update InvestmentCriticality : {}", investmentCriticalityDTO);

        return investmentCriticalityRepository
            .findById(investmentCriticalityDTO.getId())
            .map(existingInvestmentCriticality -> {
                investmentCriticalityMapper.partialUpdate(existingInvestmentCriticality, investmentCriticalityDTO);

                return existingInvestmentCriticality;
            })
            .map(investmentCriticalityRepository::save)
            .map(investmentCriticalityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvestmentCriticalityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all InvestmentCriticalities");
        return investmentCriticalityRepository.findAll(pageable).map(investmentCriticalityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InvestmentCriticalityDTO> findOne(Long id) {
        log.debug("Request to get InvestmentCriticality : {}", id);
        return investmentCriticalityRepository.findById(id).map(investmentCriticalityMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete InvestmentCriticality : {}", id);
        investmentCriticalityRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Request to check InvestmentCriticality exists by id: {}", id);
        return investmentCriticalityRepository.existsById(id);
    }

    @Override
    public boolean existsByDescription(String description) {
        log.debug("Request to check InvestmentCriticality exists by description: {}", description);
        Optional<InvestmentCriticality> investmentCriticality = investmentCriticalityRepository
                .findByDescription(description);
        return investmentCriticality.isPresent() ? true : false;
    }

    @Override
    public Long getIdByDescription(String description) {
        log.debug("Request to get InvestmentCriticality id by description: {}", description);
        Optional<InvestmentCriticality> investmentCriticality = investmentCriticalityRepository
                .findByDescription(description);
        return investmentCriticality.isPresent() ? investmentCriticality.get().getId() : null;
    }
}
