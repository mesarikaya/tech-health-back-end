package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.IntegrationTest;
import com.mes.techdebt.domain.Category;
import com.mes.techdebt.domain.TechArea;
import com.mes.techdebt.domain.TechDomain;
import com.mes.techdebt.repository.CategoryRepository;
import com.mes.techdebt.repository.TechAreaRepository;
import com.mes.techdebt.repository.TechDomainRepository;
import com.mes.techdebt.service.dto.CategoryDTO;
import com.mes.techdebt.service.mapper.CategoryMapper;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static com.mes.techdebt.web.rest.controller.utils.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CategoryController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
@Slf4j
class CategoryControllerIT {


    private static final String ENTITY_API_URL = "/api/v1/categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TechAreaRepository techAreaRepository;
    @Autowired
    private TechDomainRepository techDomainRepository;
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restCategoryMockMvc;

    private Category category;
    private Category newCategory;
    private TechArea techArea;
    private TechDomain techDomain;

    @Transactional
    public void saveAndFlushDependentEntities() {
        techDomainRepository.saveAndFlush(techDomain);
        techAreaRepository.saveAndFlush(techArea);
    }

    @BeforeEach
    void setUp() {
        techDomain = createTechDomainEntity(DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        techArea = createTechAreaEntity(techDomain, DEFAULT_TECH_AREA_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        category = createCategoryEntity(techArea, DEFAULT_CATEGORY_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        saveAndFlushDependentEntities();
    }

    @Test
    @Transactional
    void createCategory() throws Exception {
        // Create the AssessmentCriteria
        int databaseSizeBeforeCreate = categoryRepository.findAll().size();

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);
        restCategoryMockMvc
                .perform(
                        post(ENTITY_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
                                .with(jwt().authorities(adminAuthority))
                )
                .andExpect(status().isCreated());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate + 1);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getDescription()).isEqualTo(DEFAULT_CATEGORY_DESCRIPTION);
    }

    @Test
    @Transactional
    void createCategoryWithExistingId() throws Exception {

        categoryRepository.saveAndFlush(category);

        // Create the Category with an existing ID
        entityManager.detach(category);
        category.setId(1L);
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        int databaseSizeBeforeCreate = categoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        try {
            restCategoryMockMvc
                    .perform(
                            post(ENTITY_API_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putCategoryWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        categoryRepository.saveAndFlush(category);
        newCategory = createCategoryEntity(techArea, UPDATED_CATEGORY_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        categoryRepository.saveAndFlush(newCategory);

        int databaseSizeBeforeCreate = categoryRepository.findAll().size();

        // Update the category
        try {
            Category updatedCategory = categoryRepository
                    .findById(newCategory.getId()).get();
            // Disconnect from session so that the updates on updatedCategory are not directly saved in db
            entityManager.detach(updatedCategory);
            updatedCategory.setDescription(category.getDescription());

            CategoryDTO updatedCategoryDTO = categoryMapper.toDto(updatedCategory);

            // An entity with an existing description cannot be saved, so this API call must fail
            restCategoryMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedCategoryDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedCategoryDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void patchCategoryWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        categoryRepository.saveAndFlush(category);
        newCategory = createCategoryEntity(techArea, UPDATED_CATEGORY_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        categoryRepository.saveAndFlush(newCategory);

        int databaseSizeBeforeCreate = categoryRepository.findAll().size();
        // Update the category
        try {
            Category updatedCategory = categoryRepository
                    .findById(newCategory.getId()).get();
            // Disconnect from session so that the updates on updatedCategory are not directly saved in db
            entityManager.detach(updatedCategory);
            updatedCategory.setDescription(category.getDescription());

            CategoryDTO updatedCategoryDTO = categoryMapper.toDto(updatedCategory);
            // An entity with an existing description cannot be saved, so this API call must fail
            restCategoryMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedCategoryDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedCategoryDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList
        restCategoryMockMvc
                .perform(
                        get(ENTITY_API_URL + "?sort=id,desc")
                                .with(jwt().authorities(readAuthority))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(category.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_CATEGORY_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getNonExistingCategory() throws Exception {
        // Get the category
        restCategoryMockMvc
                .perform(
                        get(ENTITY_API_URL_ID, Long.MAX_VALUE)
                                .with(jwt().authorities(readAuthority))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCategoryDescription() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Update the category
        Category updatedCategory = categoryRepository.findById(category.getId()).get();
        // Disconnect from session so that the updates on updatedCategory are not directly saved in db
        entityManager.detach(updatedCategory);
        updatedCategory.description(UPDATED_CATEGORY_DESCRIPTION);
        CategoryDTO categoryDTO = categoryMapper.toDto(updatedCategory);
        restCategoryMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, categoryDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
                                .with(jwt().authorities(adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getDescription()).isEqualTo(UPDATED_CATEGORY_DESCRIPTION);
    }

    @Test
    @Transactional
    void createCategoryWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeCreate = categoryRepository.findAll().size();
        // Update the category
        try {
            // Disconnect from session so that the updates on updatedCategory are not directly saved in db
            newCategory = createCategoryEntity(techArea, UPDATED_CATEGORY_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
            entityManager.detach(newCategory);
            newCategory.setDescription(category.getDescription());

            CategoryDTO newCategoryDTO = categoryMapper.toDto(newCategory);
            // An entity with an existing description cannot be saved, so this API call must fail
            restCategoryMockMvc
                    .perform(
                            post(ENTITY_API_URL, newCategoryDTO)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(newCategoryDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putNonExistingCategory() throws Exception {
        //Save the entities
        categoryRepository.saveAndFlush(category);
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        entityManager.detach(category);
        category.setId(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);
        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        try {
            restCategoryMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, categoryDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCategory() throws Exception {
        //Save the entities
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        entityManager.detach(category);
        category.setId(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);
        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restCategoryMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCategory() throws Exception {
        //Save the entities
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        entityManager.detach(category);
        category.setId(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);
        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restCategoryMockMvc
                    .perform(
                            patch(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());

            // Validate the Category in the database
            List<Category> categoryList = categoryRepository.findAll();
            assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }
    }

    @Test
    @Transactional
    void partialUpdateCategoryWithPatch() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Update the category using partial update
        Category partialUpdatedCategory = new Category();
        partialUpdatedCategory.setId(category.getId());
        partialUpdatedCategory.description(UPDATED_CATEGORY_DESCRIPTION);
        restCategoryMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedCategory.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCategory))
                                .with(jwt().authorities(adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getDescription()).isEqualTo(UPDATED_CATEGORY_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateCategoryWithPatch() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Update the category using partial update
        Category partialUpdatedCategory = new Category();
        partialUpdatedCategory.setId(category.getId());
        partialUpdatedCategory.description(UPDATED_CATEGORY_DESCRIPTION);
        restCategoryMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedCategory.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCategory))
                                .with(jwt().authorities(adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getDescription()).isEqualTo(UPDATED_CATEGORY_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingCategory() throws Exception {
        //Save the entities
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        entityManager.detach(category);
        category.setId(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);
        try{
            // If the entity doesn't have an ID, it will throw BadRequestAlertException
            restCategoryMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, categoryDTO.getId())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCategory() throws Exception {
        //Save the entities
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        entityManager.detach(category);
        category.setId(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);
        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restCategoryMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCategory() throws Exception {
        //Save the entities
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        entityManager.detach(category);
        category.setId(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);
        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restCategoryMockMvc
                    .perform(
                            patch(ENTITY_API_URL)
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeDelete = categoryRepository.findAll().size();
        // Delete the category
        restCategoryMockMvc
                .perform(
                        delete(ENTITY_API_URL_ID, category.getId())
                                .with(jwt().authorities(adminAuthority))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();

        techDomainRepository.deleteAll();
        TechDomain foundTechDomain = techDomainRepository
                .findByDescription(techDomain.getDescription()).orElse(null);
        assertNull(foundTechDomain);

        techAreaRepository.deleteAll();
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(0);

        categoryRepository.deleteAll();
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(0);
    }
}