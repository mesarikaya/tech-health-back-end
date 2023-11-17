package com.mes.techdebt.service.impl;

import com.mes.techdebt.domain.AssessmentResult;
import com.mes.techdebt.repository.AssessmentResultRepository;
import com.mes.techdebt.service.AssessmentResultService;
import com.mes.techdebt.service.dto.AssessmentResultDTO;
import com.mes.techdebt.service.mapper.AssessmentResultMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link AssessmentResult}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AssessmentResultServiceImpl implements AssessmentResultService {
    private final AssessmentResultRepository assessmentResultRepository;
    private final AssessmentResultMapper assessmentResultMapper;
    @Override
    public AssessmentResultDTO save(AssessmentResultDTO assessmentResultDTO) {
        log.debug("Request to save AssessmentResult : {}", assessmentResultDTO);
        AssessmentResult assessmentResult = assessmentResultMapper.toEntity(assessmentResultDTO);
        log.debug("New AssessmentResult: {}", assessmentResult);
        assessmentResult = assessmentResultRepository.save(assessmentResult);
        return assessmentResultMapper.toDto(assessmentResult);
    }

    @Override
    public AssessmentResultDTO update(AssessmentResultDTO assessmentResultDTO) {
        log.debug("Request to save AssessmentResult : {}", assessmentResultDTO);
        AssessmentResult assessmentResult = assessmentResultMapper.toEntity(assessmentResultDTO);
        assessmentResult = assessmentResultRepository.save(assessmentResult);
        return assessmentResultMapper.toDto(assessmentResult);
    }

    @Override
    public Optional<AssessmentResultDTO> partialUpdate(AssessmentResultDTO assessmentResultDTO) {
        log.debug("Request to partially update AssessmentResult : {}", assessmentResultDTO);

        return assessmentResultRepository
            .findById(assessmentResultDTO.getId())
            .map(existingAssessmentResult -> {
                assessmentResultMapper.partialUpdate(existingAssessmentResult, assessmentResultDTO);

                return existingAssessmentResult;
            })
            .map(assessmentResultRepository::save)
            .map(assessmentResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssessmentResultDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AssessmentResults");
        return assessmentResultRepository.findAll(pageable).map(assessmentResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AssessmentResultDTO> findOne(Long id) {
        log.debug("Request to get AssessmentResult : {}", id);
        return assessmentResultRepository.findById(id).map(assessmentResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<AssessmentResultDTO>> findBySiteName(String siteName) {
        log.debug("Request to get all AssessmentResults for site: {}", siteName);
        return assessmentResultRepository.findBySite_NameOrderByUpdateDateDesc(siteName)
                .map(assessmentResultMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete AssessmentResult : {}", id);
        assessmentResultRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Request to check AssessmentResult exists by id: {}", id);
        return assessmentResultRepository.existsById(id);
    }

    @Override
    public long countByAssessmentCriteriaId(Long id){

        return assessmentResultRepository.countByAssessmentCriteria_Id(id);
    }

    @Override
    public long countByCategoryId(Long id) {
        return assessmentResultRepository.countByAssessmentCriteria_Category_Id(id);
    }

    @Override
    public long countByTechAreaId(Long id) {
        return assessmentResultRepository.countByAssessmentCriteria_Category_TechArea_Id(id);
    }

    @Override
    public long countByTechDomainId(Long id) {
        return assessmentResultRepository.countByAssessmentCriteria_Category_TechArea_Domain_Id(id);
    }
}
