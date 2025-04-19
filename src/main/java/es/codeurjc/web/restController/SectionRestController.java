package es.codeurjc.web.restController;

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
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.web.dto.SectionDTO;
import es.codeurjc.web.service.SectionService;




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
/*
    @GetMapping("")
    public ResponseEntity<List<SectionDTO>> getSections(@RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<SectionDTO> sectionPage = sectionService.findAllAsDTO(pageable);
        return ResponseEntity.ok(sectionPage.getContent());
    }
 */


    @GetMapping("/{id}")
    public SectionDTO getSection (@PathVariable long id) {
        if (sectionService.getSection(id) != null) {
            return sectionService.getSection(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found");
        }
        
    }
    
    @PostMapping("")
    public ResponseEntity<SectionDTO> createSection(@RequestBody SectionDTO sectionDTO) {
        sectionDTO = sectionService.saveSection(sectionDTO);
        
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(sectionDTO.id()).toUri(); // URI for the new section 

        return ResponseEntity.created(location).body(sectionDTO);
    }

    @PutMapping("/{id}")
    public SectionDTO updateSection(@PathVariable long id, @RequestBody SectionDTO oldSectionDTO, MultipartFile newImage) throws IOException {

        SectionDTO newSection = sectionService.getSection(id);
        
        if (newSection == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found");
        } else {
            return sectionService.update(newSection, oldSectionDTO, newImage);
        }
    }
    
    @DeleteMapping("/{id}")
    public SectionDTO deleteSection(@PathVariable long id){

        SectionDTO section = sectionService.getSection(id);

        if (section == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found");
        } else {
            return sectionService.deleteSection(section);
        }

    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getImageFile(@PathVariable long id) throws SQLException, IOException {
        Resource sectionImage = sectionService.getSectionImage(id);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(sectionImage);
    }
    

    @PostMapping("/{id}/image")
	public ResponseEntity<Object> createSectionImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {

		URI location = fromCurrentRequest().build().toUri();

		sectionService.createSectionImage(id, location, imageFile.getInputStream(), imageFile.getSize());

		return ResponseEntity.created(location).build();

	}

    @PutMapping("/{id}/image")
	public ResponseEntity<Object> replaceSectionImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {

		sectionService.replaceSectionImage(id, imageFile.getInputStream(), imageFile.getSize());

		return ResponseEntity.noContent().build();
	}

    @DeleteMapping("/{id}/image")
	public ResponseEntity<Object> deleteSectionImage(@PathVariable long id) throws IOException {

		sectionService.deleteSectionImage(id);

		return ResponseEntity.noContent().build();
	}

}
