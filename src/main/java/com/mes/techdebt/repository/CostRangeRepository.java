package com.mes.techdebt.repository;

import com.mes.techdebt.domain.CostRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the {@link CostRange} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CostRangeRepository extends JpaRepository<CostRange, Long> {
    Optional<CostRange> findByDescription(String description);
}
