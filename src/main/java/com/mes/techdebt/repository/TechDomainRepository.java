package com.mes.techdebt.repository;

import com.mes.techdebt.domain.TechDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the {@link TechDomain} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TechDomainRepository extends JpaRepository<TechDomain, Long> {
    Optional<TechDomain> findByDescription(String description);
}
