package com.mes.techdebt.repository;

import com.mes.techdebt.domain.InvestmentCriticality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the {@link InvestmentCriticality} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InvestmentCriticalityRepository extends JpaRepository<InvestmentCriticality, Long> {
    Optional<InvestmentCriticality> findByDescription(String description);
}
