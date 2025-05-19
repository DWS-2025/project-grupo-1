package es.codeurjc.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import es.codeurjc.web.model.Section;

/**
 * Repository interface for managing {@link Section} entities.
 * <p>
 * Extends {@link JpaRepository} to provide basic CRUD operations and
 * {@link JpaSpecificationExecutor} for advanced query capabilities.
 * </p>
 *
 * <p>
 * Custom query methods:
 * <ul>
 *   <li>{@code findSectionByTitleASC()} - Retrieves all sections ordered by title in ascending order.</li>
 *   <li>{@code findSectionAverageRatingGT5()} - Retrieves sections with an average rating greater than or equal to 5.</li>
 *   <li>{@code findSectionPublicationsGT2()} - Retrieves sections with number of publications greater than or equal to 2.</li>
 *   <li>{@code findSectionPostsGTE2ByTitle()} - Retrieves sections with at least 2 publications, ordered by title.</li>
 *   <li>{@code findSectionPostsGTE2AverageRatingGT5()} - Retrieves sections with at least 2 publications and average rating at least 5, ordered by title.</li>
 *   <li>{@code findSectionAverageRatingGTE5ByTitle()} - Retrieves sections with average rating at least 5, ordered by title.</li>
 *   <li>{@code findSectionAverageRatingGT5PublicationsGTE2()} - Retrieves sections with average rating at least 5 and at least 2 publications.</li>
 * </ul>
 * </p>
 * 
 * @author Grupo 1
 */
public interface SectionRepository extends JpaRepository<Section, Long>, JpaSpecificationExecutor<Section> {

    @Query("SELECT s FROM Section s ORDER BY s.title ASC")
    List<Section> findSectionByTitleASC();

    @Query("SELECT s FROM Section s WHERE s.averageRating >= 5")
    List<Section> findSectionAverageRatingGT5();

    @Query("SELECT s FROM Section s WHERE s.numberOfPublications >= 2")
    List<Section> findSectionPublicationsGT2();

    @Query("SELECT s FROM Section s WHERE s.numberOfPublications >= 2 ORDER BY s.title ASC")
    List<Section> findSectionPostsGTE2ByTitle();

    @Query("SELECT s FROM Section s WHERE s.numberOfPublications >= 2 AND s.averageRating >= 5 ORDER BY s.title")
    List<Section> findSectionPostsGTE2AverageRatingGT5();

    @Query("SELECT s FROM Section s WHERE s.averageRating >= 5 ORDER BY s.title ASC")
    List<Section> findSectionAverageRatingGTE5ByTitle();

    @Query("SELECT s FROM Section s WHERE s.averageRating >= 5 AND s.numberOfPublications >= 2")
    List<Section> findSectionAverageRatingGT5PublicationsGTE2();

}
