package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.IntegrationTest;
import com.mes.techdebt.domain.AssessmentCriteria;
import com.mes.techdebt.domain.Category;
import com.mes.techdebt.domain.TechArea;
import com.mes.techdebt.domain.TechDomain;
import com.mes.techdebt.repository.AssessmentCriteriaRepository;
import com.mes.techdebt.repository.CategoryRepository;
import com.mes.techdebt.repository.TechAreaRepository;
import com.mes.techdebt.repository.TechDomainRepository;
import com.mes.techdebt.service.dto.AssessmentCriteriaDTO;
import com.mes.techdebt.service.mapper.AssessmentCriteriaMapper;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AssessmentCriteriaController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
@Slf4j
class AssessmentCriteriaControllerIT {

    private static final String ENTITY_API_URL = "/api/v1/assessment-criteria";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private AssessmentCriteriaRepository assessmentCriteriaRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TechAreaRepository techAreaRepository;
    @Autowired
    private TechDomainRepository techDomainRepository;
    @Autowired
    private AssessmentCriteriaMapper assessmentCriteriaMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restAssessmentCriteriaMockMvc;

    private AssessmentCriteria assessmentCriteria;
    private AssessmentCriteria newAssessmentCriteria;
    private Category category;
    private TechArea techArea;
    private TechDomain techDomain;

    @Transactional
    public void saveAndFlushDependentEntities() {
        techDomainRepository.saveAndFlush(techDomain);
        techAreaRepository.saveAndFlush(techArea);
        categoryRepository.saveAndFlush(category);
    }

    @BeforeEach
    void setUp() {
        techDomain = TestUtil.createTechDomainEntity(TestUtil.DEFAULT_DOMAIN_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        techArea = TestUtil.createTechAreaEntity(techDomain, TestUtil.DEFAULT_TECH_AREA_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        category = TestUtil.createCategoryEntity(techArea, TestUtil.DEFAULT_CATEGORY_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        assessmentCriteria = TestUtil.createAssessmentCriteriaEntity(category, TestUtil.DEFAULT_CRITERIA_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        saveAndFlushDependentEntities();
    }

    @Test
    @Transactional
    void createAssessmentCriteria() throws Exception {
        int databaseSizeBeforeCreate = assessmentCriteriaRepository.findAll().size();

        // Create the AssessmentCriteria
        AssessmentCriteriaDTO assessmentCriteriaDTO = assessmentCriteriaMapper.toDto(assessmentCriteria);
        restAssessmentCriteriaMockMvc
                .perform(
                        post(ENTITY_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(assessmentCriteriaDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isCreated());

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeCreate + 1);
        AssessmentCriteria testAssessmentCriteria = assessmentCriteriaList.get(assessmentCriteriaList.size() - 1);
        assertThat(testAssessmentCriteria.getDescription()).isEqualTo(TestUtil.DEFAULT_CRITERIA_DESCRIPTION);
        assertThat(testAssessmentCriteria.getIsActive()).isEqualTo(TestUtil.DEFAULT_ACTIVE_FLAG);
    }

    @Test
    @Transactional
    void createAssessmentCriteriaWithExistingId() throws Exception {

        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        // Create the AssessmentCriteria with an existing ID
        entityManager.detach(assessmentCriteria);
        assessmentCriteria.setId(1L);
        AssessmentCriteriaDTO assessmentCriteriaDTO = assessmentCriteriaMapper.toDto(assessmentCriteria);

        int databaseSizeBeforeCreate = assessmentCriteriaRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        try {
            restAssessmentCriteriaMockMvc
                    .perform(
                            post(ENTITY_API_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentCriteriaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putAssessmentCriteriaWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);
        newAssessmentCriteria = TestUtil.createAssessmentCriteriaEntity(category, TestUtil.UPDATED_CRITERIA_DESCRIPTION, TestUtil.UPDATED_ACTIVE_FLAG);
        assessmentCriteriaRepository.saveAndFlush(newAssessmentCriteria);

        int databaseSizeBeforeCreate = assessmentCriteriaRepository.findAll().size();

        // Update the assessmentCriteria
        try {
            AssessmentCriteria updatedAssessmentCriteria = assessmentCriteriaRepository
                    .findById(newAssessmentCriteria.getId()).get();
            // Disconnect from session so that the updates on updatedAssessmentCriteria are not directly saved in db
            entityManager.detach(updatedAssessmentCriteria);
            updatedAssessmentCriteria.setDescription(assessmentCriteria.getDescription());

            AssessmentCriteriaDTO updatedAssessmentCriteriaDTO = assessmentCriteriaMapper.toDto(updatedAssessmentCriteria);

            // An entity with an existing description cannot be saved, so this API call must fail
            restAssessmentCriteriaMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedAssessmentCriteriaDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedAssessmentCriteriaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void patchAssessmentCriteriaWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);
        newAssessmentCriteria = TestUtil.createAssessmentCriteriaEntity(category, TestUtil.UPDATED_CRITERIA_DESCRIPTION, TestUtil.UPDATED_ACTIVE_FLAG);
        assessmentCriteriaRepository.saveAndFlush(newAssessmentCriteria);

        int databaseSizeBeforeCreate = assessmentCriteriaRepository.findAll().size();

        // Update the assessmentCriteria
        try {
            AssessmentCriteria updatedAssessmentCriteria = assessmentCriteriaRepository
                    .findById(newAssessmentCriteria.getId()).get();
            // Disconnect from session so that the updates on updatedAssessmentCriteria are not directly saved in db
            entityManager.detach(updatedAssessmentCriteria);
            updatedAssessmentCriteria.setDescription(assessmentCriteria.getDescription());

            AssessmentCriteriaDTO updatedAssessmentCriteriaDTO = assessmentCriteriaMapper.toDto(updatedAssessmentCriteria);
            // An entity with an existing description cannot be saved, so this API call must fail
            restAssessmentCriteriaMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedAssessmentCriteriaDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedAssessmentCriteriaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAssessmentCriteria() throws Exception {
        // Initialize the database
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        // Get all the assessmentCriteriaList
        restAssessmentCriteriaMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc").with(jwt().authorities(TestUtil.readAuthority)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(assessmentCriteria.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(Matchers.hasItem(TestUtil.DEFAULT_CRITERIA_DESCRIPTION)))
                .andExpect(jsonPath("$.[*].isActive").value(Matchers.hasItem(TestUtil.DEFAULT_ACTIVE_FLAG)));
    }

    @Test
    @Transactional
    void getNonExistingAssessmentCriteria() throws Exception {
        // Get the assessmentCriteria
        restAssessmentCriteriaMockMvc
                .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)
                        .with(jwt().authorities(TestUtil.readAuthority)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAssessmentCriteriaDescription() throws Exception {
        // Initialize the database
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        int databaseSizeBeforeUpdate = assessmentCriteriaRepository.findAll().size();

        // Update the assessmentCriteria
        AssessmentCriteria updatedAssessmentCriteria = assessmentCriteriaRepository.findById(assessmentCriteria.getId()).get();
        // Disconnect from session so that the updates on updatedAssessmentCriteria are not directly saved in db
        entityManager.detach(updatedAssessmentCriteria);
        updatedAssessmentCriteria.description(TestUtil.UPDATED_CRITERIA_DESCRIPTION).isActive(TestUtil.UPDATED_ACTIVE_FLAG);
        AssessmentCriteriaDTO assessmentCriteriaDTO = assessmentCriteriaMapper.toDto(updatedAssessmentCriteria);
        restAssessmentCriteriaMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, assessmentCriteriaDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(assessmentCriteriaDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeUpdate);
        AssessmentCriteria testAssessmentCriteria = assessmentCriteriaList.get(assessmentCriteriaList.size() - 1);
        assertThat(testAssessmentCriteria.getDescription()).isEqualTo(TestUtil.UPDATED_CRITERIA_DESCRIPTION);
        assertThat(testAssessmentCriteria.getIsActive()).isEqualTo(TestUtil.UPDATED_ACTIVE_FLAG);
    }

    @Test
    @Transactional
    void createAssessmentCriteriaWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        int databaseSizeBeforeCreate = assessmentCriteriaRepository.findAll().size();
        // Update the assessmentCriteria
        try {
            // Disconnect from session so that the updates on updatedAssessmentCriteria are not directly saved in db
            newAssessmentCriteria = TestUtil.createAssessmentCriteriaEntity(category, TestUtil.UPDATED_CRITERIA_DESCRIPTION, TestUtil.UPDATED_ACTIVE_FLAG);
            entityManager.detach(newAssessmentCriteria);
            newAssessmentCriteria.setDescription(assessmentCriteria.getDescription());

            AssessmentCriteriaDTO newAssessmentCriteriaDTO = assessmentCriteriaMapper.toDto(newAssessmentCriteria);

            // An entity with an existing description cannot be saved, so this API call must fail
            restAssessmentCriteriaMockMvc
                    .perform(
                            post(ENTITY_API_URL, newAssessmentCriteriaDTO)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(newAssessmentCriteriaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putNonExistingAssessmentCriteria() throws Exception {
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        int databaseSizeBeforeUpdate = assessmentCriteriaRepository.findAll().size();
        entityManager.detach(assessmentCriteria);
        assessmentCriteria.setId(count.incrementAndGet());

        // Create the AssessmentCriteria
        AssessmentCriteriaDTO assessmentCriteriaDTO = assessmentCriteriaMapper.toDto(assessmentCriteria);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        try {
            restAssessmentCriteriaMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, assessmentCriteriaDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentCriteriaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssessmentCriteria() throws Exception {

        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        int databaseSizeBeforeUpdate = assessmentCriteriaRepository.findAll().size();
        entityManager.detach(assessmentCriteria);
        assessmentCriteria.setId(count.incrementAndGet());

        // Create the AssessmentCriteria
        AssessmentCriteriaDTO assessmentCriteriaDTO = assessmentCriteriaMapper.toDto(assessmentCriteria);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restAssessmentCriteriaMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentCriteriaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssessmentCriteria() throws Exception {
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        int databaseSizeBeforeUpdate = assessmentCriteriaRepository.findAll().size();
        entityManager.detach(assessmentCriteria);
        assessmentCriteria.setId(count.incrementAndGet());

        // Create the AssessmentCriteria
        AssessmentCriteriaDTO assessmentCriteriaDTO = assessmentCriteriaMapper.toDto(assessmentCriteria);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restAssessmentCriteriaMockMvc
                    .perform(
                            put(ENTITY_API_URL)
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentCriteriaDTO))
                    )
                    .andExpect(status().isMethodNotAllowed());

            // Validate the AssessmentCriteria in the database
            List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
            assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeUpdate);
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }
    }

    @Test
    @Transactional
    void partialUpdateAssessmentCriteriaWithPatch() throws Exception {
        // Initialize the database
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        int databaseSizeBeforeUpdate = assessmentCriteriaRepository.findAll().size();

        // Update the assessmentCriteria using partial update
        AssessmentCriteria partialUpdatedAssessmentCriteria = new AssessmentCriteria();
        partialUpdatedAssessmentCriteria.setId(assessmentCriteria.getId());
        partialUpdatedAssessmentCriteria.description(TestUtil.UPDATED_CRITERIA_DESCRIPTION);
        restAssessmentCriteriaMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedAssessmentCriteria.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAssessmentCriteria))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeUpdate);
        AssessmentCriteria testAssessmentCriteria = assessmentCriteriaList.get(assessmentCriteriaList.size() - 1);
        assertThat(testAssessmentCriteria.getDescription()).isEqualTo(TestUtil.UPDATED_CRITERIA_DESCRIPTION);
        assertThat(testAssessmentCriteria.getIsActive()).isEqualTo(TestUtil.DEFAULT_ACTIVE_FLAG);
    }

    @Test
    @Transactional
    void fullUpdateAssessmentCriteriaWithPatch() throws Exception {
        // Initialize the database
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        int databaseSizeBeforeUpdate = assessmentCriteriaRepository.findAll().size();

        // Update the assessmentCriteria using partial update
        AssessmentCriteria partialUpdatedAssessmentCriteria = new AssessmentCriteria();
        partialUpdatedAssessmentCriteria.setId(assessmentCriteria.getId());
        partialUpdatedAssessmentCriteria.description(TestUtil.UPDATED_CRITERIA_DESCRIPTION).isActive(TestUtil.UPDATED_ACTIVE_FLAG);
        restAssessmentCriteriaMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedAssessmentCriteria.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAssessmentCriteria))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeUpdate);
        AssessmentCriteria testAssessmentCriteria = assessmentCriteriaList.get(assessmentCriteriaList.size() - 1);
        assertThat(testAssessmentCriteria.getDescription()).isEqualTo(TestUtil.UPDATED_CRITERIA_DESCRIPTION);
        assertThat(testAssessmentCriteria.getIsActive()).isEqualTo(TestUtil.UPDATED_ACTIVE_FLAG);
    }

    @Test
    @Transactional
    void patchNonExistingAssessmentCriteria() throws Exception {
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        int databaseSizeBeforeUpdate = assessmentCriteriaRepository.findAll().size();
        entityManager.detach(assessmentCriteria);
        assessmentCriteria.setId(count.incrementAndGet());

        // Create the AssessmentCriteria
        AssessmentCriteriaDTO assessmentCriteriaDTO = assessmentCriteriaMapper.toDto(assessmentCriteria);
        try{
            // If the entity doesn't have an ID, it will throw BadRequestAlertException
            restAssessmentCriteriaMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, assessmentCriteriaDTO.getId())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentCriteriaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssessmentCriteria() throws Exception {

        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        int databaseSizeBeforeUpdate = assessmentCriteriaRepository.findAll().size();
        entityManager.detach(assessmentCriteria);
        assessmentCriteria.setId(count.incrementAndGet());

        // Create the AssessmentCriteria
        AssessmentCriteriaDTO assessmentCriteriaDTO = assessmentCriteriaMapper.toDto(assessmentCriteria);
        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restAssessmentCriteriaMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentCriteriaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssessmentCriteria() throws Exception {
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        int databaseSizeBeforeUpdate = assessmentCriteriaRepository.findAll().size();
        entityManager.detach(assessmentCriteria);
        assessmentCriteria.setId(count.incrementAndGet());

        // Create the AssessmentCriteria
        AssessmentCriteriaDTO assessmentCriteriaDTO = assessmentCriteriaMapper.toDto(assessmentCriteria);

        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restAssessmentCriteriaMockMvc
                    .perform(
                            patch(ENTITY_API_URL)
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentCriteriaDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the AssessmentCriteria in the database
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssessmentCriteria() throws Exception {
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);

        int databaseSizeBeforeDelete = assessmentCriteriaRepository.findAll().size();

        // Delete the assessmentCriteria
        restAssessmentCriteriaMockMvc
                .perform(delete(ENTITY_API_URL_ID, assessmentCriteria.getId())
                        .with(jwt().authorities(TestUtil.adminAuthority))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(databaseSizeBeforeDelete - 1);
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

        assessmentCriteriaRepository.deleteAll();
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaList).hasSize(0);
    }
}