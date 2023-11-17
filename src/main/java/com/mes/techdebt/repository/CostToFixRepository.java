package com.mes.techdebt.repository;

import com.mes.techdebt.domain.CostToFix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the {@link CostToFix} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CostToFixRepository extends JpaRepository<CostToFix, Long> {

    List<CostToFix> findBySite_Id_AndCategory_IdAndCostRange_Id(Long siteId, Long categoryId, Long costRangeId);

    boolean existsBySite_Id(Long siteId);

    boolean existsBySite_IdAndCategory_Id(Long siteId, Long categoryId);
}
