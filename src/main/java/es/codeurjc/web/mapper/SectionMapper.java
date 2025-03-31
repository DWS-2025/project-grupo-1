package es.codeurjc.web.mapper;

import java.util.List;

import es.codeurjc.web.dto.SectionDTO;  
import es.codeurjc.web.model.Section;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
// This interface is used to map between Section and SectionDTO objects
public interface SectionMapper {
    SectionDTO toDTO(Section section); 

    Section toDomain(SectionDTO sectionDTO); 

    List<SectionDTO> toDTOs(List<Section> sections); 

    List<Section> toDomains(List<SectionDTO> sectionDTOs); 


    
}
