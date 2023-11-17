package com.mes.techdebt.domain;

import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SiteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        Site site1 = new Site();
        site1.setId(1L);

        Site site2 = new Site();
        site2.setId(site1.getId());
        assertThat(site1).isEqualTo(site2);

        site2.setId(2L);
        assertThat(site1).isNotEqualTo(site2);

        site1.setId(null);
        assertThat(site1).isNotEqualTo(site2);
    }
}