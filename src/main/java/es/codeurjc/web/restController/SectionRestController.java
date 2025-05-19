package es.codeurjc.web.restController;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.web.dto.CreateSectionDTO;
import es.codeurjc.web.dto.SectionDTO;
import es.codeurjc.web.service.SectionService;

/**
 * REST controller for managing Section resources.
 * <p>
 * Provides endpoints for CRUD operations on sections, as well as endpoints for managing section images.
 * </p>
 *
 * <ul>
 *   <li>GET /api/sections - Retrieves a paginated list of sections.</li>
 *   <li>GET /api/sections/{id} - Retrieves a specific section by its ID.</li>
 *   <li>POST /api/sections - Creates a new section.</li>
 *   <li>PUT /api/sections/{id} - Updates an existing section's details and optionally its image.</li>
 *   <li>DELETE /api/sections/{id} - Deletes a section by its ID.</li>
 *   <li>GET /api/sections/{id}/image - Retrieves the image associated with a section.</li>
 *   <li>POST /api/sections/{id}/image - Uploads a new image for a section.</li>
 *   <li>PUT /api/sections/{id}/image - Replaces the image of a section.</li>
 *   <li>DELETE /api/sections/{id}/image - Deletes the image of a section.</li>
 * </ul>
 *
 * <p>
 * Uses {@link SectionService} for business logic and data access.
 * </p>
 * 
 * @author Grupo 1
 */
@RestController
@RequestMapping("/api/sections")
public class SectionRestController {

    @Autowired
    private SectionService sectionService;

    @GetMapping("")
    public Page<SectionDTO> getSections(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return sectionService.findAllAsDTO(pageable);
    }

    @GetMapping("/{id}")
    public SectionDTO getSection(@PathVariable Long id) {
        if (sectionService.getSection(id) != null) {
            return sectionService.getSection(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found");
        }

    }

    @PostMapping("")
    public ResponseEntity<SectionDTO> createSection(@RequestBody SectionDTO sectionDTO) {
        sectionDTO = sectionService.saveSection(sectionDTO);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(sectionDTO.id()).toUri(); // URI for the new
                                                                                                   // Section

        return ResponseEntity.created(location).body(sectionDTO);
    }

    @PutMapping("/{id}")
    public SectionDTO updateSection(
            @PathVariable Long id, @RequestParam String title, @RequestParam String description,
            @RequestParam(required = false) MultipartFile newImage) throws IOException {

        SectionDTO oldSection = sectionService.getSection(id);

        if (oldSection == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found");
        } else {
            CreateSectionDTO newSectionDTO = new CreateSectionDTO(title, description);
            return sectionService.update(oldSection, sectionService.toDTO(newSectionDTO), newImage);
        }
    }

    @DeleteMapping("/{id}")
    public SectionDTO deleteSection(@PathVariable Long id) {

        SectionDTO section = sectionService.getSection(id);

        if (section == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found");
        } else {
            return sectionService.deleteSection(section);
        }

    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getImageFile(@PathVariable Long id) throws SQLException, IOException {
        Resource sectionImage = sectionService.getSectionImage(id);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(sectionImage);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Object> createSectionImage(@PathVariable Long id, @RequestParam MultipartFile imageFile)
            throws IOException {

        URI location = fromCurrentRequest().build().toUri();

        sectionService.createSectionImage(id, location, imageFile.getInputStream(), imageFile.getSize());

        return ResponseEntity.created(location).build();

    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> replaceUserImage(@PathVariable Long id, @RequestParam MultipartFile imageFile)
            throws IOException {
        sectionService.replaceSectionImage(id, imageFile.getInputStream(), imageFile.getSize());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Object> deleteSectionImage(@PathVariable Long id) throws IOException {

        sectionService.deleteSectionImage(id);

        return ResponseEntity.noContent().build();
    }

}
