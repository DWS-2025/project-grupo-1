package es.codeurjc.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import es.codeurjc.web.model.Section;

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
