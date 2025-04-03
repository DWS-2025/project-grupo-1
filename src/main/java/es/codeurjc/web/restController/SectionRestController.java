package es.codeurjc.web.restController;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.web.dto.SectionDTO;
import es.codeurjc.web.service.SectionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/sections")
public class SectionRestController {
    @Autowired
    private SectionService sectionService;

    @GetMapping("/")
    public Collection<SectionDTO> getSections(){
        return sectionService.getSections();
    }
    
    
}
