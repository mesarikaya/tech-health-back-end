package com.mes.techdebt.service.impl;

import com.mes.techdebt.domain.AssessmentCriteria;
import com.mes.techdebt.repository.AssessmentCriteriaRepository;
import com.mes.techdebt.service.AssessmentCriteriaService;
import com.mes.techdebt.service.dto.AssessmentCriteriaDTO;
import com.mes.techdebt.service.mapper.AssessmentCriteriaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link AssessmentCriteria}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AssessmentCriteriaServiceImpl implements AssessmentCriteriaService {
    private final AssessmentCriteriaRepository assessmentCriteriaRepository;
    private final AssessmentCriteriaMapper assessmentCriteriaMapper;

    @Override
    public AssessmentCriteriaDTO save(AssessmentCriteriaDTO assessmentCriteriaDTO) {
        log.debug("Request to save AssessmentCriteria : {}", assessmentCriteriaDTO);
        AssessmentCriteria assessmentCriteria = assessmentCriteriaRepository
                .findByDescription(assessmentCriteriaDTO.getDescription())
                .orElse(null);

        if(assessmentCriteria != null){
            assessmentCriteriaDTO.setId(assessmentCriteria.getId());
            assessmentCriteriaMapper.partialUpdate(assessmentCriteria, assessmentCriteriaDTO);
        }else{
            assessmentCriteria = assessmentCriteriaMapper.toEntity(assessmentCriteriaDTO);
            log.debug("New AssessmentCriteria: {}", assessmentCriteria);
        }
        assessmentCriteria = assessmentCriteriaRepository.save(assessmentCriteria);
        return assessmentCriteriaMapper.toDto(assessmentCriteria);
    }

    @Override
    public AssessmentCriteriaDTO update(AssessmentCriteriaDTO assessmentCriteriaDTO) {
        log.debug("Request to save AssessmentCriteria : {}", assessmentCriteriaDTO);
        AssessmentCriteria assessmentCriteria = assessmentCriteriaMapper.toEntity(assessmentCriteriaDTO);
        assessmentCriteria = assessmentCriteriaRepository.save(assessmentCriteria);
        return assessmentCriteriaMapper.toDto(assessmentCriteria);
    }

    @Override
    public Optional<AssessmentCriteriaDTO> partialUpdate(AssessmentCriteriaDTO assessmentCriteriaDTO) {
        log.debug("Request to partially update AssessmentCriteria : {}", assessmentCriteriaDTO);

        return assessmentCriteriaRepository
            .findById(assessmentCriteriaDTO.getId())
            .map(existingAssesmentCriteria -> {
                assessmentCriteriaMapper.partialUpdate(existingAssesmentCriteria, assessmentCriteriaDTO);

                return existingAssesmentCriteria;
            })
            .map(assessmentCriteriaRepository::save)
            .map(assessmentCriteriaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssessmentCriteriaDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AssessmentCriteria");
        return assessmentCriteriaRepository.findAll(pageable).map(assessmentCriteriaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AssessmentCriteriaDTO> findOne(Long id) {
        log.debug("Request to get AssessmentCriteria : {}", id);
        return assessmentCriteriaRepository.findById(id).map(assessmentCriteriaMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete AssessmentCriteria : {}", id);
        assessmentCriteriaRepository.deleteById(id);
    }


    @Override
    public boolean existsById(Long id) {
        log.debug("Request to check AssessmentCriteria exists by id: {}", id);
        return assessmentCriteriaRepository.existsById(id);
    }

    @Override
    public boolean existsByDescription(String description) {
        log.debug("Request to check AssessmentCriteria exists by description: {}", description);
        Optional<AssessmentCriteria> assessmentCriteria = assessmentCriteriaRepository.findByDescription(description);
        return assessmentCriteria.isPresent();
    }

    @Override
    public Long getIdByDescription(String description) {
        log.debug("Request to get AssessmentCriteria id by description: {}", description);
        Optional<AssessmentCriteria> assessmentCriteria = assessmentCriteriaRepository.findByDescription(description);
        return assessmentCriteria.isPresent() ? assessmentCriteria.get().getId() : null;
    }
}
