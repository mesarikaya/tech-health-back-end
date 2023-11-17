package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.IntegrationTest;
import com.mes.techdebt.domain.InvestmentCriticality;
import com.mes.techdebt.domain.Site;
import com.mes.techdebt.repository.InvestmentCriticalityRepository;
import com.mes.techdebt.repository.SiteRepository;
import com.mes.techdebt.service.dto.SiteDTO;
import com.mes.techdebt.service.mapper.SiteMapper;
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
 * Integration tests for the {@link SiteController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
@Slf4j
class SiteControllerIT {

    private static final String ENTITY_API_URL = "/api/v1/sites";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SiteMapper siteMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restSiteMockMvc;

    @Autowired
    private InvestmentCriticalityRepository investmentCriticalityRepository;

    private Site site;
    private Site newSite;
    private InvestmentCriticality investmentCriticality;
    private InvestmentCriticality newInvestmentCriticality;


    @BeforeEach
    @Transactional
    void setUp() {
        investmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.DEFAULT_INVESTMENT_CRITICALITY);
        site = TestUtil.createSiteEntity(investmentCriticality, TestUtil.DEFAULT_SITE_NAME, TestUtil.DEFAULT_MDM_SITE_ID);
        saveDependentEntities();
    }

    private void saveDependentEntities() {
        investmentCriticalityRepository.saveAndFlush(investmentCriticality);
    }

    @Test
    @Transactional
    void createSite() throws Exception {
        int databaseSizeBeforeCreate = siteRepository.findAll().size();
        // Create the Site
        site.setInvestmentCriticality(investmentCriticality);
        SiteDTO siteDTO = siteMapper.toDto(site);
        restSiteMockMvc
                .perform(
                        post(ENTITY_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(siteDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isCreated());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate + 1);
        Site testSite = siteList.get(siteList.size() - 1);
        assertThat(testSite.getName()).isEqualTo(TestUtil.DEFAULT_SITE_NAME);
        assertThat(testSite.getMdmSiteId()).isEqualTo(TestUtil.DEFAULT_MDM_SITE_ID);
    }

    @Test
    @Transactional
    void createSiteWithExistingId() throws Exception {
        siteRepository.saveAndFlush(site);

        // Create the Site with an existing ID
        entityManager.detach(site);
        site.setId(1L);
        SiteDTO siteDTO = siteMapper.toDto(site);

        int databaseSizeBeforeCreate = siteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        try {
            restSiteMockMvc
                    .perform(
                            post(ENTITY_API_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(siteDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putSiteWithExistingName() throws Exception {
        // Try to update an entity to another already existing name
        siteRepository.saveAndFlush(site);

        newInvestmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);
        newSite = TestUtil.createSiteEntity(investmentCriticality, TestUtil.UPDATED_SITE_NAME, TestUtil.UPDATED_MDM_SITE_ID);
        siteRepository.saveAndFlush(newSite);

        int databaseSizeBeforeCreate = siteRepository.findAll().size();

        // Update the site
        try {
            Site updatedSite = siteRepository
                    .findById(newSite.getId()).get();
            // Disconnect from session so that the updates on updatedSite are not directly saved in db
            entityManager.detach(updatedSite);
            updatedSite.setName(site.getName());

            SiteDTO updatedSiteDTO = siteMapper.toDto(updatedSite);

            // An entity with an existing description cannot be saved, so this API call must fail
            restSiteMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedSiteDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedSiteDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void patchSiteWithExistingName() throws Exception {
        // Try to update an entity to another already existing name
        siteRepository.saveAndFlush(site);

        newInvestmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);
        newSite = TestUtil.createSiteEntity(investmentCriticality, TestUtil.UPDATED_SITE_NAME, TestUtil.UPDATED_MDM_SITE_ID);
        siteRepository.saveAndFlush(newSite);

        int databaseSizeBeforeCreate = siteRepository.findAll().size();

        // Update the site
        try {
            Site updatedSite = siteRepository
                    .findById(newSite.getId()).get();
            // Disconnect from session so that the updates on updatedSite are not directly saved in db
            entityManager.detach(updatedSite);
            updatedSite.setName(site.getName());

            SiteDTO updatedSiteDTO = siteMapper.toDto(updatedSite);
            // An entity with an existing description cannot be saved, so this API call must fail
            restSiteMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedSiteDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedSiteDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList
        restSiteMockMvc
                .perform(
                        get(ENTITY_API_URL + "?sort=id,desc")
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(site.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(Matchers.hasItem(TestUtil.DEFAULT_SITE_NAME)))
                .andExpect(jsonPath("$.[*].mdmSiteId").value(hasItem(site.getMdmSiteId().intValue())));
    }

    @Test
    @Transactional
    void getNonExistingSite() throws Exception {
        // Get the site
        restSiteMockMvc.perform(
                get(ENTITY_API_URL_ID, Long.MAX_VALUE)
                        .with(jwt().authorities(TestUtil.adminAuthority))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSiteName() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        int databaseSizeBeforeUpdate = siteRepository.findAll().size();

        // Update the site
        Site updatedSite = siteRepository.findById(site.getId()).get();
        // Disconnect from session so that the updates on updatedSite are not directly saved in db
        entityManager.detach(updatedSite);
        updatedSite.name(TestUtil.UPDATED_SITE_NAME).mdmSiteId(TestUtil.UPDATED_MDM_SITE_ID);
        SiteDTO siteDTO = siteMapper.toDto(updatedSite);
        restSiteMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, siteDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(siteDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
        Site testSite = siteList.get(siteList.size() - 1);
        assertThat(testSite.getName()).isEqualTo(TestUtil.UPDATED_SITE_NAME);
        assertThat(testSite.getMdmSiteId()).isEqualTo(TestUtil.UPDATED_MDM_SITE_ID);
    }

    @Test
    @Transactional
    void createSiteWithExistingName() throws Exception {
        // Try to update an entity to another already existing name
        siteRepository.saveAndFlush(site);

        int databaseSizeBeforeCreate = siteRepository.findAll().size();

        // Update the site
        try {
            newInvestmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
            investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);
            newSite = TestUtil.createSiteEntity(investmentCriticality, TestUtil.DEFAULT_SITE_NAME, TestUtil.DEFAULT_MDM_SITE_ID);
            SiteDTO newSiteDTO = siteMapper.toDto(newSite);
            // An entity with an existing description cannot be saved, so this API call must fail
            restSiteMockMvc
                    .perform(
                            post(ENTITY_API_URL, newSiteDTO)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(newSiteDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putNonExistingSite() throws Exception {
        siteRepository.saveAndFlush(site);
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();
        entityManager.detach(site);
        site.setId(count.incrementAndGet());

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        try {
            restSiteMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, siteDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(siteDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSite() throws Exception {
        siteRepository.saveAndFlush(site);
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();
        entityManager.detach(site);
        site.setId(count.incrementAndGet());

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restSiteMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(siteDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSite() throws Exception {
        siteRepository.saveAndFlush(site);
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();
        entityManager.detach(site);
        site.setId(count.incrementAndGet());

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restSiteMockMvc
                    .perform(
                            patch(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(siteDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());

            // Validate the Site in the database
            List<Site> siteList = siteRepository.findAll();
            assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }
    }

    @Test
    @Transactional
    void partialUpdateSiteWithPatch() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        int databaseSizeBeforeUpdate = siteRepository.findAll().size();

        // Update the site using partial update
        Site partialUpdatedSite = new Site();
        partialUpdatedSite.setId(site.getId());
        partialUpdatedSite.name(TestUtil.UPDATED_SITE_NAME);
        restSiteMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedSite.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSite))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
        Site testSite = siteList.get(siteList.size() - 1);
        assertThat(testSite.getName()).isEqualTo(TestUtil.UPDATED_SITE_NAME);
        assertThat(testSite.getMdmSiteId()).isEqualTo(TestUtil.DEFAULT_MDM_SITE_ID);
    }

    @Test
    @Transactional
    void fullUpdateSiteWithPatch() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        int databaseSizeBeforeUpdate = siteRepository.findAll().size();

        // Update the site using partial update
        Site partialUpdatedSite = new Site();
        partialUpdatedSite.setId(site.getId());
        partialUpdatedSite.name(TestUtil.UPDATED_SITE_NAME).mdmSiteId(TestUtil.UPDATED_MDM_SITE_ID);
        restSiteMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedSite.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSite))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
        Site testSite = siteList.get(siteList.size() - 1);
        assertThat(testSite.getName()).isEqualTo(TestUtil.UPDATED_SITE_NAME);
        assertThat(testSite.getMdmSiteId()).isEqualTo(TestUtil.UPDATED_MDM_SITE_ID);
    }

    @Test
    @Transactional
    void patchNonExistingSite() throws Exception {
        siteRepository.saveAndFlush(site);
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();
        entityManager.detach(site);
        site.setId(count.incrementAndGet());

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        try{
            // If the entity doesn't have an ID, it will throw BadRequestAlertException
            restSiteMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, siteDTO.getId())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(siteDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSite() throws Exception {
        siteRepository.saveAndFlush(site);
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();
        entityManager.detach(site);
        site.setId(count.incrementAndGet());

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restSiteMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(siteDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSite() throws Exception {
        siteRepository.saveAndFlush(site);
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();
        entityManager.detach(site);
        site.setId(count.incrementAndGet());

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        try{
            // If url ID doesn't match entity ID, it will throw BadRequestAlertException
            restSiteMockMvc
                    .perform(
                            patch(ENTITY_API_URL)
                                    .contentType("application/merge-patch+json")
                                    .content(TestUtil.convertObjectToJsonBytes(siteDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        int databaseSizeBeforeDelete = siteRepository.findAll().size();

        // Delete the site
        restSiteMockMvc
                .perform(
                        delete(ENTITY_API_URL_ID, site.getId())
                                .with(jwt().authorities(TestUtil.adminAuthority))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        entityManager.clear();

        siteRepository.deleteAll();
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(0);

        investmentCriticalityRepository.deleteAll();
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(0);

    }
}