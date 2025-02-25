package es.codeurjc.web.service;

import java.lang.foreign.Linker.Option;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import es.codeurjc.web.Model.Section;
import es.codeurjc.web.Model.User;
import es.codeurjc.web.Repository.SectionRepository;
import es.codeurjc.web.Repository.UserRepository;

public class SectionService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SectionRepository sectionRepository;

    public List<Section> findAll(){
        return sectionRepository.findAll();
    }

    public void saveSection(Section section){
        sectionRepository.saveSectionInRepository(section);
    }


    public void deleteSection(Section sectionToDelete){

        List<User> users = userRepository.findAll();
        Section section = sectionRepository.findById(sectionToDelete.getId()).get();

        for (User user : users){ //delete section from followed sections of all users
            if(user.getFollowedSections().contains(section)){
                user.getFollowedSections().remove(section);
            }
        }
        sectionRepository.deleteSectionById(sectionToDelete);

    }



}
