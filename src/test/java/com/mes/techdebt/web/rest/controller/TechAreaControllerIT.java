package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.IntegrationTest;
import com.mes.techdebt.domain.TechArea;
import com.mes.techdebt.domain.TechDomain;
import com.mes.techdebt.repository.TechAreaRepository;
import com.mes.techdebt.repository.TechDomainRepository;
import com.mes.techdebt.service.dto.TechAreaDTO;
import com.mes.techdebt.service.mapper.TechAreaMapper;
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
 * Integration tests for the {@link TechAreaController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
@Slf4j
class TechAreaControllerIT {

    private static final String ENTITY_API_URL = "/api/v1/tech-areas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private TechAreaRepository techAreaRepository;

    @Autowired
    private TechDomainRepository techDomainRepository;

    @Autowired
    private TechAreaMapper techAreaMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restTechAreaMockMvc;

    private TechArea techArea;
    private TechArea newTechArea;
    private TechDomain techDomain;


    @BeforeEach
    void setUp() {
        techDomain = TestUtil.createTechDomainEntity(TestUtil.DEFAULT_DOMAIN_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        techArea = TestUtil.createTechAreaEntity(techDomain, TestUtil.DEFAULT_TECH_AREA_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        saveAndFlushEntities();
    }

    @Transactional
    public void saveAndFlushEntities() {
        techDomainRepository.saveAndFlush(techDomain);
    }

    @Test
    @Transactional
    void createTechArea() throws Exception {
        int databaseSizeBeforeCreate = techAreaRepository.findAll().size();

        // Create the TechArea
        TechAreaDTO techAreaDTO = techAreaMapper.toDto(techArea);
        restTechAreaMockMvc
                .perform(
                        post(ENTITY_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(techAreaDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isCreated());

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeCreate + 1);
        TechArea testTechArea = techAreaList.get(techAreaList.size() - 1);
        assertThat(testTechArea.getDescription()).isEqualTo(TestUtil.DEFAULT_TECH_AREA_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTechAreaWithExistingId() throws Exception {
        // Create the TechArea with an existing ID
        techAreaRepository.saveAndFlush(techArea);

        int databaseSizeBeforeCreate = techAreaRepository.findAll().size();
        entityManager.detach(techArea);
        techArea.setId(1L);
        TechAreaDTO techAreaDTO = techAreaMapper.toDto(techArea);

        // An entity with an existing ID cannot be created, so this API call must fail
        try {
            restTechAreaMockMvc
                    .perform(
                            post(ENTITY_API_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(techAreaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putTechAreaWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        saveAndFlushEntities();
        techAreaRepository.saveAndFlush(techArea);
        newTechArea = TestUtil.createTechAreaEntity(techDomain, TestUtil.UPDATED_TECH_AREA_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        techAreaRepository.saveAndFlush(newTechArea);

        int databaseSizeBeforeCreate = techAreaRepository.findAll().size();
        // Update the techArea
        try {
            TechArea updatedTechArea = techAreaRepository
                    .findById(newTechArea.getId()).get();
            // Disconnect from session so that the updates on updatedTechArea are not directly saved in db
            entityManager.detach(updatedTechArea);
            updatedTechArea.setDescription(techArea.getDescription());

            TechAreaDTO updatedTechAreaDTO = techAreaMapper.toDto(updatedTechArea);
            // An entity with an existing description cannot be saved, so this API call must fail
            restTechAreaMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedTechAreaDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedTechAreaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void patchTechAreaWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        //Save the entities
        saveAndFlushEntities();
        techAreaRepository.saveAndFlush(techArea);
        newTechArea = TestUtil.createTechAreaEntity(techDomain, TestUtil.UPDATED_TECH_AREA_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        techAreaRepository.saveAndFlush(newTechArea);

        int databaseSizeBeforeCreate = techAreaRepository.findAll().size();
        // Update the techArea
        try {
            TechArea updatedTechArea = techAreaRepository
                    .findById(newTechArea.getId()).get();
            // Disconnect from session so that the updates on updatedTechArea are not directly saved in db
            entityManager.detach(updatedTechArea);
            updatedTechArea.setDescription(techArea.getDescription());

            TechAreaDTO updatedTechAreaDTO = techAreaMapper.toDto(updatedTechArea);
            // An entity with an existing description cannot be saved, so this API call must fail
            restTechAreaMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedTechAreaDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedTechAreaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTechArea() throws Exception {
        // Initialize the database
        saveAndFlushEntities();
        techAreaRepository.saveAndFlush(techArea);

        // Get all the techAreaList
        restTechAreaMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc")
                        .with(jwt().authorities(TestUtil.readAuthority)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(techArea.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(Matchers.hasItem(TestUtil.DEFAULT_TECH_AREA_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getNonExistingTechArea() throws Exception {
        // Get the techArea
        restTechAreaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)
                .with(jwt().authorities(TestUtil.readAuthority)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTechAreaDescription() throws Exception {
        // Initialize the database
        techAreaRepository.saveAndFlush(techArea);
        int databaseSizeBeforeUpdate = techAreaRepository.findAll().size();

        // Update the techArea
        TechArea updatedTechArea = techAreaRepository.findById(techArea.getId()).get();
        // Disconnect from session so that the updates on updatedTechArea are not directly saved in db
        entityManager.detach(updatedTechArea);
        updatedTechArea.description(TestUtil.UPDATED_TECH_AREA_DESCRIPTION);
        TechAreaDTO techAreaDTO = techAreaMapper.toDto(updatedTechArea);
        restTechAreaMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, techAreaDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(techAreaDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeUpdate);
        TechArea testTechArea = techAreaList.get(techAreaList.size() - 1);
        assertThat(testTechArea.getDescription()).isEqualTo(TestUtil.UPDATED_TECH_AREA_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTechAreaWithExistingDescription() throws Exception {
        // Try to update an entity to another already existing description
        techAreaRepository.saveAndFlush(techArea);

        int databaseSizeBeforeCreate = techAreaRepository.findAll().size();
        // Update the techArea
        try {
            // Disconnect from session so that the updates on updatedTechArea are not directly saved in db
            newTechArea = TestUtil.createTechAreaEntity(techDomain, TestUtil.UPDATED_TECH_AREA_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
            entityManager.detach(newTechArea);
            newTechArea.setDescription(techArea.getDescription());

            TechAreaDTO newTechAreaDTO = techAreaMapper.toDto(newTechArea);
            // An entity with an existing description cannot be saved, so this API call must fail
            restTechAreaMockMvc
                    .perform(
                            post(ENTITY_API_URL, newTechAreaDTO)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(newTechAreaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putNonExistingTechArea() throws Exception {
        //Save the entities
        techAreaRepository.saveAndFlush(techArea);

        int databaseSizeBeforeUpdate = techAreaRepository.findAll().size();
        entityManager.detach(techArea);
        techArea.setId(count.incrementAndGet());

        // Create the TechArea
        TechAreaDTO techAreaDTO = techAreaMapper.toDto(techArea);
        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        try {
            restTechAreaMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, techAreaDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(techAreaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTechArea() throws Exception {
        //Save the entities
        techAreaRepository.saveAndFlush(techArea);

        int databaseSizeBeforeUpdate = techAreaRepository.findAll().size();
        entityManager.detach(techArea);
        techArea.setId(count.incrementAndGet());

        // Create the TechArea
        TechAreaDTO techAreaDTO = techAreaMapper.toDto(techArea);
        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restTechAreaMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(techAreaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTechArea() throws Exception {
        //Save the entities
        techAreaRepository.saveAndFlush(techArea);

        int databaseSizeBeforeUpdate = techAreaRepository.findAll().size();
        entityManager.detach(techArea);
        techArea.setId(count.incrementAndGet());

        // Create the TechArea
        TechAreaDTO techAreaDTO = techAreaMapper.toDto(techArea);
        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restTechAreaMockMvc
                    .perform(
                            put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(techAreaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());

            // Validate the TechArea in the database
            List<TechArea> techAreaList = techAreaRepository.findAll();
            assertThat(techAreaList).hasSize(databaseSizeBeforeUpdate);
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }
    }

    @Test
    @Transactional
    void partialUpdateTechAreaWithPatch() throws Exception {
        // Initialize the database
        techAreaRepository.saveAndFlush(techArea);

        int databaseSizeBeforeUpdate = techAreaRepository.findAll().size();

        // Update the techArea using partial update
        TechArea partialUpdatedTechArea = new TechArea();
        partialUpdatedTechArea.setId(techArea.getId());
        partialUpdatedTechArea.description(TestUtil.UPDATED_TECH_AREA_DESCRIPTION);
        restTechAreaMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedTechArea.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTechArea))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeUpdate);
        TechArea testTechArea = techAreaList.get(techAreaList.size() - 1);
        assertThat(testTechArea.getDescription()).isEqualTo(TestUtil.UPDATED_TECH_AREA_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateTechAreaWithPatch() throws Exception {
        // Initialize the database
        techAreaRepository.saveAndFlush(techArea);

        int databaseSizeBeforeUpdate = techAreaRepository.findAll().size();

        // Update the techArea using partial update
        TechArea partialUpdatedTechArea = new TechArea();
        partialUpdatedTechArea.setId(techArea.getId());
        partialUpdatedTechArea.description(TestUtil.UPDATED_TECH_AREA_DESCRIPTION);
        restTechAreaMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedTechArea.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTechArea))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeUpdate);
        TechArea testTechArea = techAreaList.get(techAreaList.size() - 1);
        assertThat(testTechArea.getDescription()).isEqualTo(TestUtil.UPDATED_TECH_AREA_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingTechArea() throws Exception {
        // Save the entities
        techAreaRepository.saveAndFlush(techArea);

        int databaseSizeBeforeUpdate = techAreaRepository.findAll().size();
        entityManager.detach(techArea);
        techArea.setId(count.incrementAndGet());

        // Create the TechArea
        TechAreaDTO techAreaDTO = techAreaMapper.toDto(techArea);
        try{
            // If the entity doesn't have an ID, it will throw BadRequestAlertException
            restTechAreaMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, techAreaDTO.getId())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(techAreaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTechArea() throws Exception {
        // Save the entities
        techAreaRepository.saveAndFlush(techArea);

        int databaseSizeBeforeUpdate = techAreaRepository.findAll().size();
        entityManager.detach(techArea);
        techArea.setId(count.incrementAndGet());

        // Create the TechArea
        TechAreaDTO techAreaDTO = techAreaMapper.toDto(techArea);
        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restTechAreaMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(techAreaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTechArea() throws Exception {
        // Save the entities
        techAreaRepository.saveAndFlush(techArea);

        int databaseSizeBeforeUpdate = techAreaRepository.findAll().size();
        entityManager.detach(techArea);
        techArea.setId(count.incrementAndGet());

        // Create the TechArea
        TechAreaDTO techAreaDTO = techAreaMapper.toDto(techArea);

        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restTechAreaMockMvc
                    .perform(
                            patch(ENTITY_API_URL)
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(techAreaDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the TechArea in the database
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTechArea() throws Exception {
        // Save the entities
        techAreaRepository.saveAndFlush(techArea);

        int databaseSizeBeforeDelete = techAreaRepository.findAll().size();

        // Delete the techArea
        restTechAreaMockMvc
                .perform(delete(ENTITY_API_URL_ID, techArea.getId())
                        .with(jwt().authorities(TestUtil.adminAuthority))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TechArea> techAreaList = techAreaRepository.findAll();
        assertThat(techAreaList).hasSize(databaseSizeBeforeDelete - 1);
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
    }
}