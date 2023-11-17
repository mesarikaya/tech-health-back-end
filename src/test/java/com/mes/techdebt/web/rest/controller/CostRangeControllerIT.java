package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.IntegrationTest;
import com.mes.techdebt.domain.CostRange;
import com.mes.techdebt.repository.CostRangeRepository;
import com.mes.techdebt.service.dto.CostRangeDTO;
import com.mes.techdebt.service.mapper.CostRangeMapper;
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
 * Integration tests for the {@link CostRangeController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
@Slf4j
class CostRangeControllerIT {
    private static final String ENTITY_API_URL = "/api/v1/cost-ranges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private CostRangeRepository costRangeRepository;

    @Autowired
    private CostRangeMapper costRangeMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restCostRangeMockMvc;

    private CostRange costRange;
    private CostRange newCostRange;


    @BeforeEach
    void setUp() {
        costRange = TestUtil.createCostRangeEntity(TestUtil.DEFAULT_COST_RANGE_DESCRIPTION);
    }

    @Test
    @Transactional
    void createCostRange() throws Exception {
        int databaseSizeBeforeCreate = costRangeRepository.findAll().size();
        // Create the CostRange
        CostRangeDTO costRangeDTO = costRangeMapper.toDto(costRange);
        restCostRangeMockMvc
                .perform(
                        post(ENTITY_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(costRangeDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isCreated());

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeCreate + 1);
        CostRange testCostRange = costRangeList.get(costRangeList.size() - 1);
        assertThat(testCostRange.getDescription()).isEqualTo(TestUtil.DEFAULT_COST_RANGE_DESCRIPTION);
    }

    @Test
    @Transactional
    void createCostRangeWithExistingId() throws Exception {
        //Save the entities
        costRangeRepository.saveAndFlush(costRange);

        // Create the CostRange with an existing ID
        entityManager.detach(costRange);
        costRange.setId(1L);
        CostRangeDTO costRangeDTO = costRangeMapper.toDto(costRange);

        int databaseSizeBeforeCreate = costRangeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        try {
            restCostRangeMockMvc
                    .perform(
                            post(ENTITY_API_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(costRangeDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putCostRangeWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        costRangeRepository.saveAndFlush(costRange);
        newCostRange = TestUtil.createCostRangeEntity(TestUtil.UPDATED_COST_RANGE_DESCRIPTION);
        costRangeRepository.saveAndFlush(newCostRange);

        int databaseSizeBeforeCreate = costRangeRepository.findAll().size();

        // Update the costRange
        try {
            CostRange updatedCostRange = costRangeRepository
                    .findById(newCostRange.getId()).get();
            // Disconnect from session so that the updates on updatedCostRange are not directly saved in db
            entityManager.detach(updatedCostRange);
            updatedCostRange.setDescription(costRange.getDescription());

            CostRangeDTO updatedCostRangeDTO = costRangeMapper.toDto(updatedCostRange);

            // An entity with an existing description cannot be saved, so this API call must fail
            restCostRangeMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedCostRangeDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedCostRangeDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void patchCostRangeWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        costRangeRepository.saveAndFlush(costRange);
        newCostRange = TestUtil.createCostRangeEntity(TestUtil.UPDATED_COST_RANGE_DESCRIPTION);
        costRangeRepository.saveAndFlush(newCostRange);

        int databaseSizeBeforeCreate = costRangeRepository.findAll().size();

        // Update the costRange
        try {
            CostRange updatedCostRange = costRangeRepository
                    .findById(newCostRange.getId()).get();
            // Disconnect from session so that the updates on updatedCostRange are not directly saved in db
            entityManager.detach(updatedCostRange);
            updatedCostRange.setDescription(costRange.getDescription());

            CostRangeDTO updatedCostRangeDTO = costRangeMapper.toDto(updatedCostRange);
            // An entity with an existing description cannot be saved, so this API call must fail
            restCostRangeMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedCostRangeDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedCostRangeDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCostRange() throws Exception {
        // Initialize the database
        costRangeRepository.saveAndFlush(costRange);

        // Get all the costRangeList
        restCostRangeMockMvc
                .perform(
                        get(ENTITY_API_URL + "?sort=id,desc")
                                .with(jwt().authorities(TestUtil.readAuthority))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(costRange.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(Matchers.hasItem(TestUtil.DEFAULT_COST_RANGE_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getNonExistingCostRange() throws Exception {
        //Save the entities
        costRangeRepository.saveAndFlush(costRange);

        // Get the costRange
        restCostRangeMockMvc.perform(
                get(ENTITY_API_URL_ID, Long.MAX_VALUE)
                        .with(jwt().authorities(TestUtil.readAuthority))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCostRangeDescription() throws Exception {
        // Initialize the database
        costRangeRepository.saveAndFlush(costRange);

        int databaseSizeBeforeUpdate = costRangeRepository.findAll().size();

        // Update the costRange
        CostRange updatedCostRange = costRangeRepository.findById(costRange.getId()).get();
        // Disconnect from session so that the updates on updatedCostRange are not directly saved in db
        entityManager.detach(updatedCostRange);
        updatedCostRange.description(TestUtil.UPDATED_COST_RANGE_DESCRIPTION);
        CostRangeDTO costRangeDTO = costRangeMapper.toDto(updatedCostRange);
        restCostRangeMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, costRangeDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(costRangeDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeUpdate);
        CostRange testCostRange = costRangeList.get(costRangeList.size() - 1);
        assertThat(testCostRange.getDescription()).isEqualTo(TestUtil.UPDATED_COST_RANGE_DESCRIPTION);
    }

    @Test
    @Transactional
    void createCostRangeWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        costRangeRepository.saveAndFlush(costRange);

        int databaseSizeBeforeCreate = costRangeRepository.findAll().size();

        // Update the costRange
        try {
            // Disconnect from session so that the updates on updatedCostRange are not directly saved in db
            newCostRange = TestUtil.createCostRangeEntity(TestUtil.UPDATED_COST_RANGE_DESCRIPTION);
            entityManager.detach(newCostRange);
            newCostRange.setDescription(costRange.getDescription());

            CostRangeDTO newCostRangeDTO = costRangeMapper.toDto(newCostRange);

            // An entity with an existing description cannot be saved, so this API call must fail
            restCostRangeMockMvc
                    .perform(
                            post(ENTITY_API_URL, newCostRangeDTO)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(newCostRangeDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putNonExistingCostRange() throws Exception {
        //Save the entities
        costRangeRepository.saveAndFlush(costRange);

        // Create the CostRange with a non-existing ID
        int databaseSizeBeforeUpdate = costRangeRepository.findAll().size();
        entityManager.detach(costRange);
        costRange.setId(count.incrementAndGet());

        // Create the CostRange
        CostRangeDTO costRangeDTO = costRangeMapper.toDto(costRange);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        try {
            restCostRangeMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, costRangeDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(costRangeDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCostRange() throws Exception {
        //Save the entities
        costRangeRepository.saveAndFlush(costRange);

        // Create the CostRange with a non-existing ID
        int databaseSizeBeforeUpdate = costRangeRepository.findAll().size();
        entityManager.detach(costRange);
        costRange.setId(count.incrementAndGet());

        // Create the CostRange
        CostRangeDTO costRangeDTO = costRangeMapper.toDto(costRange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restCostRangeMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(costRangeDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCostRange() throws Exception {
        //Save the entities
        costRangeRepository.saveAndFlush(costRange);

        // Create the CostRange with a non-existing ID
        int databaseSizeBeforeUpdate = costRangeRepository.findAll().size();
        entityManager.detach(costRange);
        costRange.setId(count.incrementAndGet());

        // Create the CostRange
        CostRangeDTO costRangeDTO = costRangeMapper.toDto(costRange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restCostRangeMockMvc
                    .perform(
                            patch(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(costRangeDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());

            // Validate the CostRange in the database
            List<CostRange> costRangeList = costRangeRepository.findAll();
            assertThat(costRangeList).hasSize(databaseSizeBeforeUpdate);
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }
    }

    @Test
    @Transactional
    void partialUpdateCostRangeWithPatch() throws Exception {
        // Initialize the database
        costRangeRepository.saveAndFlush(costRange);

        int databaseSizeBeforeUpdate = costRangeRepository.findAll().size();

        // Update the costRange using partial update
        CostRange partialUpdatedCostRange = new CostRange();
        partialUpdatedCostRange.setId(costRange.getId());
        partialUpdatedCostRange.description(TestUtil.UPDATED_COST_RANGE_DESCRIPTION);
        restCostRangeMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedCostRange.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCostRange))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeUpdate);
        CostRange testCostRange = costRangeList.get(costRangeList.size() - 1);
        assertThat(testCostRange.getDescription()).isEqualTo(TestUtil.UPDATED_COST_RANGE_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateCostRangeWithPatch() throws Exception {
        // Initialize the database
        costRangeRepository.saveAndFlush(costRange);

        int databaseSizeBeforeUpdate = costRangeRepository.findAll().size();

        // Update the costRange using partial update
        CostRange partialUpdatedCostRange = new CostRange();
        partialUpdatedCostRange.setId(costRange.getId());
        partialUpdatedCostRange.description(TestUtil.UPDATED_COST_RANGE_DESCRIPTION);
        restCostRangeMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedCostRange.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCostRange))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeUpdate);
        CostRange testCostRange = costRangeList.get(costRangeList.size() - 1);
        assertThat(testCostRange.getDescription()).isEqualTo(TestUtil.UPDATED_COST_RANGE_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingCostRange() throws Exception {
        //Save the entities
        costRangeRepository.saveAndFlush(costRange);

        // Create the CostRange with a non-existing ID
        int databaseSizeBeforeUpdate = costRangeRepository.findAll().size();
        entityManager.detach(costRange);
        costRange.setId(count.incrementAndGet());

        // Create the CostRange
        CostRangeDTO costRangeDTO = costRangeMapper.toDto(costRange);

        try{
            // If the entity doesn't have an ID, it will throw BadRequestAlertException
            restCostRangeMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, costRangeDTO.getId())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(costRangeDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCostRange() throws Exception {
        //Save the entities
        costRangeRepository.saveAndFlush(costRange);

        // Create the CostRange with a non-existing ID
        int databaseSizeBeforeUpdate = costRangeRepository.findAll().size();
        entityManager.detach(costRange);
        costRange.setId(count.incrementAndGet());

        // Create the CostRange
        CostRangeDTO costRangeDTO = costRangeMapper.toDto(costRange);
        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restCostRangeMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(costRangeDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCostRange() throws Exception {
        //Save the entities
        costRangeRepository.saveAndFlush(costRange);

        // Create the CostRange with a non-existing ID
        int databaseSizeBeforeUpdate = costRangeRepository.findAll().size();
        entityManager.detach(costRange);
        costRange.setId(count.incrementAndGet());

        // Create the CostRange
        CostRangeDTO costRangeDTO = costRangeMapper.toDto(costRange);

        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restCostRangeMockMvc
                    .perform(
                            patch(ENTITY_API_URL)
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(costRangeDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the CostRange in the database
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeUpdate);
    }



    @Test
    @Transactional
    void deleteCostRange() throws Exception {
        // Initialize the database
        costRangeRepository.saveAndFlush(costRange);

        int databaseSizeBeforeDelete = costRangeRepository.findAll().size();

        // Delete the costRange
        restCostRangeMockMvc
                .perform(
                        delete(ENTITY_API_URL_ID, costRange.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CostRange> costRangeList = costRangeRepository.findAll();
        assertThat(costRangeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();

        costRangeRepository.deleteAll();
        List<CostRange> costRangeRepositoryList = costRangeRepository.findAll();
        assertThat(costRangeRepositoryList).hasSize(0);
    }
}