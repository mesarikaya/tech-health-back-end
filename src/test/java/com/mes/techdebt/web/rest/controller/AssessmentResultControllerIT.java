package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.IntegrationTest;
import com.cargill.techdebt.domain.*;
import com.cargill.techdebt.repository.*;
import com.mes.techdebt.domain.*;
import com.mes.techdebt.repository.*;
import com.mes.techdebt.service.dto.AssessmentResultDTO;
import com.mes.techdebt.service.mapper.AssessmentResultMapper;
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
class AssessmentResultControllerIT {

    private static final String ENTITY_API_URL = "/api/v1/assessment-results";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restAssessmentResultMockMvc;

    @Autowired
    private InvestmentCriticalityRepository investmentCriticalityRepository;

    @Autowired
    private AssessmentResultMapper assessmentResultMapper;

    @Autowired
    private RecommendationStatusRepository recommendationStatusRepository;

    @Autowired
    private AssessmentCriteriaRepository assessmentCriteriaRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private AssessmentResultRepository assessmentResultRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TechAreaRepository techAreaRepository;
    @Autowired
    private TechDomainRepository techDomainRepository;


    private Site site;
    private Site newSite;
    private InvestmentCriticality investmentCriticality;
    private InvestmentCriticality newInvestmentCriticality;
    private RecommendationStatus recommendationStatus;
    private RecommendationStatus newRecommendationStatus;
    private AssessmentCriteria assessmentCriteria;
    private AssessmentCriteria newAssessmentCriteria;
    private AssessmentResult assessmentResult;
    private AssessmentResult newAssessmentResult;
    private TechArea techArea;
    private TechDomain techDomain;
    private Category category;

    @BeforeEach
    @Transactional
    void setUp() {
        investmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.DEFAULT_INVESTMENT_CRITICALITY);
        techDomain = TestUtil.createTechDomainEntity(TestUtil.DEFAULT_DOMAIN_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        techArea = TestUtil.createTechAreaEntity(techDomain, TestUtil.DEFAULT_TECH_AREA_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        category = TestUtil.createCategoryEntity(techArea, TestUtil.DEFAULT_CATEGORY_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        assessmentCriteria = TestUtil.createAssessmentCriteriaEntity(category, TestUtil.DEFAULT_CRITERIA_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        site = TestUtil.createSiteEntity(investmentCriticality, TestUtil.DEFAULT_SITE_NAME, TestUtil.DEFAULT_MDM_SITE_ID);
        recommendationStatus = TestUtil.createRecommendationStatusEntity(TestUtil.DEFAULT_RECOMMENDATION_STATUS);
        assessmentResult = TestUtil.createAssessmentResultEntity(recommendationStatus, assessmentCriteria, site, TestUtil.DEFAULT_ASSESSMENT_RESULT_RECOMMENDATION_TEXT);
        saveDependentEntities();
    }

    private void saveDependentEntities() {
        investmentCriticalityRepository.saveAndFlush(investmentCriticality);
        techDomainRepository.saveAndFlush(techDomain);
        techAreaRepository.saveAndFlush(techArea);
        categoryRepository.saveAndFlush(category);
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);
        recommendationStatusRepository.saveAndFlush(recommendationStatus);
        siteRepository.saveAndFlush(site);
    }

    @Test
    @Transactional
    void createAssessmentResult() throws Exception {
        int databaseSizeBeforeCreate = assessmentResultRepository.findAll().size();

        // Create the AssessmentResult
        AssessmentResultDTO assessmentResultDTO = assessmentResultMapper.toDto(assessmentResult);
        restAssessmentResultMockMvc
                .perform(
                        post(ENTITY_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(assessmentResultDTO))
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isCreated());

        // Validate the AssessmentResult in the database
        List<AssessmentResult> assessmentResultList = assessmentResultRepository.findAll();
        assertThat(assessmentResultList).hasSize(databaseSizeBeforeCreate + 1);
        AssessmentResult testAssessmentResult = assessmentResultList.get(assessmentResultList.size() - 1);
        assertThat(testAssessmentResult.getAssessmentCriteria().getDescription()).isEqualTo(TestUtil.DEFAULT_CRITERIA_DESCRIPTION);
        assertThat(testAssessmentResult.getSite().getName()).isEqualTo(TestUtil.DEFAULT_SITE_NAME);
        assertThat(testAssessmentResult.getRecommendationStatus().getDescription()).isEqualTo(TestUtil.DEFAULT_RECOMMENDATION_STATUS);
    }

    @Test
    @Transactional
    void createAssessmentResultWithExistingId() throws Exception {
        assessmentResultRepository.saveAndFlush(assessmentResult);

        // Create the Site with an existing ID
        entityManager.detach(assessmentResult);
        assessmentResult.setId(1L);
        AssessmentResultDTO assessmentResultDTO = assessmentResultMapper.toDto(assessmentResult);

        int databaseSizeBeforeCreate = assessmentResultRepository.findAll().size();
        // An entity with an existing ID cannot be created, so this API call must fail
        try {
            restAssessmentResultMockMvc
                    .perform(
                            post(ENTITY_API_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentResultDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }

        // Validate the AssessmentResult in the database
        List<AssessmentResult> assessmentResultList = assessmentResultRepository.findAll();
        assertThat(assessmentResultList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putAssessmentResultWithExistingRecommendationStatusAndSiteAndAssessmentCriteria() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        // Try to update an entity to another already existing name
        newInvestmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);
        newSite = TestUtil.createSiteEntity(investmentCriticality, TestUtil.UPDATED_SITE_NAME, TestUtil.UPDATED_MDM_SITE_ID);
        siteRepository.saveAndFlush(newSite);
        newRecommendationStatus = TestUtil.createRecommendationStatusEntity(TestUtil.UPDATED_RECOMMENDATION_STATUS);
        recommendationStatusRepository.saveAndFlush(newRecommendationStatus);
        newAssessmentCriteria = TestUtil.createAssessmentCriteriaEntity(category, TestUtil.DEFAULT_CATEGORY_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        assessmentCriteriaRepository.saveAndFlush(newAssessmentCriteria);
        newAssessmentResult = TestUtil.createAssessmentResultEntity(newRecommendationStatus, newAssessmentCriteria, newSite, TestUtil.UPDATED_ASSESSMENT_RESULT_RECOMMENDATION_TEXT);
        assessmentResultRepository.saveAndFlush(newAssessmentResult);

        int databaseSizeBeforeCreate = assessmentResultRepository.findAll().size();

        // Update the assessmentResult
        AssessmentResult updatedAssessmentResult = assessmentResultRepository
                .findById(newAssessmentResult.getId()).get();
        // Disconnect from session so that the updates on updatedAssessmentResult are not directly saved in db
        entityManager.detach(updatedAssessmentResult);
        updatedAssessmentResult.setRecommendationStatus(recommendationStatus);
        updatedAssessmentResult.setSite(site);
        updatedAssessmentResult.setAssessmentCriteria(assessmentCriteria);
        AssessmentResultDTO updatedCostTOFixDTO = assessmentResultMapper.toDto(updatedAssessmentResult);

        // An entity with an existing recommendationStatus cannot be saved, so this API call must fail
        restAssessmentResultMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, updatedCostTOFixDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(updatedCostTOFixDTO))
                                .with(jwt().authorities(TestUtil.writeAuthority))
                )
                .andExpect(status().isOk());

        // Validate the Site in the database
        List<AssessmentResult> assessmentResultList = assessmentResultRepository.findAll();
        assertThat(assessmentResultList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void patchAssessmentResultWithExistingRecommendationStatusAndSiteAndAssessmentCriteria() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        // Try to update an entity to another already existing name
        newInvestmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);
        newSite = TestUtil.createSiteEntity(investmentCriticality, TestUtil.UPDATED_SITE_NAME, TestUtil.UPDATED_MDM_SITE_ID);
        siteRepository.saveAndFlush(newSite);
        newRecommendationStatus = TestUtil.createRecommendationStatusEntity(TestUtil.UPDATED_RECOMMENDATION_STATUS);
        recommendationStatusRepository.saveAndFlush(newRecommendationStatus);
        newAssessmentCriteria = TestUtil.createAssessmentCriteriaEntity(category, TestUtil.DEFAULT_CATEGORY_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        assessmentCriteriaRepository.saveAndFlush(newAssessmentCriteria);
        newAssessmentResult = TestUtil.createAssessmentResultEntity(newRecommendationStatus, newAssessmentCriteria, newSite, TestUtil.UPDATED_ASSESSMENT_RESULT_RECOMMENDATION_TEXT);
        assessmentResultRepository.saveAndFlush(newAssessmentResult);

        int databaseSizeBeforeCreate = assessmentResultRepository.findAll().size();

        // Update the assessmentResult
        AssessmentResult updatedAssessmentResult = assessmentResultRepository
                .findById(newAssessmentResult.getId()).get();
        // Disconnect from session so that the updates on updatedAssessmentResult are not directly saved in db
        entityManager.detach(updatedAssessmentResult);
        updatedAssessmentResult.setRecommendationStatus(recommendationStatus);
        updatedAssessmentResult.setSite(site);
        updatedAssessmentResult.setAssessmentCriteria(assessmentCriteria);
        AssessmentResultDTO updatedCostTOFixDTO = assessmentResultMapper.toDto(updatedAssessmentResult);

        // An entity with an existing recommendationStatus cannot be saved, so this API call must fail
        restAssessmentResultMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, updatedCostTOFixDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(updatedCostTOFixDTO))
                                .with(jwt().authorities(TestUtil.writeAuthority))
                )
                .andExpect(status().isOk());

        // Validate the AssessmentResult in the database
        Long id = assessmentResult.getId();
        AssessmentResult assessmentResult = assessmentResultRepository.findById(id).get();
        assertThat(assessmentResult.getSite().getName()).isEqualTo(TestUtil.DEFAULT_SITE_NAME);
    }

    @Test
    @Transactional
    void getAllAssessmentResult() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        // Get all the assessmentResultList
        restAssessmentResultMockMvc
                .perform(
                        get(ENTITY_API_URL + "?sort=id")
                        .with(jwt().authorities(TestUtil.readAuthority))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(assessmentResult.getId().intValue())))
                .andExpect(jsonPath("$.[*].site.id").value(hasItem(site.getId().intValue())))
                .andExpect(jsonPath("$.[*].recommendationStatus.id").value(hasItem(recommendationStatus.getId().intValue())))
                .andExpect(jsonPath("$.[*].assessmentCriteria.id").value(hasItem(assessmentCriteria.getId().intValue())));
    }

    @Test
    @Transactional
    void getNonExistingAssessmentResult() throws Exception {
        // Get the assessmentResult
        restAssessmentResultMockMvc
                .perform(
                        get(ENTITY_API_URL_ID, Long.MAX_VALUE)
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAssessmentResultSite() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        int databaseSizeBeforeUpdate = assessmentResultRepository.findAll().size();

        // Update the assessmentResult using partial update
        entityManager.detach(assessmentResult);
        AssessmentResult partialUpdatedAssessmentResult = new AssessmentResult();
        partialUpdatedAssessmentResult.setId(assessmentResult.getId());
        newInvestmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);
        newSite = TestUtil.createSiteEntity(investmentCriticality, TestUtil.UPDATED_SITE_NAME, TestUtil.UPDATED_MDM_SITE_ID);
        siteRepository.saveAndFlush(newSite);
        partialUpdatedAssessmentResult
                .site(newSite)
                .recommendationStatus(recommendationStatus)
                .assessmentCriteria(assessmentCriteria);

        restAssessmentResultMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedAssessmentResult.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAssessmentResult))
                                .with(jwt().authorities(TestUtil.writeAuthority))
                )
                .andExpect(status().isOk());

        // Validate the AssessmentResult in the database
        Long id = assessmentResult.getId();
        AssessmentResult assessmentResult = assessmentResultRepository.findById(id).get();
        assertThat(assessmentResult.getSite().getName()).isEqualTo(TestUtil.UPDATED_SITE_NAME);
        assertThat(assessmentResult.getAssessmentCriteria().getDescription()).isEqualTo(TestUtil.DEFAULT_CRITERIA_DESCRIPTION);
        assertThat(assessmentResult.getRecommendationStatus().getDescription()).isEqualTo(TestUtil.DEFAULT_RECOMMENDATION_STATUS);
    }

    @Test
    @Transactional
    void createAssessmentResultWithExistingSite() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        // Try to update an entity to another already existing name
        int databaseSizeBeforeCreate = assessmentResultRepository.findAll().size();
        AssessmentResult newAssessmentResult = assessmentResultRepository.findById(assessmentResult.getId()).get();

        // Update the assessmentResult
        try {
            // Disconnect from session so that the updates on updatedSite are not directly saved in db
            newInvestmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
            investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);
            newSite = TestUtil.createSiteEntity(investmentCriticality, TestUtil.UPDATED_SITE_NAME, TestUtil.UPDATED_MDM_SITE_ID);
            siteRepository.saveAndFlush(newSite);
            entityManager.detach(newSite);
            newSite.setName(site.getName());
            entityManager.detach(newAssessmentResult);
            newAssessmentResult.site(newSite);
            AssessmentResultDTO newAssessmentResultDTO = assessmentResultMapper.toDto(newAssessmentResult);
            log.debug("AssessmentResult: {}", assessmentResultRepository.findAll());
            // An entity with an existing description cannot be saved, so this API call must fail
            restAssessmentResultMockMvc
                    .perform(
                            post(ENTITY_API_URL, newAssessmentResultDTO)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(newAssessmentResultDTO))
                                    .with(jwt().authorities(TestUtil.adminAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class,() -> {throw e.getCause();});
            assertThat(exception.getMessage()).containsPattern("A new assessmentResult cannot already have an existing id");
        }

        // Validate the AssessmentResult in the database
        List<AssessmentResult> assessmentResultList = assessmentResultRepository.findAll();
        assertThat(assessmentResultList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void putNonExistingAssessmentResult() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        int databaseSizeBeforeUpdate = assessmentResultRepository.findAll().size();
        entityManager.detach(assessmentResult);
        assessmentResult.setId(count.incrementAndGet());

        // Create the AssessmentResult
        AssessmentResultDTO assessmentResultDTO = assessmentResultMapper.toDto(assessmentResult);
        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        try {
            restAssessmentResultMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, assessmentResultDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentResultDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the AssessmentResult in the database
        List<AssessmentResult> assessmentResultList = assessmentResultRepository.findAll();
        assertThat(assessmentResultList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssessmentResult() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        int databaseSizeBeforeUpdate = assessmentResultRepository.findAll().size();
        entityManager.detach(assessmentResult);
        assessmentResult.setId(count.incrementAndGet());

        // Create the AssessmentResult
        AssessmentResultDTO assessmentResultDTO = assessmentResultMapper.toDto(assessmentResult);
        log.debug("AssessmentResult: {}", assessmentResultRepository.findAll());
        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restAssessmentResultMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentResultDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the AssessmentResult in the database
        List<AssessmentResult> assessmentResultList = assessmentResultRepository.findAll();
        assertThat(assessmentResultList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssessmentResult() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        int databaseSizeBeforeUpdate = assessmentResultRepository.findAll().size();
        entityManager.detach(assessmentResult);
        assessmentResult.setId(count.incrementAndGet());

        // Create the AssessmentResult
        AssessmentResultDTO assessmentResultDTO = assessmentResultMapper.toDto(assessmentResult);
        log.debug("AssessmentResult: {}", assessmentResultRepository.findAll());
        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restAssessmentResultMockMvc
                    .perform(
                            put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentResultDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());

            // Validate the AssessmentResult in the database
            List<AssessmentResult> assessmentResultList = assessmentResultRepository.findAll();
            assertThat(assessmentResultList).hasSize(databaseSizeBeforeUpdate);
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }
    }

    @Test
    @Transactional
    void partialUpdateAssessmentResultWithPatch() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        int databaseSizeBeforeUpdate = assessmentResultRepository.findAll().size();

        // Update the assessmentResult using partial update
        entityManager.detach(assessmentResult);
        AssessmentResult partialUpdatedAssessmentResult = new AssessmentResult();
        partialUpdatedAssessmentResult.setId(assessmentResult.getId());
        newInvestmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);
        newSite = TestUtil.createSiteEntity(investmentCriticality, TestUtil.UPDATED_SITE_NAME, TestUtil.UPDATED_MDM_SITE_ID);
        siteRepository.saveAndFlush(newSite);
        newRecommendationStatus = TestUtil.createRecommendationStatusEntity(TestUtil.UPDATED_RECOMMENDATION_STATUS);
        recommendationStatusRepository.saveAndFlush(newRecommendationStatus);
        newAssessmentCriteria = TestUtil.createAssessmentCriteriaEntity(category, TestUtil.UPDATED_CATEGORY_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        assessmentCriteriaRepository.saveAndFlush(newAssessmentCriteria);
        partialUpdatedAssessmentResult
                .site(newSite)
                .recommendationStatus(recommendationStatus)
                .assessmentCriteria(assessmentCriteria)
                .recommendationText(TestUtil.UPDATED_ASSESSMENT_RESULT_RECOMMENDATION_TEXT);

        restAssessmentResultMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedAssessmentResult.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAssessmentResult))
                                .with(jwt().authorities(TestUtil.writeAuthority))
                )
                .andExpect(status().isOk());

        // Validate the AssessmentResult in the database
        Long id = assessmentResult.getId();
        AssessmentResult assessmentResult = assessmentResultRepository.findById(id).get();
        assertThat(assessmentResult.getSite().getName()).isEqualTo(TestUtil.UPDATED_SITE_NAME);
        assertThat(assessmentResult.getAssessmentCriteria().getDescription()).isEqualTo(TestUtil.DEFAULT_CRITERIA_DESCRIPTION);
        assertThat(assessmentResult.getRecommendationStatus().getDescription()).isEqualTo(TestUtil.DEFAULT_RECOMMENDATION_STATUS);
        assertThat(assessmentResult.getRecommendationText()).isEqualTo(TestUtil.UPDATED_ASSESSMENT_RESULT_RECOMMENDATION_TEXT);
    }

    @Test
    @Transactional
    void fullUpdateAssessmentResultWithPatch() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        int databaseSizeBeforeUpdate = assessmentResultRepository.findAll().size();

        // Update the assessmentResult using partial update
        newInvestmentCriticality = TestUtil.createInvestmentCriticalityEntity(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
        investmentCriticalityRepository.saveAndFlush(newInvestmentCriticality);
        newSite = TestUtil.createSiteEntity(investmentCriticality, TestUtil.UPDATED_SITE_NAME, TestUtil.UPDATED_MDM_SITE_ID);
        siteRepository.saveAndFlush(newSite);
        newRecommendationStatus = TestUtil.createRecommendationStatusEntity(TestUtil.UPDATED_RECOMMENDATION_STATUS);
        recommendationStatusRepository.saveAndFlush(newRecommendationStatus);
        newAssessmentCriteria = TestUtil.createAssessmentCriteriaEntity(category, TestUtil.UPDATED_CRITERIA_DESCRIPTION, TestUtil.DEFAULT_ACTIVE_FLAG);
        assessmentCriteriaRepository.saveAndFlush(newAssessmentCriteria);

        entityManager.detach(assessmentResult);
        AssessmentResult updatedAssessmentResult = new AssessmentResult();
        updatedAssessmentResult.setId(assessmentResult.getId());
        InvestmentCriticality updatedInvestmentCriticality = new InvestmentCriticality();
        updatedInvestmentCriticality.setId(newInvestmentCriticality.getId());
        updatedInvestmentCriticality.setDescription(TestUtil.UPDATED_INVESTMENT_CRITICALITY);
        Site updatedSite = new Site();
        updatedSite.setId(newSite.getId());
        updatedSite.setName(TestUtil.UPDATED_SITE_NAME);
        updatedSite.investmentCriticality(updatedInvestmentCriticality);
        RecommendationStatus updatedRecommendationStatus = new RecommendationStatus();
        updatedRecommendationStatus.setId(newRecommendationStatus.getId());
        updatedRecommendationStatus.setDescription(TestUtil.UPDATED_RECOMMENDATION_STATUS);
        AssessmentCriteria updatedAssessmentCriteria = new AssessmentCriteria();
        updatedAssessmentCriteria.setId(newAssessmentCriteria.getId());
        updatedAssessmentCriteria.setDescription(TestUtil.UPDATED_CRITERIA_DESCRIPTION);
        updatedAssessmentResult
                .site(updatedSite)
                .recommendationStatus(updatedRecommendationStatus)
                .assessmentCriteria(updatedAssessmentCriteria)
                .recommendationText(TestUtil.UPDATED_ASSESSMENT_RESULT_RECOMMENDATION_TEXT);
        log.debug("AssessmentResult: {}", assessmentResultRepository.findAll());
        AssessmentResultDTO updatedAssessmentResultDTO = assessmentResultMapper.toDto(updatedAssessmentResult);
        log.debug("Updated cost to fix: {}", updatedAssessmentResultDTO);

        restAssessmentResultMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, updatedAssessmentResultDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(updatedAssessmentResultDTO))
                                .with(jwt().authorities(TestUtil.writeAuthority))
                )
                .andExpect(status().isOk());

        // Validate the AssessmentResult in the database
        Long id = assessmentResult.getId();
        AssessmentResult assessmentResult = assessmentResultRepository.findById(id).get();
        assertThat(assessmentResult.getSite().getName()).isEqualTo(TestUtil.UPDATED_SITE_NAME);
        assertThat(assessmentResult.getAssessmentCriteria().getDescription()).isEqualTo(TestUtil.UPDATED_CRITERIA_DESCRIPTION);
        assertThat(assessmentResult.getRecommendationStatus().getDescription()).isEqualTo(TestUtil.UPDATED_RECOMMENDATION_STATUS);
        assertThat(assessmentResult.getRecommendationText()).isEqualTo(TestUtil.UPDATED_ASSESSMENT_RESULT_RECOMMENDATION_TEXT);
    }

    @Test
    @Transactional
    void patchNonExistingAssessmentResult() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        int databaseSizeBeforeUpdate = assessmentResultRepository.findAll().size();
        entityManager.detach(assessmentResult);
        assessmentResult.setId(count.incrementAndGet());

        // Create the AssessmentResult
        AssessmentResultDTO assessmentResultDTO = assessmentResultMapper.toDto(assessmentResult);
        log.debug("AssessmentResult: {}", assessmentResultRepository.findAll());
        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        try {
            restAssessmentResultMockMvc
                    .perform(
                            patch(ENTITY_API_URL_ID, assessmentResultDTO.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentResultDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the AssessmentResult in the database
        List<AssessmentResult> assessmentResultList = assessmentResultRepository.findAll();
        assertThat(assessmentResultList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssessmentResult() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        int databaseSizeBeforeUpdate = assessmentResultRepository.findAll().size();
        entityManager.detach(assessmentResult);
        assessmentResult.setId(count.incrementAndGet());

        // Create the AssessmentResult
        AssessmentResultDTO assessmentResultDTO = assessmentResultMapper.toDto(assessmentResult);
        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restAssessmentResultMockMvc
                    .perform(
                            put(ENTITY_API_URL_ID, count.incrementAndGet())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentResultDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isBadRequest());
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("Entity not found");
        }

        // Validate the AssessmentResult in the database
        List<AssessmentResult> assessmentResultList = assessmentResultRepository.findAll();
        assertThat(assessmentResultList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssessmentResult() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        int databaseSizeBeforeUpdate = assessmentResultRepository.findAll().size();
        entityManager.detach(assessmentResult);
        assessmentResult.setId(count.incrementAndGet());

        // Create the AssessmentResult
        AssessmentResultDTO assessmentResultDTO = assessmentResultMapper.toDto(assessmentResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        try {
            restAssessmentResultMockMvc
                    .perform(
                            put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtil.convertObjectToJsonBytes(assessmentResultDTO))
                                    .with(jwt().authorities(TestUtil.writeAuthority))
                    )
                    .andExpect(status().isMethodNotAllowed());

            // Validate the AssessmentResult in the database
            List<AssessmentResult> assessmentResultList = assessmentResultRepository.findAll();
            assertThat(assessmentResultList).hasSize(databaseSizeBeforeUpdate);
        } catch (NestedServletException e) {
            Exception exception = assertThrows(BadRequestAlertException.class, () -> {
                throw e.getCause();
            });
            assertThat(exception.getMessage()).containsPattern("cannot already have an existing id");
        }
    }

    @Test
    @Transactional
    void deleteAssessmentResult() throws Exception {
        // Initialize the database
        assessmentResultRepository.saveAndFlush(assessmentResult);

        int databaseSizeBeforeDelete = assessmentResultRepository.findAll().size();

        // Delete the site
        restAssessmentResultMockMvc
                .perform(
                        delete(ENTITY_API_URL_ID, assessmentResult.getId())
                                .with(jwt().authorities(TestUtil.adminAuthority))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AssessmentResult> assessmentResultList = assessmentResultRepository.findAll();
        assertThat(assessmentResultList).hasSize(databaseSizeBeforeDelete-1);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();

        assessmentResultRepository.deleteAll();
        List<AssessmentResult> assessmentResultList = assessmentResultRepository.findAll();
        assertThat(assessmentResultList).hasSize(0);

        siteRepository.deleteAll();
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(0);

        recommendationStatusRepository.deleteAll();
        List<RecommendationStatus> recommendationStatusList = recommendationStatusRepository.findAll();
        assertThat(assessmentResultList).hasSize(0);

        investmentCriticalityRepository.deleteAll();
        List<InvestmentCriticality> investmentCriticalityList = investmentCriticalityRepository.findAll();
        assertThat(investmentCriticalityList).hasSize(0);

        assessmentCriteriaRepository.deleteAll();
        List<AssessmentCriteria> assessmentCriteriaRepositoryList = assessmentCriteriaRepository.findAll();
        assertThat(assessmentCriteriaRepositoryList).hasSize(0);

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