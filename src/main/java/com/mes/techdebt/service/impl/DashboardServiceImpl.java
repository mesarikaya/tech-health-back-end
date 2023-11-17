package com.mes.techdebt.service.impl;

import com.mes.techdebt.domain.AssessmentCriteria;
import com.mes.techdebt.domain.Category;
import com.mes.techdebt.domain.TechArea;
import com.mes.techdebt.domain.TechDomain;
import com.mes.techdebt.repository.AssessmentCriteriaRepository;
import com.mes.techdebt.service.AssessmentCriteriaService;
import com.mes.techdebt.service.AttachmentService;
import com.mes.techdebt.service.DashboardService;
import com.mes.techdebt.service.SiteService;
import com.mes.techdebt.service.dto.AssessmentResultDTO;
import com.mes.techdebt.service.dto.AttachmentDTO;
import com.mes.techdebt.web.rest.request.DashboardRequestDTO;
import com.mes.techdebt.web.rest.request.DashboardResponseDTO;
import com.mes.techdebt.web.rest.request.HierarchyResponseDTO;
import com.mes.techdebt.web.rest.request.HierarchyTuple;
import com.mes.techdebt.web.rest.response.DashboardAttachmentListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final AttachmentService attachmentService;
    private final SiteService siteService;
    private final AssessmentCriteriaRepository assessmentCriteriaRepository;

    private final AssessmentCriteriaService assessmentCriteriaService;

    @Override
    public List<DashboardResponseDTO> getAssessmentResult(DashboardRequestDTO dashboardRequest,
                                                          List<AssessmentResultDTO> assessmentResultDTO) {
        String siteName= dashboardRequest.getName();
        List<DashboardResponseDTO> response = new ArrayList<>();
        Map<String, List<AssessmentResultDTO>> assessmentResultsByHierarchy = assessmentResultDTO.stream()
                .collect(Collectors.groupingBy(assessmentResult ->
                        HierarchyTuple.builder()
                                .assessmentCriteriaDescription(assessmentResult.getAssessmentCriteria().getDescription())
                                .categoryDescription(assessmentResult.getAssessmentCriteria().getCategory().getDescription())
                                .techAreaDescription(assessmentResult.getAssessmentCriteria().getCategory().getTechArea().getDescription())
                                .domainDescription(assessmentResult.getAssessmentCriteria().getCategory().getTechArea().getDomain().getDescription())
                                .build().toString()));
        log.debug("assessment results by hierarchy is: {}", assessmentResultsByHierarchy);

        Long siteId = siteService.getIdByName(siteName);
        List<HierarchyResponseDTO> hierarchyList = this.getHierarchy(Optional.of(Boolean.FALSE));
        hierarchyList.stream().forEach(hierarchy -> {
            List<AssessmentResultDTO> assessments = new ArrayList<>();
            List<DashboardAttachmentListDTO> attachmentDownloadLinks = new ArrayList<>();
            String keyToCompare = hierarchy.getAssessmentCriteriaDescription() + "-" + hierarchy.getCategoryDescription()
                    + "-" + hierarchy.getTechAreaDescription() + '-' + hierarchy.getDomainDescription();

            if (assessmentResultsByHierarchy.containsKey(keyToCompare)){
                assessments = assessmentResultsByHierarchy.get(keyToCompare);
                log.debug("Assessment criteria description: {}", hierarchy.getAssessmentCriteriaDescription());
                Long categoryId = assessmentCriteriaService.getIdByDescription(hierarchy.getAssessmentCriteriaDescription());
                List<AttachmentDTO> attachmentDTO = attachmentService.findBySiteIdAndAssessmentCriteriaId(siteId, categoryId)
                        .orElse(null);
                if (!attachmentDTO.isEmpty()){
                    attachmentDownloadLinks = attachmentDTO.stream().map(attachment ->
                        DashboardAttachmentListDTO.builder()
                                .attachmentEndpoint(String.format("/attachments/%s",attachment.getId()))
                                .id(attachment.getId())
                                .fileSize(attachment.getFileSize())
                                .fileType(attachment.getFileType())
                                .fileName(attachment.getFileName())
                                .updateDate(attachment.getUpdateDate())
                                .updatedBy(attachment.getUpdatedBy())
                                .build()
                    ).collect(Collectors.toList());
                }
            }

            AssessmentCriteria criteria = assessmentCriteriaRepository.findByDescription(hierarchy.getAssessmentCriteriaDescription()).orElse(null);
            DashboardResponseDTO data = DashboardResponseDTO.builder()
                    .siteName(dashboardRequest.getName())
                    .siteId(siteId)
                    .assessmentCriteriaDescription(hierarchy.getAssessmentCriteriaDescription())
                    .assessmentCriteriaId(hierarchy.getAssessmentCriteriaId())
                    .techStandardDescription(criteria != null ? criteria.getTechStandardDescription() : null)
                    .categoryDescription(hierarchy.getCategoryDescription())
                    .categoryId(hierarchy.getCategoryId())
                    .techAreaDescription(hierarchy.getTechAreaDescription())
                    .techAreaId(hierarchy.getTechAreaId())
                    .domainDescription(hierarchy.getDomainDescription())
                    .domainId(hierarchy.getDomainId())
                    .assessmentResults(assessments)
                    .attachments(attachmentDownloadLinks)
                    .build();
            response.add(data);
        });

        return response;
    }

    @Override
    public List<HierarchyResponseDTO> getHierarchy(Optional<Boolean> isOnlyActive) {

        List<HierarchyResponseDTO> hierarchyList = getHierarchies(isOnlyActive);
        return hierarchyList;
    }

    private List<HierarchyResponseDTO> getHierarchies(Optional<Boolean> isOnlyActive) {
        List<AssessmentCriteria> assessmentCriteriaList = assessmentCriteriaRepository
                .findAll();

        List<HierarchyResponseDTO> hierarchyList = new ArrayList<>();
        Comparator<AssessmentCriteria> sortByCriteria = Comparator.comparing(AssessmentCriteria::getDescription);
        assessmentCriteriaList.stream().filter(assessmentCriteria -> assessmentCriteria.getIsActive())
                .sorted(sortByCriteria).forEach(
                        assessmentCriteria ->     {
                            Category category = assessmentCriteria.getCategory();
                            TechArea techArea = category.getTechArea();
                            TechDomain techDomain = techArea.getDomain();

                            HierarchyResponseDTO hierarchy = HierarchyResponseDTO.builder()
                                    .assessmentCriteriaDescription(assessmentCriteria.getDescription())
                                    .assessmentCriteriaId(assessmentCriteria.getId())
                                    .categoryDescription(category.getDescription())
                                    .categoryId(category.getId())
                                    .techAreaDescription(techArea.getDescription())
                                    .techAreaId(techArea.getId())
                                    .domainDescription(techDomain.getDescription())
                                    .domainId(techDomain.getId())
                                    .build();

                            if (isOnlyActive.isPresent()){
                                // Technical choice to not add to hierarchy if not active
                                boolean isActiveCategory = category.getIsActive()==null ? true : category.getIsActive();
                                boolean isActiveTechArea = techArea.getIsActive()==null ? true : techArea.getIsActive();
                                boolean isActiveTechDomain = techDomain.getIsActive()==null ? true : techDomain.getIsActive();
                                if (isActiveCategory && isActiveTechArea && isActiveTechDomain){
                                    hierarchyList.add(hierarchy);
                                }
                            }else{
                                hierarchyList.add(hierarchy);
                            }
                        }
                );
        return hierarchyList;
    }
}