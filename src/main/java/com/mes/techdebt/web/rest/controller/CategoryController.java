package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.service.CategoryService;
import com.mes.techdebt.service.TechAreaService;
import com.mes.techdebt.service.TechDomainService;
import com.mes.techdebt.service.dto.CategoryDTO;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import com.mes.techdebt.domain.Category;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link Category}.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Read') or hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
public class CategoryController {
    private static final String ENTITY_NAME = "category";
    @Value("${spring.application.name}")
    private String applicationName;

    private final CategoryService categoryService;
    private final TechAreaService techAreaService;
    private final TechDomainService techDomainService;

    /**
     * {@code GET  /categories} : get all the categories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of categories in body.
     */
    @GetMapping(path="/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CategoryDTO>> getAllCategories(@ParameterObject @PageableDefault(size = 100, sort = "description", direction = Sort.Direction.ASC) Pageable pageable) {
        log.debug("REST request to get a page of Categories");
        Page<CategoryDTO> page = categoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /categories/:id} : get the "id" category.
     *
     * @param id the id of the categoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the categoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        log.debug("REST request to get Category : {}", id);
        Optional<CategoryDTO> categoryDTO = categoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(categoryDTO);
    }
	
    /**
     * {@code POST  /categories} : Create a new category.
     *
     * @param categoryDTO the categoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new categoryDTO, or with status {@code 400 (Bad Request)} if the category has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) throws URISyntaxException {
        log.debug("REST request to save Category: {}", categoryDTO);
        if (categoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new category cannot already have an existing id", ENTITY_NAME, "id exists");
        }

        if (categoryService.existsByDescription(categoryDTO.getDescription())) {
            throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
        }

        CategoryDTO result = categoryService.save(categoryDTO);
        return ResponseEntity
                .created(new URI("/api/v1/categories/" + result.getId()))
                .headers(
                        HeaderUtil
                                .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getDescription().toString())
                )
                .body(result);
    }

    private void validateDependencies(CategoryDTO categoryDTO) {
        if(categoryDTO.getTechArea().getId() == null){
            throw new BadRequestAlertException("Entity techArea dependency id not exists", ENTITY_NAME, "techArea id isnull");
        }

        if(categoryDTO.getTechArea() == null){
            throw new BadRequestAlertException("Entity techArea dependency not exists", ENTITY_NAME, "techArea isnull");
        }

        if(!techAreaService.existsByDescription(categoryDTO.getTechArea().getDescription())){
            throw new BadRequestAlertException("Entity techArea dependency not exists", ENTITY_NAME, "techArea notexists");
        }

        if(categoryDTO.getTechArea().getDomain() == null){
            throw new BadRequestAlertException("Entity techDomain dependency not exists", ENTITY_NAME, "techDomain isnull");
        }

        if(categoryDTO.getTechArea().getDomain().getId() == null){
            throw new BadRequestAlertException("Entity techDomain dependency id not exists", ENTITY_NAME, "techDomain id isnull");
        }

        if(!techDomainService.existsByDescription(categoryDTO.getTechArea().getDomain().getDescription())){
            throw new BadRequestAlertException("Entity techDomain dependency not exists", ENTITY_NAME, "techDomain notexists");
        }
    }

    /**
     * {@code PUT  /categories/:id} : Updates an existing category.
     *
     * @param id the id of the categoryDTO to save.
     * @param categoryDTO the categoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoryDTO,
     * or with status {@code 400 (Bad Request)} if the categoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the categoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PutMapping(path="/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable(value = "id", required = false) final Long id, @RequestBody CategoryDTO categoryDTO)
            throws URISyntaxException {
        log.debug("REST request to update Category : {}, {}", id, categoryDTO);

        if (!categoryService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (categoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, categoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (categoryService.existsByDescription(categoryDTO.getDescription()) &&
                    !Objects.equals(id,categoryService
                            .getIdByDescription(categoryDTO.getDescription()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
            }
        }

        CategoryDTO result = categoryService.update(categoryDTO);
        return ResponseEntity
                .ok()
                .headers(
                        HeaderUtil
                                .createEntityUpdateAlert(applicationName, true, ENTITY_NAME, categoryDTO.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /categories/:id} : Partial updates given fields of an existing category, field will ignore if it is null
     *
     * @param id the id of the categoryDTO to save.
     * @param categoryDTO the categoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoryDTO,
     * or with status {@code 400 (Bad Request)} if the categoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the categoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the categoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PatchMapping(path = "/categories/{id}", consumes = { "application/json", "application/merge-patch+json" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> partialUpdateCategory(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody CategoryDTO categoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Category partially : {}, {}", id, categoryDTO);
        if (!categoryService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (categoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, categoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (categoryService.existsByDescription(categoryDTO.getDescription()) &&
                    !Objects.equals(id,categoryService
                            .getIdByDescription(categoryDTO.getDescription()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
            }
        }

        Optional<CategoryDTO> result = categoryService.partialUpdate(categoryDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, categoryDTO.getId().toString())
        );
    }

    /**
     * {@code DELETE  /categories/:id} : delete the "id" category.
     *
     * @param id the id of the categoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @DeleteMapping(path="/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.debug("REST request to delete Category : {}", id);
        categoryService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }
}
