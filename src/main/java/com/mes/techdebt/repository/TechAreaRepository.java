package com.mes.techdebt.repository;

import com.mes.techdebt.domain.TechArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the {@link TechArea} entity.
 */
@Repository
public interface TechAreaRepository extends JpaRepository<TechArea, Long> {

    Optional<TechArea> findByDescription(String description);
}
