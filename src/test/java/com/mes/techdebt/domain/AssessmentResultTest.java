package com.mes.techdebt.domain;

import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentResultTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        AssessmentResult assessmentResult1 = new AssessmentResult();
        assessmentResult1.setId(1L);

        AssessmentResult assessmentResult2 = new AssessmentResult();
        assessmentResult2.setId(assessmentResult1.getId());
        assertThat(assessmentResult1).isEqualTo(assessmentResult2);

        assessmentResult2.setId(2L);
        assertThat(assessmentResult1).isNotEqualTo(assessmentResult2);

        assessmentResult1.setId(null);
        assertThat(assessmentResult1).isNotEqualTo(assessmentResult2);
    }
}