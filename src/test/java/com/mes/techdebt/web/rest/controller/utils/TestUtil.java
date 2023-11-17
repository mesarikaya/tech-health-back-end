package com.mes.techdebt.web.rest.controller.utils;

import com.cargill.techdebt.domain.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mes.techdebt.domain.*;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Utility class for testing REST controllers.
 */
public final class TestUtil {

    private static final ObjectMapper mapper = createObjectMapper();
    public static final String DEFAULT_INVESTMENT_CRITICALITY = "TestInvestmentCriticality";
    public static final String UPDATED_INVESTMENT_CRITICALITY = "NewInvestmentCriticality";
    public static final String DEFAULT_COST_RANGE_DESCRIPTION = "TestCostRange";
    public static final String UPDATED_COST_RANGE_DESCRIPTION = "NewCostRange";
    public static final String DEFAULT_CATEGORY_DESCRIPTION = "TestCategory";
    public static final String UPDATED_CATEGORY_DESCRIPTION = "NewCategory";
    public static final String DEFAULT_CRITERIA_DESCRIPTION = "TestCriteria";
    public static final String UPDATED_CRITERIA_DESCRIPTION = "UpdatedCriteria";
    public static final Boolean DEFAULT_ACTIVE_FLAG = Boolean.TRUE;
    public static final Boolean UPDATED_ACTIVE_FLAG = Boolean.FALSE;
    public static final String DEFAULT_DOMAIN_DESCRIPTION = "TestTechDomain";
    public static final String UPDATED_DOMAIN_DESCRIPTION = "NewTechDomain";
    public static final String DEFAULT_TECH_AREA_DESCRIPTION = "TestTechArea";
    public static final String UPDATED_TECH_AREA_DESCRIPTION = "NewTestTechArea";
    public static final String DEFAULT_SITE_NAME = "TestSite";
    public static final String UPDATED_SITE_NAME = "NewTestSite";
    public static final Long DEFAULT_MDM_SITE_ID = 1L;
    public static final Long UPDATED_MDM_SITE_ID = 2L;
    public static final String DEFAULT_RECOMMENDATION_STATUS = "TestRecommendationStatus";
    public static final String UPDATED_RECOMMENDATION_STATUS = "NewRecommendationStatus";
    public static final String DEFAULT_ASSESSMENT_RESULT_RECOMMENDATION_TEXT = "TestAssessmentResultRecommendationText";
    public static final String UPDATED_ASSESSMENT_RESULT_RECOMMENDATION_TEXT = "NewAssessmentResultRecommendationText";
    public static final SimpleGrantedAuthority readAuthority = new SimpleGrantedAuthority("APPROLE_TechHealth_User_Read");
    public static final SimpleGrantedAuthority writeAuthority = new SimpleGrantedAuthority("APPROLE_TechHealth_User_Write");
    public static final SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("APPROLE_TechHealth_User_Admin");
    public static final String DEFAULT_ATTACHMENT_FILE_NAME = "TestAttachment.txt";
    public static final String DEFAULT_ATTACHMENT_FILE_TYPE = String.valueOf(MediaType.TEXT_PLAIN);
    public static final String DEFAULT_ATTACHMENT_FILE_CONTENT = "Hello, Here Testing attachments!";
    public static final String DEFAULT_ATTACHMENT_CREATOR = "TestUser";
    public static final String UPDATED_ATTACHMENT_FILE_NAME = "NewAttachment.txt";
    public static final String UPDATED_ATTACHMENT_FILE_TYPE = String.valueOf(MediaType.IMAGE_JPEG);
    public static final String UPDATED_ATTACHMENT_FILE_CONTENT = "Hello, Here Testing attachments!";


    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    /**
     * Convert an object to JSON byte array.
     *
     * @param object the object to convert.
     * @return the JSON byte array.
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }

    /**
     * Create a byte array with a specific size filled with specified data.
     *
     * @param size the size of the byte array.
     * @param data the data to put in the byte array.
     * @return the JSON byte array.
     */
    public static byte[] createByteArray(int size, String data) {
        byte[] byteArray = new byte[size];
        for (int i = 0; i < size; i++) {
            byteArray[i] = Byte.parseByte(data, 2);
        }
        return byteArray;
    }

    /**
     * A matcher that tests that the examined string represents the same instant as the reference datetime.
     */
    public static class OffsetDateTimeMatcher extends TypeSafeDiagnosingMatcher<String> {

        private final OffsetDateTime date;

        public OffsetDateTimeMatcher(OffsetDateTime date) {
            this.date = date;
        }

        @Override
        protected boolean matchesSafely(String item, Description mismatchDescription) {
            try {
                if (!date.isEqual(OffsetDateTime.parse(item))) {
                    mismatchDescription.appendText("was ").appendValue(item);
                    return false;
                }
                return true;
            } catch (DateTimeParseException e) {
                mismatchDescription.appendText("was ").appendValue(item).appendText(", which could not be parsed as a ZonedDateTime");
                return false;
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a String representing the same Instant as ").appendValue(date);
        }
    }

    /**
     * Creates a matcher that matches when the examined string represents the same instant as the reference datetime.
     *
     * @param date the reference datetime against which the examined string is checked.
     */
    public static OffsetDateTimeMatcher sameInstant(OffsetDateTime date) {
        return new OffsetDateTimeMatcher(date);
    }

    /**
     * A matcher that tests that the examined number represents the same value - it can be Long, Double, etc - as the reference BigDecimal.
     */
    public static class NumberMatcher extends TypeSafeMatcher<Number> {

        final BigDecimal value;

        public NumberMatcher(BigDecimal value) {
            this.value = value;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a numeric value is ").appendValue(value);
        }

        @Override
        protected boolean matchesSafely(Number item) {
            BigDecimal bigDecimal = asDecimal(item);
            return bigDecimal != null && value.compareTo(bigDecimal) == 0;
        }

        private static BigDecimal asDecimal(Number item) {
            if (item == null) {
                return null;
            }
            if (item instanceof BigDecimal) {
                return (BigDecimal) item;
            } else if (item instanceof Long) {
                return BigDecimal.valueOf((Long) item);
            } else if (item instanceof Integer) {
                return BigDecimal.valueOf((Integer) item);
            } else if (item instanceof Double) {
                return BigDecimal.valueOf((Double) item);
            } else if (item instanceof Float) {
                return BigDecimal.valueOf((Float) item);
            } else {
                return BigDecimal.valueOf(item.doubleValue());
            }
        }
    }

    /**
     * Creates a matcher that matches when the examined number represents the same value as the reference BigDecimal.
     *
     * @param number the reference BigDecimal against which the examined number is checked.
     */
    public static NumberMatcher sameNumber(BigDecimal number) {
        return new NumberMatcher(number);
    }

    /**
     * Verifies the equals/hashcode contract on the domain object.
     */
    public static <T> void equalsVerifier(Class<T> clazz) throws Exception {
        T domainObject1 = clazz.getConstructor().newInstance();
        assertThat(domainObject1.toString()).isNotNull();
        assertThat(domainObject1).isEqualTo(domainObject1);
        assertThat(domainObject1).hasSameHashCodeAs(domainObject1);
        // Test with an instance of another class
        Object testOtherObject = new Object();
        assertThat(domainObject1).isNotEqualTo(testOtherObject);
        assertThat(domainObject1).isNotEqualTo(null);
        // Test with an instance of the same class
        T domainObject2 = clazz.getConstructor().newInstance();
        assertThat(domainObject1).isNotEqualTo(domainObject2);
        // HashCodes are equals because the objects are not persisted yet
        assertThat(domainObject1).hasSameHashCodeAs(domainObject2);
    }

    /**
     * Create a {@link FormattingConversionService} which use ISO date format, instead of the localized one.
     * @return the {@link FormattingConversionService}.
     */
    public static FormattingConversionService createFormattingConversionService() {
        DefaultFormattingConversionService dfcs = new DefaultFormattingConversionService();
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(dfcs);
        return dfcs;
    }

    /**
     * Makes a an executes a query to the EntityManager finding all stored objects.
     * @param <T> The type of objects to be searched
     * @param em The instance of the EntityManager
     * @param clss The class type to be searched
     * @return A list of all found objects
     */
    public static <T> List<T> findAll(EntityManager em, Class<T> clss) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clss);
        Root<T> rootEntry = cq.from(clss);
        CriteriaQuery<T> all = cq.select(rootEntry);
        TypedQuery<T> allQuery = em.createQuery(all);
        return allQuery.getResultList();
    }

    /**
     * Below methods creates an entity for specific domains for the relevant tests.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CostRange createCostRangeEntity(String description) {
        return new CostRange()
                .description(description);
    }

    public static InvestmentCriticality createInvestmentCriticalityEntity(String description) {
        return new InvestmentCriticality().description(description);
    }

    public static TechArea createTechAreaEntity(TechDomain techDomain, String description, Boolean activeFlag) {
        return new TechArea()
                .description(description)
                .domain(techDomain)
                .isActive(activeFlag);
    }

    public static TechDomain createTechDomainEntity(String description, Boolean activeFlag) {
        return new TechDomain().description(description).isActive(activeFlag);
    }

    public static Site createSiteEntity(InvestmentCriticality investmentCriticality, String siteName, Long mdmSiteId ) {
        return new Site()
                .name(siteName)
                .mdmSiteId(mdmSiteId)
                .investmentCriticality(investmentCriticality);
    }

    public static CostToFix createCostToFixEntity(Category category, CostRange costRange, Site site) {
        return new CostToFix()
                .site(site)
                .category(category)
                .costRange(costRange);
    }

    public static Category createCategoryEntity(TechArea techArea, String description, Boolean activeFlag) {
        return new Category()
                .description(description)
                .techArea(techArea)
                .isActive(activeFlag);
    }

    public static AssessmentCriteria createAssessmentCriteriaEntity(Category category, String description, Boolean activeFlag) {
        return new AssessmentCriteria()
                .description(description)
                .isActive(activeFlag)
                .category(category);
    }

    public static RecommendationStatus createRecommendationStatusEntity(String description) {
        return new RecommendationStatus()
                .description(description);
    }

    public static AssessmentResult createAssessmentResultEntity(RecommendationStatus recommendationStatus, AssessmentCriteria assessmentCriteria, Site site,
                                                                String recommendationText) {
        return new AssessmentResult()
                .recommendationStatus(recommendationStatus)
                .assessmentCriteria(assessmentCriteria)
                .recommendationText(recommendationText)
                .site(site);
    }

    private TestUtil() {}
}
