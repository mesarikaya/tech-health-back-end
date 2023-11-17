package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.Site;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SiteDTOTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        SiteDTO site1 = new SiteDTO();
        site1.setId(1L);

        SiteDTO site2 = new SiteDTO();
        site2.setId(site1.getId());
        assertThat(site1).isEqualTo(site2);

        site2.setId(2L);
        assertThat(site1).isNotEqualTo(site2);

        site1.setId(null);
        assertThat(site1).isNotEqualTo(site2);
    }
}