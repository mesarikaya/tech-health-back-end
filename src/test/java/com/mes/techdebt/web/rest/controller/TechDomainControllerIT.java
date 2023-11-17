package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.IntegrationTest;
import com.mes.techdebt.domain.TechDomain;
import com.mes.techdebt.repository.TechDomainRepository;
import com.mes.techdebt.service.dto.TechDomainDTO;
import com.mes.techdebt.service.mapper.TechDomainMapper;
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
 * Integration tests for the {@link TechDomainController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
@Slf4j
class TechDomainControllerIT {

    private static final String ENTITY_API_URL = "/api/v1/tech-domains";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private TechDomainRepository techDomainRepository;

    @Autowired
    private TechDomainMapper techDomainMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restTechDomainMockMvc;

    private TechDomain techDomain;
    private TechDomain newTechDomain;


    @BeforeEach
    void setUp() {
        techDomain = TestUtil.createTechDomainEntity(TestUtil.DEFAULT_DOMAIN_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
    }

    @Test
    @Transactional
    void createTechDomain() throws Exception {
        int databaseSizeBeforeCreate = techDomainRepository.findAll().size();
        // Create the TechDomain
        TechDomainDTO techDomainDTO = techDomainMapper.toDto(techDomain);
        restTechDomainMockMvc
                .perform(
                        post(ENTITY_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(techDomainDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isCreated());

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeCreate + 1);
        TechDomain testTechDomain = techDomainList.get(techDomainList.size() - 1);
        assertThat(testTechDomain.getDescription()).isEqualTo(TestUtil.DEFAULT_DOMAIN_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTechDomainWithExistingId() throws Exception {
        // Create the TechDomain with an existing ID
        techDomain.setId(1L);
        TechDomainDTO techDomainDTO = techDomainMapper.toDto(techDomain);

        int databaseSizeBeforeCreate = techDomainRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        try {
            restTechDomainMockMvc
                    .perform(
                            post(ENTITY_API_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(techDomainDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putTechDomainWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        techDomainRepository.saveAndFlush(techDomain);
        newTechDomain = TestUtil.createTechDomainEntity(TestUtil.UPDATED_DOMAIN_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        techDomainRepository.saveAndFlush(newTechDomain);

        int databaseSizeBeforeCreate = techDomainRepository.findAll().size();

        // Update the techDomain
        try {
            TechDomain updatedTechDomain = techDomainRepository
                    .findById(newTechDomain.getId()).get();
            // Disconnect from session so that the updates on updatedTechDomain are not directly saved in db
            entityManager.detach(updatedTechDomain);
            updatedTechDomain.setDescription(techDomain.getDescription());

            TechDomainDTO updatedTechDomainDTO = techDomainMapper.toDto(updatedTechDomain);

            // An entity with an existing description cannot be saved, so this API call must fail
            restTechDomainMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedTechDomainDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedTechDomainDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void patchTechDomainWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        techDomainRepository.saveAndFlush(techDomain);
        newTechDomain = TestUtil.createTechDomainEntity(TestUtil.UPDATED_DOMAIN_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        techDomainRepository.saveAndFlush(newTechDomain);

        int databaseSizeBeforeCreate = techDomainRepository.findAll().size();

        // Update the techDomain
        try {
            TechDomain updatedTechDomain = techDomainRepository
                    .findById(newTechDomain.getId()).get();
            // Disconnect from session so that the updates on updatedTechDomain are not directly saved in db
            entityManager.detach(updatedTechDomain);
            updatedTechDomain.setDescription(techDomain.getDescription());

            TechDomainDTO updatedTechDomainDTO = techDomainMapper.toDto(updatedTechDomain);
            // An entity with an existing description cannot be saved, so this API call must fail
            restTechDomainMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedTechDomainDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedTechDomainDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTechDomain() throws Exception {
        // Initialize the database
        techDomainRepository.saveAndFlush(techDomain);

        // Get all the techDomainList
        restTechDomainMockMvc
                .perform(
                        get(ENTITY_API_URL + "?sort=id,desc")
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(techDomain.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(Matchers.hasItem(TestUtil.DEFAULT_DOMAIN_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getNonExistingTechDomain() throws Exception {
        // Get the techDomain
        restTechDomainMockMvc.perform(
                get(ENTITY_API_URL_ID, Long.MAX_VALUE)
                        .with(jwt().authorities(TestUtil.adminAuthority))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTechDomainDescription() throws Exception {
        // Initialize the database
        techDomainRepository.saveAndFlush(techDomain);

        int databaseSizeBeforeUpdate = techDomainRepository.findAll().size();

        // Update the techDomain
        TechDomain updatedTechDomain = techDomainRepository.findById(techDomain.getId()).get();
        // Disconnect from session so that the updates on updatedTechDomain are not directly saved in db
        entityManager.detach(updatedTechDomain);
        updatedTechDomain.description(TestUtil.UPDATED_DOMAIN_DESCRIPTION);
        TechDomainDTO techDomainDTO = techDomainMapper.toDto(updatedTechDomain);
        restTechDomainMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, techDomainDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(techDomainDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeUpdate);
        TechDomain testTechDomain = techDomainList.get(techDomainList.size() - 1);
        assertThat(testTechDomain.getDescription()).isEqualTo(TestUtil.UPDATED_DOMAIN_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTechDomainWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        techDomainRepository.saveAndFlush(techDomain);

        int databaseSizeBeforeCreate = techDomainRepository.findAll().size();

        // Update the techDomain
        try {
            // Disconnect from session so that the updates on updatedTechDomain are not directly saved in db
            newTechDomain = TestUtil.createTechDomainEntity(TestUtil.UPDATED_DOMAIN_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
            entityManager.detach(newTechDomain);
            newTechDomain.setDescription(techDomain.getDescription());

            TechDomainDTO newTechDomainDTO = techDomainMapper.toDto(newTechDomain);

            // An entity with an existing description cannot be saved, so this API call must fail
            restTechDomainMockMvc
                    .perform(
                            post(ENTITY_API_URL, newTechDomainDTO)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(newTechDomainDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putNonExistingTechDomain() throws Exception {
        //Save the entities
        techDomainRepository.saveAndFlush(techDomain);
        int databaseSizeBeforeUpdate = techDomainRepository.findAll().size();
        entityManager.detach(techDomain);
        techDomain.setId(count.incrementAndGet());

        // Create the TechDomain
        TechDomainDTO techDomainDTO = techDomainMapper.toDto(techDomain);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        try {
            restTechDomainMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, techDomainDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(techDomainDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTechDomain() throws Exception {
        int databaseSizeBeforeUpdate = techDomainRepository.findAll().size();
        techDomain.setId(count.incrementAndGet());

        // Create the TechDomain
        TechDomainDTO techDomainDTO = techDomainMapper.toDto(techDomain);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restTechDomainMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(techDomainDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTechDomain() throws Exception {
        techDomainRepository.saveAndFlush(techDomain);
        int databaseSizeBeforeUpdate = techDomainRepository.findAll().size();
        entityManager.detach(techDomain);
        techDomain.setId(count.incrementAndGet());

        // Create the TechDomain
        TechDomainDTO techDomainDTO = techDomainMapper.toDto(techDomain);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restTechDomainMockMvc
                    .perform(
                            patch(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(techDomainDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());

            // Validate the TechDomain in the database
            List<TechDomain> techDomainList = techDomainRepository.findAll();
            assertThat(techDomainList).hasSize(databaseSizeBeforeUpdate);
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }
    }

    @Test
    @Transactional
    void partialUpdateTechDomainWithPatch() throws Exception {
        // Initialize the database
        techDomainRepository.saveAndFlush(techDomain);

        int databaseSizeBeforeUpdate = techDomainRepository.findAll().size();

        // Update the techDomain using partial update
        TechDomain partialUpdatedTechDomain = new TechDomain();
        partialUpdatedTechDomain.setId(techDomain.getId());
        partialUpdatedTechDomain.description(TestUtil.UPDATED_DOMAIN_DESCRIPTION);
        restTechDomainMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedTechDomain.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTechDomain))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeUpdate);
        TechDomain testTechDomain = techDomainList.get(techDomainList.size() - 1);
        assertThat(testTechDomain.getDescription()).isEqualTo(TestUtil.UPDATED_DOMAIN_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateTechDomainWithPatch() throws Exception {
        // Initialize the database
        techDomainRepository.saveAndFlush(techDomain);

        int databaseSizeBeforeUpdate = techDomainRepository.findAll().size();

        // Update the techDomain using partial update
        TechDomain partialUpdatedTechDomain = new TechDomain();
        partialUpdatedTechDomain.setId(techDomain.getId());
        partialUpdatedTechDomain.description(TestUtil.UPDATED_DOMAIN_DESCRIPTION);
        restTechDomainMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedTechDomain.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTechDomain))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeUpdate);
        TechDomain testTechDomain = techDomainList.get(techDomainList.size() - 1);
        assertThat(testTechDomain.getDescription()).isEqualTo(TestUtil.UPDATED_DOMAIN_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingTechDomain() throws Exception {
        techDomainRepository.saveAndFlush(techDomain);
        int databaseSizeBeforeUpdate = techDomainRepository.findAll().size();
        entityManager.detach(techDomain);
        techDomain.setId(count.incrementAndGet());

        // Create the TechDomain
        TechDomainDTO techDomainDTO = techDomainMapper.toDto(techDomain);
        try{
            // If the entity doesn't have an ID, it will throw BadRequestAlertException
            restTechDomainMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, techDomainDTO.getId())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(techDomainDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTechDomain() throws Exception {
        techDomainRepository.saveAndFlush(techDomain);
        int databaseSizeBeforeUpdate = techDomainRepository.findAll().size();
        entityManager.detach(techDomain);
        techDomain.setId(count.incrementAndGet());

        // Create the TechDomain
        TechDomainDTO techDomainDTO = techDomainMapper.toDto(techDomain);

        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restTechDomainMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(techDomainDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTechDomain() throws Exception {
        techDomainRepository.saveAndFlush(techDomain);
        int databaseSizeBeforeUpdate = techDomainRepository.findAll().size();
        entityManager.detach(techDomain);
        techDomain.setId(count.incrementAndGet());

        // Create the TechDomain
        TechDomainDTO techDomainDTO = techDomainMapper.toDto(techDomain);

        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restTechDomainMockMvc
                    .perform(
                            patch(ENTITY_API_URL)
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(techDomainDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the TechDomain in the database
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeUpdate);
    }



    @Test
    @Transactional
    void deleteTechDomain() throws Exception {
        // Initialize the database
        techDomainRepository.saveAndFlush(techDomain);

        int databaseSizeBeforeDelete = techDomainRepository.findAll().size();

        // Delete the techDomain
        restTechDomainMockMvc
                .perform(
                        delete(ENTITY_API_URL_ID, techDomain.getId())
                                .with(jwt().authorities(TestUtil.adminAuthority))
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TechDomain> techDomainList = techDomainRepository.findAll();
        assertThat(techDomainList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();

        techDomainRepository.deleteAll();
        TechDomain foundTechDomain = techDomainRepository
                .findByDescription(techDomain.getDescription()).orElse(null);
        assertNull(foundTechDomain);
    }
}