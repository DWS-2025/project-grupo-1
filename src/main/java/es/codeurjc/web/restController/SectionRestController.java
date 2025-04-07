package es.codeurjc.web.restController;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.web.dto.SectionDTO;
import es.codeurjc.web.service.SectionService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/sections")
public class SectionRestController {

    @Autowired
    private SectionService sectionService;

    @GetMapping("/")
    public Page<SectionDTO> getSections(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return sectionService.findAllAsDTO(pageable);
    }



    @GetMapping("/{id}")
    public SectionDTO getSection (@PathVariable long id) {
        if (sectionService.getSection(id) != null) {
            return sectionService.getSection(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found");
        }
        
    }
    
    @PostMapping("/")
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

}
