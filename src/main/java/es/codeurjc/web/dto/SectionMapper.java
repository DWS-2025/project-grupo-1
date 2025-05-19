package es.codeurjc.web.dto;

import java.util.Collection;

import es.codeurjc.web.model.Section;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between Section domain objects and their corresponding DTOs.
 * Utilizes MapStruct for automatic implementation generation.
 * <p>
 * This interface provides methods to map between:
 * <ul>
 *     <li>{@link Section} and {@link SectionDTO}</li>
 *     <li>{@link CreateSectionDTO} and {@link SectionDTO}</li>
 *     <li>Collections of {@link Section} and {@link SectionDTO}</li>
 * </ul>
 * <p>
 * The {@code @Mapper(componentModel = "spring")} annotation allows this mapper to be managed by the Spring container.
 * 
 * @author Grupo 1
 */
@Mapper(componentModel = "spring")

public interface SectionMapper {
    SectionDTO toDTO(Section section);

    SectionDTO toDTO(CreateSectionDTO section);

    Section toDomain(SectionDTO sectionDTO);

    Collection<SectionDTO> toDTOs(Collection<Section> sections);

    Collection<Section> toDomains(Collection<SectionDTO> sectionDTOs);

}
