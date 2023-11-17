package com.mes.techdebt.bootstrap.initialdata.impl;

import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.domain.Category;
import com.mes.techdebt.domain.TechArea;
import com.mes.techdebt.repository.CategoryRepository;
import com.mes.techdebt.repository.TechAreaRepository;
import com.mes.techdebt.service.dto.CategoryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

/**
 * Migration Data Loader for {@link Category}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CategoryLoadDataServiceImpl implements GenericLoadDataService<CategoryDTO> {

    private final JdbcTemplate jdbcTemplate;

    private final CategoryRepository categoryRepository;

    private final TechAreaRepository techAreaRepository;

    @Override
    public Long saveWithSpecificId(CategoryDTO categoryDTO) {
        String sqlQuery = "insert into category(id, description, tech_area_id, is_active) " +
                "values (?, ?, ?, ?)";

        Category category = categoryRepository.findById(categoryDTO.getId())
                .orElse(null);
        log.debug("Requested category: {}", categoryDTO);
        TechArea techArea = techAreaRepository.findById(categoryDTO.getTech_area_id()).orElse(null);
        if(category == null && techArea != null){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setLong(1, categoryDTO.getId());
                stmt.setString(2, categoryDTO.getDescription());
                stmt.setLong(3, techArea.getId());
                stmt.setBoolean(4, categoryDTO.getIsActive());
                return stmt;
            }, keyHolder);
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        }else{
            return categoryDTO.getId();
        }
    }

    @Override
    public List<Long> saveAllWithSpecificId(List<CategoryDTO> categoryDTOs) {
        log.info("Request to save categoryRepository.count() : {}", categoryRepository.count());
        return categoryRepository.count()>0  ? null : categoryDTOs.stream()
                .map(this::saveWithSpecificId)
                .toList();
    }
}
