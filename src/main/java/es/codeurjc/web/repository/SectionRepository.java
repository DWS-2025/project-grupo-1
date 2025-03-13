package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Section;


public interface SectionRepository extends JpaRepository<Section, Long> {

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
