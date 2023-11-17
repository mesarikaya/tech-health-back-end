package com.mes.techdebt.service.impl;

import com.mes.techdebt.domain.RecommendationStatus;
import com.mes.techdebt.repository.RecommendationStatusRepository;
import com.mes.techdebt.service.RecommendationStatusService;
import com.mes.techdebt.service.dto.RecommendationStatusDTO;
import com.mes.techdebt.service.mapper.RecommendationStatusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link RecommendationStatus}.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RecommendationStatusServiceImpl implements RecommendationStatusService {

    private final RecommendationStatusRepository recommendationStatusRepository;

    private final RecommendationStatusMapper recommendationStatusMapper;

    @Override
    public RecommendationStatusDTO save(RecommendationStatusDTO recommendationStatusDTO) {
        log.debug("Request to save RecommendationStatus : {}", recommendationStatusDTO);
        RecommendationStatus recommendationStatus = recommendationStatusRepository
                .findByDescription(recommendationStatusDTO.getDescription())
                .orElse(null);

        if(recommendationStatus != null){
            log.debug("No save needed - Existing RecommendationStatus entity: {}", recommendationStatus);
            recommendationStatusDTO.setId(recommendationStatus.getId());
            recommendationStatusMapper.partialUpdate(recommendationStatus, recommendationStatusDTO);
        }else{
            recommendationStatus = recommendationStatusMapper.toEntity(recommendationStatusDTO);
            log.debug("New RecommendationStatus: {}", recommendationStatus);
        }

        recommendationStatus = recommendationStatusRepository.save(recommendationStatus);
        return recommendationStatusMapper.toDto(recommendationStatus);
    }

    @Override
    public RecommendationStatusDTO update(RecommendationStatusDTO recommendationStatusDTO) {
        log.debug("Request to save RecommendationStatus : {}", recommendationStatusDTO);
        RecommendationStatus recommendationStatus = recommendationStatusMapper.toEntity(recommendationStatusDTO);
        recommendationStatus = recommendationStatusRepository.save(recommendationStatus);
        return recommendationStatusMapper.toDto(recommendationStatus);
    }

    @Override
    public Optional<RecommendationStatusDTO> partialUpdate(RecommendationStatusDTO recommendationStatusDTO) {
        log.debug("Request to partially update RecommendationStatus : {}", recommendationStatusDTO);

        return recommendationStatusRepository
            .findById(recommendationStatusDTO.getId())
            .map(existingRecommendationStatus -> {
                recommendationStatusMapper.partialUpdate(existingRecommendationStatus, recommendationStatusDTO);

                return existingRecommendationStatus;
            })
            .map(recommendationStatusRepository::save)
            .map(recommendationStatusMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecommendationStatusDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RecommendationStatuses");
        return recommendationStatusRepository.findAll(pageable).map(recommendationStatusMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RecommendationStatusDTO> findOne(Long id) {
        log.debug("Request to get RecommendationStatus : {}", id);
        return recommendationStatusRepository.findById(id).map(recommendationStatusMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete RecommendationStatus : {}", id);
        recommendationStatusRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Request to check RecommendationStatus exists by id: {}", id);
        return recommendationStatusRepository.existsById(id);
    }

    @Override
    public boolean existsByDescription(String description) {
        log.debug("Request to check RecommendationStatus exists by description: {}", description);
        Optional<RecommendationStatus> recommendationStatus = recommendationStatusRepository.findByDescription(description);
        return recommendationStatus.isPresent() ? true : false;
    }

    @Override
    public Long getIdByDescription(String description) {
        log.debug("Request to get RecommendationStatus id by description: {}", description);
        Optional<RecommendationStatus> recommendationStatus = recommendationStatusRepository.findByDescription(description);
        return recommendationStatus.isPresent() ? recommendationStatus.get().getId() : null;
    }
}
