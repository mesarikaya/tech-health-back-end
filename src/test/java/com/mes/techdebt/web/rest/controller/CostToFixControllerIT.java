package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.IntegrationTest;
import com.cargill.techdebt.domain.*;
import com.cargill.techdebt.repository.*;
import com.mes.techdebt.domain.*;
import com.mes.techdebt.repository.*;
import com.mes.techdebt.service.dto.CostToFixDTO;
import com.mes.techdebt.service.mapper.CostToFixMapper;
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
 * Integration tests for the {@link SiteController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
@Slf4j
class CostToFixControllerIT {

    private static final String ENTITY_API_URL = "/api/v1/cost-to-fixes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restCostToFixMockMvc;

    @Autowired
    private InvestmentCriticalityRepository investmentCriticalityRepository;
    @Autowired
    private CostToFixMapper costToFixMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CostRangeRepository costRangeRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private CostToFixRepository costToFixRepository;
    @Autowired
    private TechAreaRepository techAreaRepository;
    @Autowired
    private TechDomainRepository techDomainRepository;

    private TechArea techArea;
    private TechDomain techDomain;
    private Site site;
    private Site newSite;
    private InvestmentCriticality investmentCriticality;
    private InvestmentCriticality newInvestmentCriticality;
    private Category category;
    private Category newCategory;
    private CostRange costRange;
    private CostRange newCostRange;
    private CostToFix costToFix;
    private CostToFix newCostToFix;

    @BeforeEach
    @Transactional
    void setUp() {
        investmentCriticality = createInvestmentCriticalityEntity(DEFAULT_INVESTMENT_CRITICALITY);
        techDomain = createTechDomainEntity(DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        techArea = createTechAreaEntity(techDomain, DEFAULT_TECH_AREA_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        category = createCategoryEntity(techArea, DEFAULT_CATEGORY_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        costRange = createCostRangeEntity(DEFAULT_COST_RANGE_DESCRIPTION);
        site = createSiteEntity(investmentCriticality, DEFAULT_SITE_NAME, DEFAULT_MDM_SITE_ID);
        costToFix = createCostToFixEntity(category, costRange, site);
        saveDependentEntities();
    }

    private void saveDependentEntities() {
        investmentCriticalityRepository.saveAndFlush(investmentCriticality);
        techDomainRepository.saveAndFlush(techDomain);
        techAreaRepository.saveAndFlush(techArea);
        categoryRepository.saveAndFlush(category);
        costRangeRepository.saveAndFlush(costRange);
        siteRepository.saveAndFlush(site);
    }

    @Test
    @Transactional
    void createCostToFix() throws Exception {
        int databaseSizeBeforeCreate = costToFixRepository.findAll().size();

        // Create the CostToFix
        CostToFixDTO costToFixDTO = costToFixMapper.toDto(costToFix);
        restCostToFixMockMvc
                .perform(
                        post(ENTITY_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(costToFixDTO))
                                .with(jwt().authorities(adminAuthority))
                )
                .andExpect(status().isCreated());

        // Validate the CostToFix in the database
        List<CostToFix> costToFixList = costToFixRepository.findAll();
        assertThat(costToFixList).hasSize(databaseSizeBeforeCreate + 1);
        CostToFix testCostToFix = costToFixList.get(costToFixList.size() - 1);
        assertThat(testCostToFix.getCostRange().getDescription()).isEqualTo(DEFAULT_COST_RANGE_DESCRIPTION);
        assertThat(testCostToFix.getSite().getName()).isEqualTo(DEFAULT_SITE_NAME);
        assertThat(testCostToFix.getCategory().getDescription()).isEqualTo(DEFAULT_CATEGORY_DESCRIPTION);
    }

    @Test
    @Transactional
    void createCostToFixWithExistingId() throws Exception {

        costToFixRepository.saveAndFlush(costToFix);
        int databaseSizeBeforeCreate = costToFixRepository.findAll().size();

        // Create the CostToFix with an existing ID
        entityManager.detach(costToFix);
        costToFix.setId(1L);
        CostToFixDTO costToFixDTO = costToFixMapper.toDto(costToFix);

        // An entity with an existing ID cannot be created, so this API call must fail
        try {
            restCostToFixMockMvc
                    .perform(
                            post(ENTITY_API_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(costToFixDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the CostToFix in the database
        List<CostToFix> costToFixList = costToFixRepository.findAll();
        assertThat(costToFixList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putCostToFixWithExistingCategoryAndSiteAndCostRange() throws Exception {
        // Try to update an entity to another already existing name
        costToFixRepository.saveAndFlush(costToFix);

        //Create updated entity
        newInvestmentCriticality = createInvestmentCriticalityEntity(UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);

        newSite = createSiteEntity(newInvestmentCriticality, UPDATED_SITE_NAME, UPDATED_MDM_SITE_ID);
        siteRepository.saveAndFlush(newSite);

        newCategory = createCategoryEntity(techArea, UPDATED_CATEGORY_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        categoryRepository.saveAndFlush(newCategory);

        newCostRange = createCostRangeEntity(UPDATED_COST_RANGE_DESCRIPTION);
        costRangeRepository.saveAndFlush(newCostRange);

        newCostToFix = createCostToFixEntity(newCategory, costRange, newSite);
        costToFixRepository.saveAndFlush(newCostToFix);

        int databaseSizeBeforeCreate = costToFixRepository.findAll().size();

        // Update the costToFix
        try {
            CostToFix updatedCostToFix = costToFixRepository
                    .findById(newCostToFix.getId()).get();
            // Disconnect from session so that the updates on updatedCostToFix are not directly saved in db
            entityManager.detach(updatedCostToFix);
            updatedCostToFix.setCategory(category);
            updatedCostToFix.setSite(site);
            updatedCostToFix.setCostRange(costRange);

            CostToFixDTO updatedCostTOFixDTO = costToFixMapper.toDto(updatedCostToFix);
            // An entity with an existing category cannot be saved, so this API call must fail
            restCostToFixMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, updatedCostTOFixDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedCostTOFixDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the Site in the database
        List<CostToFix> costToFixList = costToFixRepository.findAll();
        assertThat(costToFixList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void patchCostToFixWithExistingCategoryAndSiteAndCostRange() throws Exception {
        // Try to update an entity to another already existing name
        costToFixRepository.saveAndFlush(costToFix);

        //Create updated entity
        newInvestmentCriticality = createInvestmentCriticalityEntity(UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);

        newSite = createSiteEntity(newInvestmentCriticality, UPDATED_SITE_NAME, UPDATED_MDM_SITE_ID);
        siteRepository.saveAndFlush(newSite);

        newCategory = createCategoryEntity(techArea, UPDATED_CATEGORY_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        categoryRepository.saveAndFlush(newCategory);

        newCostRange = createCostRangeEntity(UPDATED_COST_RANGE_DESCRIPTION);
        costRangeRepository.saveAndFlush(newCostRange);

        newCostToFix = createCostToFixEntity(newCategory, costRange, newSite);
        costToFixRepository.saveAndFlush(newCostToFix);

        int databaseSizeBeforeCreate = costToFixRepository.findAll().size();

        // Update the costToFix
        try {
            CostToFix updatedCostToFix = costToFixRepository
                    .findById(newCostToFix.getId()).get();
            // Disconnect from session so that the updates on updatedCostToFix are not directly saved in db
            entityManager.detach(updatedCostToFix);
            updatedCostToFix.setCategory(category);
            updatedCostToFix.setSite(site);
            updatedCostToFix.setCostRange(costRange);

            CostToFixDTO updatedCostTOFixDTO = costToFixMapper.toDto(updatedCostToFix);

            // An entity with an existing category cannot be saved, so this API call must fail
            restCostToFixMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, updatedCostTOFixDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(updatedCostTOFixDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("Entity not unique");
        }

        // Validate the CostToFix in the database
        List<CostToFix> costToFixList = costToFixRepository.findAll();
        assertThat(costToFixList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCostToFix() throws Exception {
        // Initialize the database
        costToFixRepository.saveAndFlush(costToFix);

        int databaseSizeBeforeCreate = costToFixRepository.findAll().size();

        // Get all the costToFixList
        restCostToFixMockMvc
                .perform(
                        get(ENTITY_API_URL + "?sort=id")
                                .with(jwt().authorities(readAuthority))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(costToFix.getId().intValue())))
                .andExpect(jsonPath("$.[*].site.id").value(hasItem(site.getId().intValue())))
                .andExpect(jsonPath("$.[*].category.id").value(hasItem(category.getId().intValue())))
                .andExpect(jsonPath("$.[*].costRange.id").value(hasItem(costRange.getId().intValue())));
    }

    @Test
    @Transactional
    void getNonExistingCostToFix() throws Exception {
        // Get the costToFix
        restCostToFixMockMvc.perform(
                get(ENTITY_API_URL_ID, Long.MAX_VALUE)
                        .with(jwt().authorities(readAuthority))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCostToFixSite() throws Exception {
        // Initialize the database
        costToFixRepository.saveAndFlush(costToFix);

        int databaseSizeBeforeCreate = costToFixRepository.findAll().size();

        // Update the costToFix using partial update
        CostToFix partialUpdatedCostToFix = new CostToFix();
        partialUpdatedCostToFix.setId(costToFix.getId());

        newInvestmentCriticality = createInvestmentCriticalityEntity(UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);

        newSite = createSiteEntity(newInvestmentCriticality, UPDATED_SITE_NAME, UPDATED_MDM_SITE_ID);
        siteRepository.saveAndFlush(newSite);
        partialUpdatedCostToFix
                .site(newSite)
                .category(category)
                .costRange(costRange);

        restCostToFixMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedCostToFix.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCostToFix))
                                .with(jwt().authorities(adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the CostToFix in the database
        Long id = costToFix.getId();
        CostToFix costToFix = costToFixRepository.findById(id).get();
        assertThat(costToFix.getSite().getName()).isEqualTo(UPDATED_SITE_NAME);
        assertThat(costToFix.getCostRange().getDescription()).isEqualTo(DEFAULT_COST_RANGE_DESCRIPTION);
        assertThat(costToFix.getCategory().getDescription()).isEqualTo(DEFAULT_CATEGORY_DESCRIPTION);
    }

    @Test
    @Transactional
    void createCostToFixWithExistingSite() throws Exception {
        // Try to update an entity to another already existing name
        costToFixRepository.saveAndFlush(costToFix);

        int databaseSizeBeforeCreate = costToFixRepository.findAll().size();
        CostToFix newCostToFix = costToFixRepository.findById(costToFix.getId()).get();

        // Update the costToFix
        try {
            // Disconnect from session so that the updates on updatedSite are not directly saved in db
            newInvestmentCriticality = createInvestmentCriticalityEntity(UPDATED_INVESTMENT_CRITICALITY);
            newSite = createSiteEntity(newInvestmentCriticality, DEFAULT_SITE_NAME, DEFAULT_MDM_SITE_ID);
            entityManager.detach(newSite);
            newSite.setName(site.getName());
            entityManager.detach(newCostToFix);
            newCostToFix.site(newSite);
            CostToFixDTO newCostToFixDTO = costToFixMapper.toDto(newCostToFix);

            // An entity with an existing description cannot be saved, so this API call must fail
            restCostToFixMockMvc
                    .perform(
                            post(ENTITY_API_URL, newCostToFixDTO)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(newCostToFixDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("A new costToFix cannot already have an existing id");
        }

        // Validate the CostToFix in the database
        List<CostToFix> costToFixList = costToFixRepository.findAll();
        assertThat(costToFixList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putNonExistingCostToFix() throws Exception {

        costToFixRepository.saveAndFlush(costToFix);

        int databaseSizeBeforeUpdate = costToFixRepository.findAll().size();
        entityManager.detach(costToFix);
        costToFix.setId(count.incrementAndGet());

        // Create the CostToFix
        CostToFixDTO costToFixDTO = costToFixMapper.toDto(costToFix);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        try {
            restCostToFixMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, costToFixDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(costToFixDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the CostToFix in the database
        List<CostToFix> costToFixList = costToFixRepository.findAll();
        assertThat(costToFixList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCostToFix() throws Exception {

        costToFixRepository.saveAndFlush(costToFix);

        int databaseSizeBeforeUpdate = costToFixRepository.findAll().size();
        entityManager.detach(costToFix);
        costToFix.setId(count.incrementAndGet());

        // Create the CostToFix
        CostToFixDTO costToFixDTO = costToFixMapper.toDto(costToFix);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restCostToFixMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(costToFixDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the CostToFix in the database
        List<CostToFix> costToFixList = costToFixRepository.findAll();
        assertThat(costToFixList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCostToFix() throws Exception {

        costToFixRepository.saveAndFlush(costToFix);

        int databaseSizeBeforeUpdate = costToFixRepository.findAll().size();
        entityManager.detach(costToFix);
        costToFix.setId(count.incrementAndGet());

        // Create the CostToFix
        CostToFixDTO costToFixDTO = costToFixMapper.toDto(costToFix);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restCostToFixMockMvc
                    .perform(
                            put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(costToFixDTO))
                                    .with(jwt().authorities(writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());

            // Validate the CostToFix in the database
            List<CostToFix> costToFixList = costToFixRepository.findAll();
            assertThat(costToFixList).hasSize(databaseSizeBeforeUpdate);
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }
    }

    @Test
    @Transactional
    void partialUpdateCostToFixWithPatch() throws Exception {
        // Initialize the database
        costToFixRepository.saveAndFlush(costToFix);

        int databaseSizeBeforeUpdate = costToFixRepository.findAll().size();

        // Update the costToFix using partial update
        entityManager.detach(costToFix);
        CostToFix partialUpdatedCostToFix = new CostToFix();
        partialUpdatedCostToFix.setId(costToFix.getId());

        //Create updated entities
        newInvestmentCriticality = createInvestmentCriticalityEntity(UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);

        newSite = createSiteEntity(newInvestmentCriticality, UPDATED_SITE_NAME, UPDATED_MDM_SITE_ID);
        siteRepository.saveAndFlush(newSite);

        newCategory = createCategoryEntity(techArea, UPDATED_CATEGORY_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        categoryRepository.saveAndFlush(newCategory);

        newCostRange = createCostRangeEntity(UPDATED_COST_RANGE_DESCRIPTION);
        costRangeRepository.saveAndFlush(newCostRange);

        partialUpdatedCostToFix
                .site(newSite)
                .category(category)
                .costRange(costRange);

        restCostToFixMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedCostToFix.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCostToFix))
                                .with(jwt().authorities(adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the CostToFix in the database
        Long id = costToFix.getId();
        CostToFix costToFix = costToFixRepository.findById(id).get();
        assertThat(costToFix.getSite().getName()).isEqualTo(UPDATED_SITE_NAME);
        assertThat(costToFix.getCostRange().getDescription()).isEqualTo(DEFAULT_COST_RANGE_DESCRIPTION);
        assertThat(costToFix.getCategory().getDescription()).isEqualTo(DEFAULT_CATEGORY_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateCostToFixWithPatch() throws Exception {
        // Initialize the database
        costToFixRepository.saveAndFlush(costToFix);

        int databaseSizeBeforeUpdate = costToFixRepository.findAll().size();

        //Create updated entities
        newInvestmentCriticality = createInvestmentCriticalityEntity(UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);

        newSite = createSiteEntity(newInvestmentCriticality, UPDATED_SITE_NAME, UPDATED_MDM_SITE_ID);
        siteRepository.saveAndFlush(newSite);

        newCategory = createCategoryEntity(techArea, UPDATED_CATEGORY_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        categoryRepository.saveAndFlush(newCategory);

        newCostRange = createCostRangeEntity(UPDATED_COST_RANGE_DESCRIPTION);
        costRangeRepository.saveAndFlush(newCostRange);

        entityManager.detach(costToFix);
        CostToFix updatedCostToFix = new CostToFix();
        updatedCostToFix.setId(costToFix.getId());
        InvestmentCriticality updatedInvestmentCriticality = new InvestmentCriticality();
        updatedInvestmentCriticality.setId(newInvestmentCriticality.getId());
        updatedInvestmentCriticality.setDescription(UPDATED_INVESTMENT_CRITICALITY);
        Site updatedSite = new Site();
        updatedSite.setId(newSite.getId());
        updatedSite.setName(UPDATED_SITE_NAME);
        updatedSite.investmentCriticality(updatedInvestmentCriticality);
        Category updatedCategory = new Category();
        updatedCategory.setId(newCategory.getId());
        updatedCategory.setDescription(UPDATED_CATEGORY_DESCRIPTION);
        CostRange updatedCostRange = new CostRange();
        updatedCostRange.setId(newCostRange.getId());
        updatedCostRange.setDescription(UPDATED_COST_RANGE_DESCRIPTION);
        updatedCostToFix
                .site(updatedSite)
                .category(updatedCategory)
                .costRange(updatedCostRange);
        log.debug("CostToFix: {}", costToFixRepository.findAll());
        CostToFixDTO updatedCostToFixDTO = costToFixMapper.toDto(updatedCostToFix);
        log.debug("Updated cost to fix: {}", updatedCostToFixDTO);

        restCostToFixMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, updatedCostToFixDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(updatedCostToFixDTO))
                                .with(jwt().authorities(adminAuthority))
                )
                .andExpect(status().isOk());

        // Validate the CostToFix in the database
        Long id = costToFix.getId();
        CostToFix costToFix = costToFixRepository.findById(id).get();
        assertThat(costToFix.getSite().getName()).isEqualTo(UPDATED_SITE_NAME);
        assertThat(costToFix.getCostRange().getDescription()).isEqualTo(UPDATED_COST_RANGE_DESCRIPTION);
        assertThat(costToFix.getCategory().getDescription()).isEqualTo(UPDATED_CATEGORY_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingCostToFix() throws Exception {

        costToFixRepository.saveAndFlush(costToFix);

        int databaseSizeBeforeUpdate = costToFixRepository.findAll().size();
        entityManager.detach(costToFix);
        costToFix.setId(count.incrementAndGet());

        // Create the CostToFix
        CostToFixDTO costToFixDTO = costToFixMapper.toDto(costToFix);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        try {
            restCostToFixMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, costToFixDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(costToFixDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the CostToFix in the database
        List<CostToFix> costToFixList = costToFixRepository.findAll();
        assertThat(costToFixList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCostToFix() throws Exception {

        costToFixRepository.saveAndFlush(costToFix);

        int databaseSizeBeforeUpdate = costToFixRepository.findAll().size();
        entityManager.detach(costToFix);
        costToFix.setId(count.incrementAndGet());

        // Create the CostToFix
        CostToFixDTO costToFixDTO = costToFixMapper.toDto(costToFix);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restCostToFixMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(costToFixDTO))
                                    .with(jwt().authorities(adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the CostToFix in the database
        List<CostToFix> costToFixList = costToFixRepository.findAll();
        assertThat(costToFixList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCostToFix() throws Exception {

        costToFixRepository.saveAndFlush(costToFix);

        int databaseSizeBeforeUpdate = costToFixRepository.findAll().size();
        entityManager.detach(costToFix);
        costToFix.setId(count.incrementAndGet());

        // Create the CostToFix
        CostToFixDTO costToFixDTO = costToFixMapper.toDto(costToFix);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restCostToFixMockMvc
                    .perform(
                            put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(costToFixDTO))
                                    .with(jwt().authorities(writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());

            // Validate the CostToFix in the database
            List<CostToFix> costToFixList = costToFixRepository.findAll();
            assertThat(costToFixList).hasSize(databaseSizeBeforeUpdate);
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }
    }

    @Test
    @Transactional
    void deleteCostToFix() throws Exception {
        // Initialize the database
        costToFixRepository.saveAndFlush(costToFix);

        int databaseSizeBeforeDelete = costToFixRepository.findAll().size();

        // Delete the site
        restCostToFixMockMvc
                .perform(
                        delete(ENTITY_API_URL_ID, costToFix.getId())
                                .with(jwt().authorities(adminAuthority))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CostToFix> costToFixList = costToFixRepository.findAll();
        assertThat(costToFixList).hasSize(databaseSizeBeforeDelete-1);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();

        costToFixRepository.deleteAll();
        List<CostToFix> costToFixList = costToFixRepository.findAll();
        assertThat(costToFixList).hasSize(0);

        siteRepository.deleteAll();
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(0);

        investmentCriticalityRepository.deleteAll();
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(0);

        costRangeRepository.deleteAll();
        List<CostRange> costRangeRepositoryList = costRangeRepository.findAll();
        assertThat(costRangeRepositoryList).hasSize(0);

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