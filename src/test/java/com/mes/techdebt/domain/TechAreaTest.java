package com.mes.techdebt.domain;

import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TechAreaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        TechArea techArea1 = new TechArea();
        techArea1.setId(1L);

        TechArea techArea2 = new TechArea();
        techArea2.setId(techArea1.getId());
        assertThat(techArea1).isEqualTo(techArea2);

        techArea2.setId(2L);
        assertThat(techArea1).isNotEqualTo(techArea2);

        techArea1.setId(null);
        assertThat(techArea1).isNotEqualTo(techArea2);
    }
}