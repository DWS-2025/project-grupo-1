package es.codeurjc.web.service;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.web.dto.SectionDTO;
import es.codeurjc.web.dto.SectionMapper;
import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.dto.UserMapper;
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
    @Autowired
    private SectionMapper mapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    SectionService(CommentRepository commentRepository, CommentService commentService) {
        this.commentRepository = commentRepository;
        this.commentService = commentService;
    }

    public long count() {
        return sectionRepository.count();
    }

    public Page<Section> findAll(Pageable pageable) {
        return sectionRepository.findAll(pageable);
    }

    public Collection<SectionDTO> findAll() {
        return toDTOs(sectionRepository.findAll());
    }

    public Collection<SectionDTO> findAll(Example<Section> example) {
        return toDTOs(sectionRepository.findAll(example));
    }

    public Optional<Section> findById(long id) {
        return sectionRepository.findById(id);
    }

    public void saveSection(Section section) {
        sectionRepository.save(section);
    }

    public void saveSectionWithImageSection(Section section, MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            section.setSectionImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        this.saveSection(section);
    }

    public void deleteSection(Section sectionToDelete) {

        List<User> users = userRepository.findAll();
        Section section = sectionRepository.findById(sectionToDelete.getId()).get();

        for (User user : users) { //delete section from followed sections of all users
            if (user.getFollowedSections().contains(section)) {
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

    public void update(Section oldSection, Section updatedSection, MultipartFile newImage) throws IOException {
        oldSection.setTitle(updatedSection.getTitle());
        oldSection.setDescription(updatedSection.getDescription());

        if (!newImage.isEmpty()) {
            Blob updatedImage = BlobProxy.generateProxy(newImage.getInputStream(), newImage.getSize()); // converts MultipartFile to Blob
            oldSection.setSectionImage(updatedImage);
        }

        sectionRepository.save(oldSection);
    }

    public Collection<SectionDTO> findNotFollowedSections() {
        List<Section> allSections = sectionRepository.findAll();
        List<Section> followedSections = userMapper.toDomain(userService.getLoggedUser()).getFollowedSections();
        // Filter only the sections that are NOT in the list of followed sections
        List<Section> notFollowedSections = allSections.stream()
                .filter(section -> !followedSections.contains(section))
                .collect(Collectors.toList());

        return toDTOs(notFollowedSections);
    }

    private SectionDTO toDTO(Section section) {
        return mapper.toDTO(section);
    }

    private Section toDomain(SectionDTO sectionDTO) {
        return mapper.toDomain(sectionDTO);
    }

    private Collection<SectionDTO> toDTOs(Collection<Section> sections) {
        return mapper.toDTOs(sections);
    }

    private Collection<Section> toDomains(Collection<SectionDTO> sectionsDTO) {
        return mapper.toDomains(sectionsDTO);

    }
}
