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


    public void deleteSection(long sectionId){

        List<User> users = userRepository.findAll();
        Section section = sectionRepository.findById(sectionId).get();

        for (User user : users){
            if(user.getFollowedSections().contains(section)){
                user.getFollowedSections().remove(section);
            }
        }
        sectionRepository.deleteSectionById(sectionId);

    }



}
