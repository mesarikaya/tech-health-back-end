package com.mes.techdebt.repository;

import com.mes.techdebt.domain.RecommendationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the {@link RecommendationStatus} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecommendationStatusRepository extends JpaRepository<RecommendationStatus, Long> {
    Optional<RecommendationStatus> findByDescription(String description);
}
