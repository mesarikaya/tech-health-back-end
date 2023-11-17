package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.IntegrationTest;
import com.cargill.techdebt.domain.*;
import com.cargill.techdebt.repository.*;
import com.mes.techdebt.domain.*;
import com.mes.techdebt.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mes.techdebt.web.rest.controller.utils.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AttachmentController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
@Slf4j
class AttachmentControllerIT {

    private static final String ENTITY_API_URL = "/api/v1/attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private InvestmentCriticalityRepository investmentCriticalityRepository;
    @Autowired
    private AssessmentCriteriaRepository assessmentCriteriaRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TechAreaRepository techAreaRepository;
    @Autowired
    private TechDomainRepository techDomainRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private MockMvc restAttachmentMockMvc;

    private Site site;
    private InvestmentCriticality investmentCriticality;
    private AssessmentCriteria assessmentCriteria;
    private TechArea techArea;
    private TechDomain techDomain;
    private Category category;
    MockMultipartFile file1;
    MockMultipartFile file2;

    @BeforeEach
    @Transactional
    void setUp() {
        investmentCriticality = createInvestmentCriticalityEntity(DEFAULT_INVESTMENT_CRITICALITY);
        techDomain = createTechDomainEntity(DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        techArea = createTechAreaEntity(techDomain, DEFAULT_TECH_AREA_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        category = createCategoryEntity(techArea, DEFAULT_CATEGORY_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        assessmentCriteria = createAssessmentCriteriaEntity(category, DEFAULT_CRITERIA_DESCRIPTION, DEFAULT_ACTIVE_FLAG);
        site = createSiteEntity(investmentCriticality, DEFAULT_SITE_NAME, DEFAULT_MDM_SITE_ID);
        saveDependentEntities();
    }

    private void saveDependentEntities() {
        investmentCriticalityRepository.saveAndFlush(investmentCriticality);
        techDomainRepository.saveAndFlush(techDomain);
        techAreaRepository.saveAndFlush(techArea);
        categoryRepository.saveAndFlush(category);
        assessmentCriteriaRepository.saveAndFlush(assessmentCriteria);
        siteRepository.saveAndFlush(site);
    }

    private void createAttachmentFiles(String fileOrFiles){
        file1 = new MockMultipartFile(fileOrFiles, DEFAULT_ATTACHMENT_FILE_NAME,
                DEFAULT_ATTACHMENT_FILE_TYPE, DEFAULT_ATTACHMENT_FILE_CONTENT.getBytes()
        );

        file2 = new MockMultipartFile(fileOrFiles, UPDATED_ATTACHMENT_FILE_NAME,
                UPDATED_ATTACHMENT_FILE_TYPE, UPDATED_ATTACHMENT_FILE_CONTENT.getBytes()
        );
    }

    private void saveSingleFile(Long siteId, Long assessmentCriteriaId) throws Exception {
        createAttachmentFiles("file");
        restAttachmentMockMvc.perform(multipart(ENTITY_API_URL)
                .file(file1)
                .param("siteId", String.valueOf(siteId))
                .param("assessmentCriteriaId", String.valueOf(assessmentCriteriaId))
                .param("createdBy", DEFAULT_ATTACHMENT_CREATOR)
                .with(jwt().authorities(adminAuthority))
        ).andExpect(status().isOk());
    }

    private void saveMultipleFile(Long siteId, Long assessmentCriteriaId) throws Exception {
        createAttachmentFiles("files");
        restAttachmentMockMvc.perform(multipart(ENTITY_API_URL+"/multiple")
                .file(file1)
                .file(file2)
                .param("siteId", String.valueOf(siteId))
                .param("assessmentCriteriaId", String.valueOf(assessmentCriteriaId))
                .param("createdBy", DEFAULT_ATTACHMENT_CREATOR)
                .with(jwt().authorities(adminAuthority))
        ).andExpect(status().isOk());
    }

    @Test
    @Transactional
    void uploadSingleAttachment() throws Exception {
        int databaseSizeBeforeCreate = attachmentRepository.findAll().size();
        Long siteId = site.getId();
        Long assessmentCriteriaId = assessmentCriteria.getId();
        saveSingleFile(siteId, assessmentCriteriaId);

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeCreate + 1);
        Attachment testAttachment = attachmentList.get(attachmentList.size() - 1);
        assertThat(testAttachment.getFileName()).isEqualTo(DEFAULT_ATTACHMENT_FILE_NAME);
        assertThat(testAttachment.getSite().getId()).isEqualTo(siteId);
        assertThat(testAttachment.getAssessmentCriteria().getId()).isEqualTo(assessmentCriteriaId);
        assertThat(testAttachment.getCreatedBy()).isEqualTo(DEFAULT_ATTACHMENT_CREATOR);
        assertThat(testAttachment.getFileType()).isEqualTo(DEFAULT_ATTACHMENT_FILE_TYPE);
        assertThat(testAttachment.getData()).isEqualTo(DEFAULT_ATTACHMENT_FILE_CONTENT.getBytes());
    }

    @Test
    @Transactional
    void uploadMultipleAttachment() throws Exception {
        int databaseSizeBeforeCreate = attachmentRepository.findAll().size();
        Long siteId = site.getId();
        Long assessmentCriteriaId = assessmentCriteria.getId();
        saveMultipleFile(siteId, assessmentCriteriaId);

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeCreate + 2);
        Attachment testAttachment = attachmentList.get(attachmentList.size() - 2);
        assertThat(testAttachment.getFileName()).isEqualTo(DEFAULT_ATTACHMENT_FILE_NAME);
        assertThat(testAttachment.getSite().getId()).isEqualTo(siteId);
        assertThat(testAttachment.getAssessmentCriteria().getId()).isEqualTo(assessmentCriteriaId);
        assertThat(testAttachment.getCreatedBy()).isEqualTo(DEFAULT_ATTACHMENT_CREATOR);
        assertThat(testAttachment.getFileType()).isEqualTo(DEFAULT_ATTACHMENT_FILE_TYPE);
        assertThat(testAttachment.getData()).isEqualTo(DEFAULT_ATTACHMENT_FILE_CONTENT.getBytes());

        testAttachment = attachmentList.get(attachmentList.size() - 1);
        assertThat(testAttachment.getFileName()).isEqualTo(UPDATED_ATTACHMENT_FILE_NAME);
        assertThat(testAttachment.getSite().getId()).isEqualTo(siteId);
        assertThat(testAttachment.getAssessmentCriteria().getId()).isEqualTo(assessmentCriteriaId);
        assertThat(testAttachment.getCreatedBy()).isEqualTo(DEFAULT_ATTACHMENT_CREATOR);
        assertThat(testAttachment.getFileType()).isEqualTo(UPDATED_ATTACHMENT_FILE_TYPE);
        assertThat(testAttachment.getData()).isEqualTo(UPDATED_ATTACHMENT_FILE_CONTENT.getBytes());
    }

    @Test
    @Transactional
    void getAllAttachments() throws Exception {
        // Initialize the database
        Long siteId = site.getId();
        Long assessmentCriteriaId = assessmentCriteria.getId();
        saveMultipleFile(siteId, assessmentCriteriaId);

        // Get all the categoryList
        restAttachmentMockMvc
                .perform(get(ENTITY_API_URL)
                                .with(jwt()
                                        .authorities(adminAuthority))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].site.id").value(hasItem(site.getId().intValue())))
                .andExpect(jsonPath("$.[*].assessmentCriteria.id").value(hasItem(assessmentCriteria.getId().intValue())));
    }

    @Test
    @Transactional
    void getAttachmentById() throws Exception {
        // Initialize the database
        Long siteId = site.getId();
        Long assessmentCriteriaId = assessmentCriteria.getId();
        saveMultipleFile(siteId, assessmentCriteriaId);

        // Get available attachments in the database
        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(2);

        // Get all the categoryList
        restAttachmentMockMvc
                .perform(get(ENTITY_API_URL_ID, attachmentList.get(0).getId())
                        .with(jwt()
                                .authorities(adminAuthority))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString(DEFAULT_ATTACHMENT_FILE_CONTENT)));
    }

    @Test
    @Transactional
    void deleteAttachment() throws Exception {
        // Initialize the database
        Long siteId = site.getId();
        Long assessmentCriteriaId = assessmentCriteria.getId();
        saveMultipleFile(siteId, assessmentCriteriaId);

        int databaseSizeBeforeDelete = attachmentRepository.findAll().size();
        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(2);

        // Delete the site
        restAttachmentMockMvc
                .perform(
                        delete(ENTITY_API_URL_ID, attachmentList.get(0).getId())
                                .with(jwt().authorities(adminAuthority))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeDelete-1);
    }


    @AfterEach
    void tearDown() {
        entityManager.clear();

        attachmentRepository.deleteAll();
        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(0);

        siteRepository.deleteAll();
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(0);

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