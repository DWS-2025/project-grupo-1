package es.codeurjc.web.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.SectionRepository;
import es.codeurjc.web.repository.UserRepository;

@Service
public class SectionService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SectionRepository sectionRepository;

    public List<Section> findAll(){
        return sectionRepository.findAll();
    }

    public Optional<Section> findById(long id) {
        return sectionRepository.findById(id);
    }

    public void saveSection(Section section){
        sectionRepository.save(section);
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

    public void addPost(Section section, Post post) {
        section.addPost(post);
    }

    public void deletePost(Section section, Post post) {
        section.deletePost(post);
    }

}
