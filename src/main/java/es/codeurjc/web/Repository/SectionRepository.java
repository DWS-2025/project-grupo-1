package es.codeurjc.web.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import es.codeurjc.web.Model.Section;

public class SectionRepository {
    
    private AtomicLong nextId = new AtomicLong(1L);
    private ConcurrentHashMap<Long, Section> sections = new ConcurrentHashMap<>();
    
    public List<Section> findAll(){
        return sections.values().stream().toList();
    }

    public Optional<Section> findById(long id) {
        return Optional.ofNullable(sections.get(id));
    }

    public void saveSectionInRepository (Section section){
        long id = section.getId();

        if (id == 0){
            id = nextId.getAndIncrement();
            section.setId(id);
        }
        sections.put(id, section);
    }

    public void deleteSectionById(Section section){
        sections.remove(section.getId());
    }


}
