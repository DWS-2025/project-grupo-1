package es.codeurjc.web.dto;

import java.util.Collection;

import es.codeurjc.web.model.Section;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
// This interface is used to map between Section and SectionDTO objects
public interface SectionMapper {
    SectionDTO toDTO(Section section); 
    
     SectionDTO toDTO(CreateSectionDTO section); 

    Section toDomain(SectionDTO sectionDTO); 

    Collection<SectionDTO> toDTOs(Collection<Section> sections); 

    Collection<Section> toDomains(Collection<SectionDTO> sectionDTOs); 
    
}
