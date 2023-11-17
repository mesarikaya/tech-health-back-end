package com.mes.techdebt.service.impl;

import com.mes.techdebt.repository.CategoryRepository;
import com.mes.techdebt.domain.Category;
import com.mes.techdebt.service.CategoryService;
import com.mes.techdebt.service.dto.CategoryDTO;
import com.mes.techdebt.service.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Category}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    @Override
    public CategoryDTO save(CategoryDTO categoryDTO) {
        log.debug("Request to save Category : {}", categoryDTO);
        Category category = categoryRepository.findByDescription(categoryDTO.getDescription())
                .orElse(null);

        if(category != null){
            categoryDTO.setId(category.getId());
            categoryMapper.partialUpdate(category, categoryDTO);
        }else{
            category = categoryMapper.toEntity(categoryDTO);
            log.debug("New category: {}", category);
        }

        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDTO update(CategoryDTO categoryDTO) {
        log.debug("Request to save Category : {}", categoryDTO);
        Category category = categoryMapper.toEntity(categoryDTO);
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public Optional<CategoryDTO> partialUpdate(CategoryDTO categoryDTO) {
        log.debug("Request to partially update Category : {}", categoryDTO);

        return categoryRepository
            .findById(categoryDTO.getId())
            .map(existingCategory -> {
                categoryMapper.partialUpdate(existingCategory, categoryDTO);

                return existingCategory;
            })
            .map(categoryRepository::save)
            .map(categoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Categories");

        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> findOne(Long id) {
        log.debug("Request to get Category : {}", id);
        return categoryRepository.findById(id).map(categoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Category : {}", id);
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Request to check Category exists by id: {}", id);
        return categoryRepository.existsById(id);
    }

    @Override
    public boolean existsByDescription(String description) {
        log.debug("Request to check AssessmentCriteria exists by description: {}", description);
        Optional<Category> assessmentCriteria = categoryRepository.findByDescription(description);
        return assessmentCriteria.isPresent() ? true : false;
    }

    @Override
    public Long getIdByDescription(String description) {
        log.debug("Request to get Category id by description: {}", description);
        Optional<Category> category = categoryRepository.findByDescription(description);
        return category.isPresent() ? category.get().getId() : null;
    }
}
