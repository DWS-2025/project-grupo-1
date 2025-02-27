package es.codeurjc.web.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import es.codeurjc.web.Model.Section;

@Component
public class SectionRepository {

    private AtomicLong nextId = new AtomicLong(1L);
    private ConcurrentHashMap<Long, Section> sections = new ConcurrentHashMap<>();

    public SectionRepository() {

        Section defaultSection1 = new Section("Reversing",
                "Análisis y descompilación de binarios para entender su funcionamiento.", "reversing.png");
        Section defaultSection2 = new Section("Hacking Web", "Explotación de vulnerabilidades en aplicaciones web.",
                "hacking_web.png");
        Section defaultSection3 = new Section("Escalada de Privilegios",
                "Métodos para obtener acceso administrativo en Windows.", "escalada_windows.jpeg");
        Section defaultSection4 = new Section("Hardware Hacking",
                "Explotación de vulnerabilidades a nivel de hardware.", "hardware.jpeg");
        Section defaultSection5 = new Section("WiFi", "Ataques y auditorías de seguridad en redes inalámbricas.",
                "wifi.jpg");

        saveSectionInRepository(defaultSection1);
        saveSectionInRepository(defaultSection2);
        saveSectionInRepository(defaultSection3);
        saveSectionInRepository(defaultSection4);
        saveSectionInRepository(defaultSection5);

    }

    public List<Section> findAll() {
        return sections.values().stream().toList();
    }

    public Optional<Section> findById(long id) {
        return Optional.ofNullable(sections.get(id));
    }

    public void saveSectionInRepository(Section section) {
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

}
