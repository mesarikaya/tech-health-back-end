package com.mes.techdebt.repository;

import com.mes.techdebt.domain.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Spring Data SQL repository for the {@link Site} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    Optional<Site> findByName(String name);

    Optional<Site> findByMdmSiteId(Long mdmSiteId);

    Optional<Set<Site>> findSitesByRegionIn(Set<String> name);
}
