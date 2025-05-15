package es.codeurjc.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /* 

    private AtomicLong nextId = new AtomicLong(1L);
    private ConcurrentHashMap<Long, Section> sections = new ConcurrentHashMap<>();

    public List<Section> findAll() {
        return sections.values().stream().toList();
    }

    public Optional<Section> findById(long id) {
        return Optional.ofNullable(sections.get(id));
    }

    public void save(Section section) {
        long id = section.getId();
        if (id == 0) {
            id = nextId.getAndIncrement();
            section.setId(id);
        }
        sections.put(id, section);
    }

    public void deleteSectionById(Section section) {
        sections.remove(section.getId());
    }

    */
}
