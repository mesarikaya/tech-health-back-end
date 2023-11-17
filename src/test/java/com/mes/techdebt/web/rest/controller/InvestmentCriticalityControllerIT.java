package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.IntegrationTest;
import com.mes.techdebt.domain.InvestmentCriticality;
import com.mes.techdebt.repository.InvestmentCriticalityRepository;
import com.mes.techdebt.service.dto.InvestmentCriticalityDTO;
import com.mes.techdebt.service.mapper.InvestmentCriticalityMapper;
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
 * Integration tests for the {@link InvestmentCriticalityController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
@Slf4j
class InvestmentCriticalityControllerIT {

    private static final String ENTITY_API_URL = "/api/v1/investment-criticalities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private InvestmentCriticalityRepository investmentCriticalityRepository;

    @Autowired
    private InvestmentCriticalityMapper investmentCriticalityMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restInvestmentCriticalityMockMvc;

    private InvestmentCriticality investmentCriticality;
    private InvestmentCriticality newInvestmentCriticality;


    @BeforeEach
    void setUp() {
        investmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.DEFAULT_INVESTMENT_CRITICALITY);
    }

    @Test
    @Transactional
    void createInvestmentCriticality() throws Exception {
        int databaseSizeBeforeCreate = investmentCriticalityRepository.findAll().size();
        // Create the InvestmentCriticality
        InvestmentCriticalityDTO investmentCriticalityDTO = investmentCriticalityMapper.toDto(investmentCriticality);
        restInvestmentCriticalityMockMvc
                .perform(
                        post(ENTITY_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(investmentCriticalityDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isCreated());

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeCreate + 1);
        InvestmentCriticality testInvestmentCriticality = investmentCriticalityList.get(investmentCriticalityList.size() - 1);
        assertThat(testInvestmentCriticality.getDescription()).isEqualTo(TestUtil.DEFAULT_INVESTMENT_CRITICALITY);
    }

    @Test
    @Transactional
    void createInvestmentCriticalityWithExistingId() throws Exception {
        // Create the InvestmentCriticality with an existing ID
        investmentCriticality.setId(1L);
        InvestmentCriticalityDTO investmentCriticalityDTO = investmentCriticalityMapper.toDto(investmentCriticality);

        int databaseSizeBeforeCreate = investmentCriticalityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        try {
            restInvestmentCriticalityMockMvc
                    .perform(
                            post(ENTITY_API_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(investmentCriticalityDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putInvestmentCriticalityWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        investmentCriticalityRepository.saveAndFlush(investmentCriticality);
        newInvestmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);

        int databaseSizeBeforeCreate = investmentCriticalityRepository.findAll().size();

        // Update the investmentCriticality
        try {
            InvestmentCriticality updatedInvestmentCriticality = investmentCriticalityRepository
                    .findById(newInvestmentCriticality.getId()).get();
            // Disconnect from session so that the updates on updatedInvestmentCriticality are not directly saved in db
            entityManager.detach(updatedInvestmentCriticality);
            updatedInvestmentCriticality.setDescription(investmentCriticality.getDescription());

            InvestmentCriticalityDTO updatedInvestmentCriticalityDTO = investmentCriticalityMapper.toDto(updatedInvestmentCriticality);

            // An entity with an existing description cannot be saved, so this API call must fail
            restInvestmentCriticalityMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedInvestmentCriticalityDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedInvestmentCriticalityDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void patchInvestmentCriticalityWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        investmentCriticalityRepository.saveAndFlush(investmentCriticality);
        newInvestmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);

        int databaseSizeBeforeCreate = investmentCriticalityRepository.findAll().size();

        // Update the investmentCriticality
        try {
            InvestmentCriticality updatedInvestmentCriticality = investmentCriticalityRepository
                    .findById(newInvestmentCriticality.getId()).get();
            // Disconnect from session so that the updates on updatedInvestmentCriticality are not directly saved in db
            entityManager.detach(updatedInvestmentCriticality);
            updatedInvestmentCriticality.setDescription(investmentCriticality.getDescription());

            InvestmentCriticalityDTO updatedInvestmentCriticalityDTO = investmentCriticalityMapper.toDto(updatedInvestmentCriticality);
            // An entity with an existing description cannot be saved, so this API call must fail
            restInvestmentCriticalityMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, updatedInvestmentCriticalityDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedInvestmentCriticalityDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInvestmentCriticality() throws Exception {
        // Initialize the database
        investmentCriticalityRepository.saveAndFlush(investmentCriticality);

        // Get all the investmentCriticalityList
        restInvestmentCriticalityMockMvc
                .perform(
                        get(ENTITY_API_URL + "?sort=id,desc")
                                .with(jwt().authorities(TestUtil.readAuthority))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(investmentCriticality.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(Matchers.hasItem(TestUtil.DEFAULT_INVESTMENT_CRITICALITY)));
    }

    @Test
    @Transactional
    void getNonExistingInvestmentCriticality() throws Exception {
        // Get the investmentCriticality
        restInvestmentCriticalityMockMvc.perform(
                get(ENTITY_API_URL_ID, Long.MAX_VALUE)
                        .with(jwt().authorities(TestUtil.adminAuthority))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewInvestmentCriticalityDescription() throws Exception {
        // Initialize the database
        investmentCriticalityRepository.saveAndFlush(investmentCriticality);

        int databaseSizeBeforeUpdate = investmentCriticalityRepository.findAll().size();


        // Update the investmentCriticality
        InvestmentCriticality updatedInvestmentCriticality = investmentCriticalityRepository.findById(investmentCriticality.getId()).get();
        // Disconnect from session so that the updates on updatedInvestmentCriticality are not directly saved in db
        entityManager.detach(updatedInvestmentCriticality);
        updatedInvestmentCriticality.description(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
        InvestmentCriticalityDTO investmentCriticalityDTO = investmentCriticalityMapper.toDto(updatedInvestmentCriticality);
        restInvestmentCriticalityMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, investmentCriticalityDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(investmentCriticalityDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeUpdate);
        InvestmentCriticality testInvestmentCriticality = investmentCriticalityList.get(investmentCriticalityList.size() - 1);
        assertThat(testInvestmentCriticality.getDescription()).isEqualTo(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
    }

    @Test
    @Transactional
    void createInvestmentCriticalityWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        investmentCriticalityRepository.saveAndFlush(investmentCriticality);

        int databaseSizeBeforeCreate = investmentCriticalityRepository.findAll().size();

        // Update the investmentCriticality
        try {
            // Disconnect from session so that the updates on updatedInvestmentCriticality are not directly saved in db
            newInvestmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
            entityManager.detach(newInvestmentCriticality);
            newInvestmentCriticality.setDescription(investmentCriticality.getDescription());

            InvestmentCriticalityDTO newInvestmentCriticalityDTO = investmentCriticalityMapper.toDto(newInvestmentCriticality);

            // An entity with an existing description cannot be saved, so this API call must fail
            restInvestmentCriticalityMockMvc
                    .perform(
                            post(ENTITY_API_URL, newInvestmentCriticalityDTO)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(newInvestmentCriticalityDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putNonExistingInvestmentCriticality() throws Exception {
        int databaseSizeBeforeUpdate = investmentCriticalityRepository.findAll().size();
        investmentCriticality.setId(count.incrementAndGet());

        // Create the InvestmentCriticality
        InvestmentCriticalityDTO investmentCriticalityDTO = investmentCriticalityMapper.toDto(investmentCriticality);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        try {
            restInvestmentCriticalityMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, investmentCriticalityDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(investmentCriticalityDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInvestmentCriticality() throws Exception {
        int databaseSizeBeforeUpdate = investmentCriticalityRepository.findAll().size();
        investmentCriticality.setId(count.incrementAndGet());

        // Create the InvestmentCriticality
        InvestmentCriticalityDTO investmentCriticalityDTO = investmentCriticalityMapper.toDto(investmentCriticality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restInvestmentCriticalityMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(investmentCriticalityDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInvestmentCriticality() throws Exception {
        int databaseSizeBeforeUpdate = investmentCriticalityRepository.findAll().size();
        investmentCriticality.setId(count.incrementAndGet());

        // Create the InvestmentCriticality
        InvestmentCriticalityDTO investmentCriticalityDTO = investmentCriticalityMapper.toDto(investmentCriticality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restInvestmentCriticalityMockMvc
                    .perform(
                            put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(investmentCriticalityDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());

            // Validate the InvestmentCriticality in the database
            List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
            assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeUpdate);
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }
    }

    @Test
    @Transactional
    void partialUpdateInvestmentCriticalityWithPatch() throws Exception {
        // Initialize the database
        investmentCriticalityRepository.saveAndFlush(investmentCriticality);

        int databaseSizeBeforeUpdate = investmentCriticalityRepository.findAll().size();

        // Update the investmentCriticality using partial update
        InvestmentCriticality partialUpdatedInvestmentCriticality = new InvestmentCriticality();
        partialUpdatedInvestmentCriticality.setId(investmentCriticality.getId());

        partialUpdatedInvestmentCriticality.description(TestUtil.UPDATED_INVESTMENT_CRITICALITY);

        restInvestmentCriticalityMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedInvestmentCriticality.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInvestmentCriticality))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeUpdate);
        InvestmentCriticality testInvestmentCriticality = investmentCriticalityList.get(investmentCriticalityList.size() - 1);
        assertThat(testInvestmentCriticality.getDescription()).isEqualTo(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
    }

    @Test
    @Transactional
    void fullUpdateInvestmentCriticalityWithPatch() throws Exception {
        // Initialize the database
        investmentCriticalityRepository.saveAndFlush(investmentCriticality);

        int databaseSizeBeforeUpdate = investmentCriticalityRepository.findAll().size();

        // Update the investmentCriticality using partial update
        InvestmentCriticality partialUpdatedInvestmentCriticality = new InvestmentCriticality();
        partialUpdatedInvestmentCriticality.setId(investmentCriticality.getId());

        partialUpdatedInvestmentCriticality.description(TestUtil.UPDATED_INVESTMENT_CRITICALITY);

        restInvestmentCriticalityMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedInvestmentCriticality.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInvestmentCriticality))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeUpdate);
        InvestmentCriticality testInvestmentCriticality = investmentCriticalityList.get(investmentCriticalityList.size() - 1);
        assertThat(testInvestmentCriticality.getDescription()).isEqualTo(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
    }

    @Test
    @Transactional
    void patchNonExistingInvestmentCriticality() throws Exception {
        int databaseSizeBeforeUpdate = investmentCriticalityRepository.findAll().size();
        investmentCriticality.setId(count.incrementAndGet());

        // Create the InvestmentCriticality
        InvestmentCriticalityDTO investmentCriticalityDTO = investmentCriticalityMapper.toDto(investmentCriticality);

        try{
            // If the entity doesn't have an ID, it will throw BadRequestAlertException
            restInvestmentCriticalityMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, investmentCriticalityDTO.getId())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(investmentCriticalityDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInvestmentCriticality() throws Exception {
        int databaseSizeBeforeUpdate = investmentCriticalityRepository.findAll().size();
        investmentCriticality.setId(count.incrementAndGet());

        // Create the InvestmentCriticality
        InvestmentCriticalityDTO investmentCriticalityDTO = investmentCriticalityMapper.toDto(investmentCriticality);

        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restInvestmentCriticalityMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(investmentCriticalityDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInvestmentCriticality() throws Exception {
        int databaseSizeBeforeUpdate = investmentCriticalityRepository.findAll().size();
        investmentCriticality.setId(count.incrementAndGet());

        // Create the InvestmentCriticality
        InvestmentCriticalityDTO investmentCriticalityDTO = investmentCriticalityMapper.toDto(investmentCriticality);

        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restInvestmentCriticalityMockMvc
                    .perform(
                            patch(ENTITY_API_URL)
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(investmentCriticalityDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the InvestmentCriticality in the database
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeUpdate);
    }
    
    @Test
    @Transactional
    void deleteInvestmentCriticality() throws Exception {
        // Initialize the database
        investmentCriticalityRepository.saveAndFlush(investmentCriticality);

        int databaseSizeBeforeDelete = investmentCriticalityRepository.findAll().size();

        // Delete the investmentCriticality
        restInvestmentCriticalityMockMvc
                .perform(
                        delete(ENTITY_API_URL_ID, investmentCriticality.getId())
                                .with(jwt().authorities(TestUtil.adminAuthority))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();

        investmentCriticalityRepository.deleteAll();
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(0);
    }
}