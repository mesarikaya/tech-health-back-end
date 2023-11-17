package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.IntegrationTest;
import com.mes.techdebt.domain.RecommendationStatus;
import com.mes.techdebt.repository.RecommendationStatusRepository;
import com.mes.techdebt.service.dto.RecommendationStatusDTO;
import com.mes.techdebt.service.mapper.RecommendationStatusMapper;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link RecommendationStatusController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
@Slf4j
class RecommendationStatusControllerIT {

    private static final String ENTITY_API_URL = "/api/v1/recommendation-statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private RecommendationStatusRepository recommendationStatusRepository;

    @Autowired
    private RecommendationStatusMapper recommendationStatusMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restRecommendationStatusMockMvc;

    private RecommendationStatus recommendationStatus;
    private RecommendationStatus newRecommendationStatus;

    @BeforeEach
    void setUp() {
        recommendationStatus = TestUtil.createRecommendationStatusEntity(TestUtil.DEFAULT_RECOMMENDATION_STATUS);
    }

    @Test
    @Transactional
    void createRecommendationStatus() throws Exception {

        int databaseSizeBeforeCreate = recommendationStatusRepository.findAll().size();
        // Create the RecommendationStatus
        RecommendationStatusDTO recommendationStatusDTO = recommendationStatusMapper.toDto(recommendationStatus);
        restRecommendationStatusMockMvc
                .perform(
                        post(ENTITY_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(recommendationStatusDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isCreated());

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeCreate + 1);
        RecommendationStatus testRecommendationStatus = recommendationStatusList.get(recommendationStatusList.size() - 1);
        assertThat(testRecommendationStatus.getDescription()).isEqualTo(TestUtil.DEFAULT_RECOMMENDATION_STATUS);
    }

    @Test
    @Transactional
    void createRecommendationStatusWithExistingId() throws Exception {
        //Save the entities
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        // Create the RecommendationStatus with an existing ID
        entityManager.detach(recommendationStatus);
        recommendationStatus.setId(1L);
        RecommendationStatusDTO recommendationStatusDTO = recommendationStatusMapper.toDto(recommendationStatus);

        int databaseSizeBeforeCreate = recommendationStatusRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        try {
            restRecommendationStatusMockMvc
                    .perform(
                            post(ENTITY_API_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(recommendationStatusDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putRecommendationStatusWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        recommendationStatusRepository.saveAndFlush(recommendationStatus);
        newRecommendationStatus = TestUtil.createRecommendationStatusEntity(TestUtil.UPDATED_RECOMMENDATION_STATUS);
        recommendationStatusRepository.saveAndFlush(newRecommendationStatus);

        int databaseSizeBeforeCreate = recommendationStatusRepository.findAll().size();

        // Update the recommendationStatus
        try {
            RecommendationStatus updatedRecommendationStatus = recommendationStatusRepository
                    .findById(newRecommendationStatus.getId()).get();
            // Disconnect from session so that the updates on updatedRecommendationStatus are not directly saved in db
            entityManager.detach(updatedRecommendationStatus);
            updatedRecommendationStatus.setDescription(recommendationStatus.getDescription());

            RecommendationStatusDTO updatedRecommendationStatusDTO = recommendationStatusMapper.toDto(updatedRecommendationStatus);

            // An entity with an existing description cannot be saved, so this API call must fail
            restRecommendationStatusMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedRecommendationStatusDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedRecommendationStatusDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void patchRecommendationStatusWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        recommendationStatusRepository.saveAndFlush(recommendationStatus);
        newRecommendationStatus = TestUtil.createRecommendationStatusEntity(TestUtil.UPDATED_RECOMMENDATION_STATUS);
        recommendationStatusRepository.saveAndFlush(newRecommendationStatus);

        int databaseSizeBeforeCreate = recommendationStatusRepository.findAll().size();

        // Update the recommendationStatus
        try {
            RecommendationStatus updatedRecommendationStatus = recommendationStatusRepository
                    .findById(newRecommendationStatus.getId()).get();
            // Disconnect from session so that the updates on updatedRecommendationStatus are not directly saved in db
            entityManager.detach(updatedRecommendationStatus);
            updatedRecommendationStatus.setDescription(recommendationStatus.getDescription());

            RecommendationStatusDTO updatedRecommendationStatusDTO = recommendationStatusMapper.toDto(updatedRecommendationStatus);
            // An entity with an existing description cannot be saved, so this API call must fail
            restRecommendationStatusMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, updatedRecommendationStatusDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedRecommendationStatusDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRecommendationStatus() throws Exception {
        // Initialize the database
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        // Get all the recommendationStatusList
        restRecommendationStatusMockMvc
                .perform(
                        get(ENTITY_API_URL + "?sort=id,desc")
                                .with(jwt().authorities(TestUtil.readAuthority))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(recommendationStatus.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(Matchers.hasItem(TestUtil.DEFAULT_RECOMMENDATION_STATUS)));
    }

    @Test
    @Transactional
    void getNonExistingRecommendationStatus() throws Exception {
        // Get the recommendationStatus
        restRecommendationStatusMockMvc.perform(
                get(ENTITY_API_URL_ID, Long.MAX_VALUE)
                        .with(jwt().authorities(TestUtil.readAuthority))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRecommendationStatusDescription() throws Exception {
        // Initialize the database
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        int databaseSizeBeforeUpdate = recommendationStatusRepository.findAll().size();


        // Update the recommendationStatus
        RecommendationStatus updatedRecommendationStatus = recommendationStatusRepository.findById(recommendationStatus.getId()).get();
        // Disconnect from session so that the updates on updatedRecommendationStatus are not directly saved in db
        entityManager.detach(updatedRecommendationStatus);
        updatedRecommendationStatus.description(TestUtil.UPDATED_RECOMMENDATION_STATUS);
        RecommendationStatusDTO recommendationStatusDTO = recommendationStatusMapper.toDto(updatedRecommendationStatus);
        restRecommendationStatusMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, recommendationStatusDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(recommendationStatusDTO))
                                .with(jwt().authorities(TestUtil.writeAuthority))
                )
                .andExpect(status().isOk());

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeUpdate);
        RecommendationStatus testRecommendationStatus = recommendationStatusList.get(recommendationStatusList.size() - 1);
        assertThat(testRecommendationStatus.getDescription()).isEqualTo(TestUtil.UPDATED_RECOMMENDATION_STATUS);
    }

    @Test
    @Transactional
    void createRecommendationStatusWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        int databaseSizeBeforeCreate = recommendationStatusRepository.findAll().size();

        // Update the recommendationStatus
        try {
            // Disconnect from session so that the updates on updatedRecommendationStatus are not directly saved in db
            newRecommendationStatus = TestUtil.createRecommendationStatusEntity(TestUtil.DEFAULT_RECOMMENDATION_STATUS);
            entityManager.detach(newRecommendationStatus);
            newRecommendationStatus.setDescription(recommendationStatus.getDescription());

            RecommendationStatusDTO newRecommendationStatusDTO = recommendationStatusMapper.toDto(newRecommendationStatus);

            // An entity with an existing description cannot be saved, so this API call must fail
            restRecommendationStatusMockMvc
                    .perform(
                            post(ENTITY_API_URL, newRecommendationStatusDTO)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(newRecommendationStatusDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putNonExistingRecommendationStatus() throws Exception {
        //Save the entities
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        // Create the RecommendationStatus with an existing ID
        int databaseSizeBeforeUpdate = recommendationStatusRepository.findAll().size();
        entityManager.detach(recommendationStatus);
        recommendationStatus.setId(count.incrementAndGet());

        // Create the RecommendationStatus
        RecommendationStatusDTO recommendationStatusDTO = recommendationStatusMapper.toDto(recommendationStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        try {
            restRecommendationStatusMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, recommendationStatusDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(recommendationStatusDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRecommendationStatus() throws Exception {
        //Save the entities
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        // Create the RecommendationStatus with an existing ID
        int databaseSizeBeforeUpdate = recommendationStatusRepository.findAll().size();
        entityManager.detach(recommendationStatus);
        recommendationStatus.setId(count.incrementAndGet());

        // Create the RecommendationStatus
        RecommendationStatusDTO recommendationStatusDTO = recommendationStatusMapper.toDto(recommendationStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restRecommendationStatusMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(recommendationStatusDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRecommendationStatus() throws Exception {
        //Save the entities
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        // Create the RecommendationStatus with an existing ID
        int databaseSizeBeforeUpdate = recommendationStatusRepository.findAll().size();
        entityManager.detach(recommendationStatus);
        recommendationStatus.setId(count.incrementAndGet());

        // Create the RecommendationStatus
        RecommendationStatusDTO recommendationStatusDTO = recommendationStatusMapper.toDto(recommendationStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restRecommendationStatusMockMvc
                    .perform(
                            put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(recommendationStatusDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());

            // Validate the RecommendationStatus in the database
            List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
            assertThat(recommendationStatusList).hasSize(databaseSizeBeforeUpdate);
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }
    }

    @Test
    @Transactional
    void partialUpdateRecommendationStatusWithPatch() throws Exception {
        // Initialize the database
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        int databaseSizeBeforeUpdate = recommendationStatusRepository.findAll().size();

        // Update the recommendationStatus using partial update
        RecommendationStatus partialUpdatedRecommendationStatus = new RecommendationStatus();
        partialUpdatedRecommendationStatus.setId(recommendationStatus.getId());
        partialUpdatedRecommendationStatus.description(TestUtil.UPDATED_RECOMMENDATION_STATUS);
        restRecommendationStatusMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedRecommendationStatus.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecommendationStatus))
                                .with(jwt().authorities(TestUtil.writeAuthority))
                )
                .andExpect(status().isOk());

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeUpdate);
        RecommendationStatus testRecommendationStatus = recommendationStatusList.get(recommendationStatusList.size() - 1);
        assertThat(testRecommendationStatus.getDescription()).isEqualTo(TestUtil.UPDATED_RECOMMENDATION_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateRecommendationStatusWithPatch() throws Exception {
        // Initialize the database
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        int databaseSizeBeforeUpdate = recommendationStatusRepository.findAll().size();

        // Update the recommendationStatus using partial update
        RecommendationStatus partialUpdatedRecommendationStatus = new RecommendationStatus();
        partialUpdatedRecommendationStatus.setId(recommendationStatus.getId());
        partialUpdatedRecommendationStatus.description(TestUtil.UPDATED_RECOMMENDATION_STATUS);
        restRecommendationStatusMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedRecommendationStatus.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecommendationStatus))
                                .with(jwt().authorities(TestUtil.writeAuthority))
                )
                .andExpect(status().isOk());

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeUpdate);
        RecommendationStatus testRecommendationStatus = recommendationStatusList.get(recommendationStatusList.size() - 1);
        assertThat(testRecommendationStatus.getDescription()).isEqualTo(TestUtil.UPDATED_RECOMMENDATION_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingRecommendationStatus() throws Exception {
        //Save the entities
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        // Create the RecommendationStatus with an existing ID
        int databaseSizeBeforeUpdate = recommendationStatusRepository.findAll().size();
        entityManager.detach(recommendationStatus);
        recommendationStatus.setId(count.incrementAndGet());

        // Create the RecommendationStatus
        RecommendationStatusDTO recommendationStatusDTO = recommendationStatusMapper.toDto(recommendationStatus);
        try{
            // If the entity doesn't have an ID, it will throw BadRequestAlertException
            restRecommendationStatusMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, recommendationStatusDTO.getId())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(recommendationStatusDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRecommendationStatus() throws Exception {
        //Save the entities
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        // Create the RecommendationStatus with an existing ID
        int databaseSizeBeforeUpdate = recommendationStatusRepository.findAll().size();
        entityManager.detach(recommendationStatus);
        recommendationStatus.setId(count.incrementAndGet());

        // Create the RecommendationStatus
        RecommendationStatusDTO recommendationStatusDTO = recommendationStatusMapper.toDto(recommendationStatus);

        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restRecommendationStatusMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(recommendationStatusDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRecommendationStatus() throws Exception {
        //Save the entities
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        // Create the RecommendationStatus with an existing ID
        int databaseSizeBeforeUpdate = recommendationStatusRepository.findAll().size();
        entityManager.detach(recommendationStatus);
        recommendationStatus.setId(count.incrementAndGet());

        // Create the RecommendationStatus
        RecommendationStatusDTO recommendationStatusDTO = recommendationStatusMapper.toDto(recommendationStatus);
        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restRecommendationStatusMockMvc
                    .perform(
                            patch(ENTITY_API_URL)
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(recommendationStatusDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the RecommendationStatus in the database
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeUpdate);
    }



    @Test
    @Transactional
    void deleteRecommendationStatus() throws Exception {
        // Initialize the database
        recommendationStatusRepository.saveAndFlush(recommendationStatus);

        int databaseSizeBeforeDelete = recommendationStatusRepository.findAll().size();

        // Delete the recommendationStatus
        restRecommendationStatusMockMvc
                .perform(
                        delete(ENTITY_API_URL_ID, recommendationStatus.getId())
                                .with(jwt().authorities(TestUtil.adminAuthority))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();

        recommendationStatusRepository.deleteAll();
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(recommendationStatusList).hasSize(0);
    }
}