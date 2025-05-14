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

    @Query("SELECT s FROM Section s ORDER BY s.averageRating DESC")
    List<Section> findSectionByAverageRatingDESC();

    @Query("SELECT s FROM Section s WHERE s.averageRating >= 5")
    List<Section> findSectionGT5();

    @Query("SELECT s FROM Section s WHERE s.numberOfPublications >= 5")
    List<Section> findSectionGT5Publications();

    @Query("""
            SELECT s FROM Section s
            WHERE (:minPosts IS NULL OR s.numberOfPublications >= :minPosts) 
            AND (:minRating IS NULL OR s.averageRating >= :minRating)

            ORDER BY
                CASE WHEN :orderBy = "title" THEN s.title END ASC,
                CASE WHEN :orderBy = "averageRating" THEN s.averageRating END DESC,
                CASE WHEN :orderBy = "numberOfPublications" THEN s.numberOfPublications END DESC
            """)

    List<Section> findFilteredSecions (
        @Param("minPosts") int minPosts,
        @Param("minRating") float minRating,
        @Param("orderBy") String orderBy);


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
