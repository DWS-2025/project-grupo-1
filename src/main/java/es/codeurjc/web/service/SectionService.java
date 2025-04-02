package es.codeurjc.web.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.CommentRepository;
import es.codeurjc.web.repository.SectionRepository;
import es.codeurjc.web.repository.UserRepository;

@Service
public class SectionService {

    private final CommentService commentService;

    private final CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SectionRepository sectionRepository;

    SectionService(CommentRepository commentRepository, CommentService commentService) {
        this.commentRepository = commentRepository;
        this.commentService = commentService;
    }

    public long count() {
        return sectionRepository.count();  
    }

    public Page<Section> findAll(Pageable pageable){
        return sectionRepository.findAll(pageable);
    }

    public List<Section> findAll(){
        return sectionRepository.findAll();
    }

    public List<Section> findAll(Example<Section> example) {
        return sectionRepository.findAll(example);
    }

    

    public Optional<Section> findById(long id) {
        return sectionRepository.findById(id);
    }

    public void saveSection(Section section){
        sectionRepository.save(section);
    }

    public void saveSectionWithImageSection(Section section, MultipartFile imageFile) throws IOException{
        if(!imageFile.isEmpty()){
            section.setSectionImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        this.saveSection(section);
    }






    public void deleteSection(Section sectionToDelete){

        List<User> users = userRepository.findAll();
        Section section = sectionRepository.findById(sectionToDelete.getId()).get();

        for (User user : users){ //delete section from followed sections of all users
            if(user.getFollowedSections().contains(section)){
                user.getFollowedSections().remove(section);
            }
        }
        sectionRepository.delete(sectionToDelete);

    }

    public void addPost(Section section, Post post) {
        section.addPost(post);
    }

    public void deletePost(Section section, Post post) {
        section.deletePost(post);
    }

    public void update(Section oldSection, Section updatedSection){
        oldSection.setTitle(updatedSection.getTitle());
        oldSection.setDescription(updatedSection.getDescription());
        oldSection.setSectionImage(updatedSection.getSectionImage());
        sectionRepository.save(oldSection);
    }

    public List<Section> getSectionsFromIdsList(List<Long> sectionIds) {
        List<Section> sections = new ArrayList<>();
        Section section;
        
        for (Long sectionId : sectionIds) {
            section = findById(sectionId).orElse(null);
            if (section != null) {
                sections.add(section);
            }
        }

        return sections;
    }

}
