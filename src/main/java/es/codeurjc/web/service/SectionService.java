package es.codeurjc.web.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.engine.jdbc.BlobProxy;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.dto.CreateSectionDTO;
import es.codeurjc.web.dto.SectionDTO;
import es.codeurjc.web.dto.SectionMapper;
import es.codeurjc.web.dto.UserMapper;
import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.CommentRepository;
import es.codeurjc.web.repository.SectionRepository;
import es.codeurjc.web.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

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

    public Collection<Section> findAll() {
        return sectionRepository.findAll();
    }

    public Collection<SectionDTO> findAllDTO() {
        return toDTOs(findAll());
    }

    public Collection<Section> findAll(Example<Section> example) { // Example is a Spring Data interface for creating queries
        return sectionRepository.findAll(example);
    }

    public Collection<SectionDTO> findAllDTO(Example<Section> example) {
        return toDTOs(findAll(example));
    }

    public Optional<SectionDTO> findById(long id) {
        return toDTO(sectionRepository.findById(id));
    }

    public Optional<Section> findSectionById(long id) {
        return sectionRepository.findById(id);
    }

    public Page<SectionDTO> findAllAsDTO(Pageable pageable) {
        return sectionRepository.findAll(pageable).map(this::toDTO);
    }

    public List<Section> findAll(Example<Section> example, Sort sort) {
        return sectionRepository.findAll(example, sort);
    }

    public List<Section> findAll(Sort sort) {
        return sectionRepository.findAll(sort);
    }

    public Collection<SectionDTO> getAllSections() {
        return toDTOs(sectionRepository.findAll());
    }

    public String sanitizeHtml(String htmlContent) {
        // Use a predefined safelist to allow only basic HTML tags
        return Jsoup.clean(htmlContent, Safelist.relaxed());
    }

    protected void saveSection(Section section) {
        sectionRepository.save(section);
    }

    public SectionDTO saveSection(SectionDTO sectionDTO) {
        Section section = toDomain(sectionDTO);
        section.setTitle(sanitizeHtml(section.getTitle()));
        section.setDescription(sanitizeHtml(section.getDescription()));

        saveSection(section);

        return toDTO(section);
    }

    public void saveSectionWithImageSection(CreateSectionDTO sectionDTO, MultipartFile imageFile) throws IOException {
        
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        String sanitizedDescription = policy.sanitize(sectionDTO.description());
        String sanitizedTitle = sanitizeHtml(sectionDTO.title());
        Section section = new Section(sanitizedTitle, sanitizedDescription);

        if (!imageFile.isEmpty()) {
            section.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        this.saveSection(section);
    }

    public void createSectionImage(long id, URI location, InputStream inputStream, long size) {
        Section section = sectionRepository.findById(id).orElseThrow();

        section.setImage(location.toString()); // Set the image URL or path here
        section.setImageFile(BlobProxy.generateProxy(inputStream, size)); // Converts inputStream to Blob
        sectionRepository.save(section);
    }

    public Resource getSectionImage(long id) throws SQLException {
        Section section = sectionRepository.findById(id).orElseThrow();

        if (section.getImageFile() != null) {
            return new InputStreamResource(section.getImageFile().getBinaryStream()); 
        } else {
            throw new NoSuchElementException();
        }
    }

    public void replaceSectionImage(long id, InputStream inputStream, long size) {
        Section section = sectionRepository.findById(id).orElseThrow();

        if (section.getImage() == null) {
            throw new NoSuchElementException();
        }

        section.setImageFile(BlobProxy.generateProxy(inputStream, size)); // Converts inputStream to Blob

        sectionRepository.save(section);
    }

    public void deleteSectionImage(long id) {
        Section section = sectionRepository.findById(id).orElseThrow();

        if (section.getImage() == null) {
            throw new NoSuchElementException();
        }

        section.setImageFile(null); // Set the image URL or path here
        section.setImage(null); // Set the image URL or path here

    }

    public SectionDTO deleteSection(SectionDTO sectionDTO) {

        Section sectionToDelete = toDomain(sectionDTO);

        List<User> users = userRepository.findAll();
        Section section = sectionRepository.findById(sectionToDelete.getId()).get();

        for (User user : users) { // delete section from followed sections of all users
            if (user.getFollowedSections().contains(section)) {
                user.getFollowedSections().remove(section);
            }
        }
        for (Post post : section.getPosts()) {
            post.getSections().remove(section);
        }
        sectionRepository.delete(sectionToDelete);
        return toDTO(sectionToDelete);
    }

    public void addPost(Section section, Post post) {
        section.addPost(post);
    }

    public void deletePost(Section section, Post post) {
        section.deletePost(post);
    }

    public SectionDTO update(SectionDTO oldSectionDTO, SectionDTO updatedSectionDTO, MultipartFile newImage) 
            throws IOException {
        Section oldSection =  sectionRepository.findById(oldSectionDTO.id()).orElseThrow();
        //Section updatedSection = toDomain(updatedSectionDTO);

        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        String updatedSanitizedDescription = policy.sanitize(updatedSectionDTO.description());
        String updatedSanitizedTitle = sanitizeHtml(updatedSectionDTO.title());

        oldSection.setTitle(updatedSanitizedDescription);
        oldSection.setDescription(sanitizeHtml(updatedSanitizedTitle));

        if (!newImage.isEmpty()) {
            Blob updatedImage = BlobProxy.generateProxy(newImage.getInputStream(), newImage.getSize()); // converts
                                                                                                        // MultipartFile
                                                                                                        // to Blob
            oldSection.setImageFile(updatedImage);
        }

        sectionRepository.save(oldSection);
        return toDTO(oldSection);
    }

    public Collection<Section> getSectionsFromIdsList(List<Long> sectionIds) {
        Collection<Section> sections = new ArrayList<>();
        SectionDTO sectionDTO;

        for (Long sectionId : sectionIds) {
            sectionDTO = findById(sectionId).orElse(null);
            if (sectionDTO != null) {
                sections.add(toDomain(sectionDTO));
            }
        }

        return sections;
    }

    public Collection<SectionDTO> getSectionsFromIdsListDTO(List<Long> sectionIds) {
        return toDTOs(getSectionsFromIdsList(sectionIds));
    }

    public Collection<SectionDTO> findNotFollowedSections(HttpServletRequest request) {
        List<Section> allSections = sectionRepository.findAll();
        List<Section> followedSections = userMapper.toDomain(userService.getLoggedUser(request.getUserPrincipal().getName())).getFollowedSections();
    
        // Filter only the sections that are NOT in the list of followed sections
        if (followedSections != null) {
            List<Section> notFollowedSections = allSections.stream()
                    .filter(section -> !followedSections.contains(section))
                    .collect(Collectors.toList());
            return toDTOs(notFollowedSections);
        } else {
            return toDTOs(allSections);
        }

    }

    private SectionDTO toDTO(Section section) {
        return mapper.toDTO(section);
    }

    private Optional<SectionDTO> toDTO(Optional<Section> section) {
        return section.map(this::toDTO); // if present, convert to DTO
    }

    Section toDomain(SectionDTO sectionDTO) {
        return mapper.toDomain(sectionDTO);
    }

    private Collection<SectionDTO> toDTOs(Collection<Section> sections) {
        return mapper.toDTOs(sections);
    }

    private Collection<Section> toDomains(Collection<SectionDTO> sectionsDTO) {
        return mapper.toDomains(sectionsDTO);

    }

    public Collection<SectionDTO> getSections() {
        return toDTOs(sectionRepository.findAll());
    }

    public SectionDTO getSection(Long id) {
        return toDTO(sectionRepository.findById(id).orElseThrow());
    }

    public Collection<SectionDTO> getSectionByTitltesASC() {
        return toDTOs(sectionRepository.findSectionByTitleASC());
    }

    public Collection<SectionDTO> getSectionAverageRatingGT5() {
        return toDTOs(sectionRepository.findSectionAverageRatingGT5());
    }

    public Collection<SectionDTO> getSectionPublicationsGT2() {
        return toDTOs(sectionRepository.findSectionPublicationsGT2());
    }

    public Collection<SectionDTO> getSectionPostsGTE2ByTitle() {
        return toDTOs(sectionRepository.findSectionPostsGTE2ByTitle());
    }

    public Collection<SectionDTO> getSectionPostsGTE2AverageRatingGT5() {
        return toDTOs(sectionRepository.findSectionPostsGTE2AverageRatingGT5());
    }

    public Collection<SectionDTO> getSectionAverageRatingGTE5ByTitle() {
        return toDTOs(sectionRepository.findSectionAverageRatingGTE5ByTitle());
    }

    public Collection<SectionDTO> getSectionAverageRatingGT5PublicationsGTE2() {
        return toDTOs(sectionRepository.findSectionAverageRatingGT5PublicationsGTE2());
    }

}
