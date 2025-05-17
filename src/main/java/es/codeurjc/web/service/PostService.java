package es.codeurjc.web.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.engine.jdbc.BlobProxy;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.dto.CreatePostDTO;
import es.codeurjc.web.dto.PostDTO;
import es.codeurjc.web.dto.PostMapper;
import es.codeurjc.web.dto.UserBasicDTO;
import es.codeurjc.web.dto.UserMapper;
import es.codeurjc.web.model.Comment;
import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    PostMapper postMapper;

    public Collection<Post> findAll() {
        return postRepository.findAll();
    }

    public Collection<PostDTO> findAllAsDTO() {
        return toDTOs(postRepository.findAll());
    }

    public Page<CreatePostDTO> findAllAsCreateDTO(Pageable pageable) {
        return postRepository.findAll(pageable).map(this::toCreatePostDTO);
    }

    public Page<PostDTO> findAllAsDTO(Pageable pageable) {
        return postRepository.findAll(pageable).map(this::toDTO);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public PostDTO findByIdAsDTO(Long id) {
        return toDTO(findById(id).orElseThrow());
    }

    public boolean existsById(Long id) {
        return postRepository.existsById(id);
    }

    public Post save(Post post, MultipartFile imageFile, HttpServletRequest request) throws IOException { // Swapped from Post to void

        if (!imageFile.isEmpty()) {
            post.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        } 
        post.setContent(sanitizeHtml(post.getContent()));
        post.setTitle(sanitizeHtml(post.getTitle()));
        return save(post,request);

    }

    public PostDTO save(PostDTO postDTO, MultipartFile imagFile, HttpServletRequest request) throws IOException {
        return toDTO(save(toDomain(postDTO), imagFile, request));
    }

    public PostDTO save(CreatePostDTO postDTO, MultipartFile imagFile, HttpServletRequest request) throws IOException {
        return toDTO(save(toDomain(postDTO), imagFile, request));
    }

    public Post save(Post post, HttpServletRequest request) { // Swapped from Post to void

        User currentUser = userMapper.toDomain(userService.getLoggedUser(request.getUserPrincipal().getName()));
        post.setOwner(currentUser);
        currentUser.getPosts().add(post);
        post.setContent(sanitizeHtml(post.getContent()));
        post.setTitle(sanitizeHtml(post.getTitle()));

        List<Section> sections = post.getSections();
        for (Section section : sections) {
            section.addPost(post);
        }

        List<User> contributors = post.getContributors();
        for (User contributor : contributors) {
            contributor.addCollaboratedPosts(post);
        }

        post = postRepository.save(post);

        post.setImage("/api/posts/" + post.getId() + "/image");

        postRepository.save(post);

        return post;

    }

    public PostDTO save(PostDTO postDTO) {
        return toDTO(toDomain(postDTO));
    }

    public void saveForInit(Post post) {
        postRepository.save(post);
    }

    public void saveOtherUsersPost(Post post, User user) {
        post.setOwner(user);
        post.setTitle(sanitizeHtml(post.getTitle()));
        post.setContent(sanitizeHtml(post.getContent()));
        postRepository.save(post);
    }

    public void deletePost(Long id) {
 
        Post post = postRepository.findById(id).orElseThrow();

        for (Section section : post.getSections()) {
            section.getPosts().remove(post);
            sectionService.saveSection(section);
        }

    
        List<Comment> commentsCopy = new ArrayList<>(post.getComments());
        for (Comment comment : commentsCopy) {
            commentService.deleteCommentFromPost(post.getId(), comment.getId());
        }

        post.getContributors().clear();
        post.getSections().clear();
        post.getComments().clear();

        postRepository.deleteById(post.getId());
    }

    // public void deletePost(PostDTO postDTO) {
    //     deletePost(toDomain(postDTO));
    // }

   public Post updatePost(Long id, Post newPost, List<Long> newSectionIds, String[] newContributorsStrings, MultipartFile newImage) throws IOException {
    Post oldPost = postRepository.findById(id).orElseThrow();
    oldPost.setTitle(sanitizeHtml(newPost.getTitle()));
    oldPost.setContent(sanitizeHtml(newPost.getContent()));

    for (Section s : oldPost.getSections()) {
        s.getPosts().remove(oldPost);
    }
    oldPost.getSections().clear();


    if (newSectionIds != null && !newSectionIds.isEmpty()) {
        for (Long sectionId : newSectionIds) {
            Section section = sectionService.findSectionById(sectionId).get();
            oldPost.getSections().add(section);
            if (!section.getPosts().contains(oldPost)) {
                section.getPosts().add(oldPost);
            }
        }
    }

    if (newContributorsStrings != null && newContributorsStrings.length > 0) {
        oldPost.getContributors().clear();
        addContributors(oldPost, newContributorsStrings);
    }

    if (!newImage.isEmpty()) {
        oldPost.setImageFile(BlobProxy.generateProxy(newImage.getInputStream(), newImage.getSize()));
    }

    postRepository.save(oldPost);
  
    return oldPost;
}
    public PostDTO updatePost(Long id, CreatePostDTO newCreatePostDTO, List<Long> newSectionIds, String[] newContributorsStrings, MultipartFile newImage) throws IOException {
        return toDTO(updatePost(id, toDomain(newCreatePostDTO), newSectionIds, newContributorsStrings, newImage));
    }

    public CommentService getCommentService() {
        return this.commentService;
    }

    public void setAverageRatingPostRemoving(Long id, Long commentRemovedId) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setAverageRating(postRepository.findAverageRatingByPostIdExcludingComment(id,commentRemovedId));
        
      
    }
    public void setAverageRatingPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setAverageRating(postRepository.findAverageRatingByPostId(id));
        
      
    }

    public void addSections(Post post, List<Long> sectionIds) {
        
        if (sectionIds != null && !sectionIds.isEmpty()) {
        
            for (Long sectionId : sectionIds) {
                post.addSection(sectionService.toDomain(sectionService.findById(sectionId).get()));
            }

        }

    }

    public void addSections(PostDTO postDTO, List<Long> sectionIds) {
        addSections(toDomain(postDTO), sectionIds);
    }

    public void addSections(CreatePostDTO createPostDTO, List<Long> sectionIds) {
        addSections(toDomain(createPostDTO), sectionIds);
    }

    public void addContributor(Post post, User user) {
        post.addContributor(user);
    }

    public void addContributor(CreatePostDTO createPostDTO, UserBasicDTO userBasicDTO) {
        addContributor(toDomain(createPostDTO), userMapper.toBasicDomain(userBasicDTO));
    }

    public void addContributors(Post post, String[] contributorNames) {
        UserBasicDTO userBasicDTO;
        if (contributorNames != null && contributorNames.length > 0) {
            
            for (String colaborator : contributorNames) {
                try {
                    userBasicDTO = userService.findByUserNameBasicDTO(colaborator);
                    if (userBasicDTO != null) {
                        addContributor(post, userMapper.toBasicDomain(userBasicDTO));
                    }
                } catch (NoSuchElementException e) {
                    // Handle the case where the user is not found
                    System.out.println("User not found: " + colaborator);
                }
            }

        }
    }

    public void addContributors(CreatePostDTO createPostDTO, String[] contributorNames) {
        addContributors(toDomain(createPostDTO), contributorNames);
    }

    public void updateSections(Post post, List<Section> oldSections, List<Section> newSections) {
        for (Section section : newSections) {
            if (!post.getSections().contains(section)) {
                post.addSection(section);
                section.addPost(post);
            }
        }
    }

    public void updateSections(PostDTO postDTO, List<Section> oldSections, List<Section> newSections) {
        updateSections(toDomain(postDTO), oldSections, newSections);
    }

    public void createPostImage(Long id, URI location, InputStream inputStream, Long size) {
        
        Post post = postRepository.findById(id).orElseThrow();
        post.setImage(location.toString());
        post.setImageFile(BlobProxy.generateProxy(inputStream, size));

        postRepository.save(post);
    }

    public Resource getImageFile(Long id) throws SQLException {

        Post post = postRepository.findById(id).orElseThrow();

        if (post.getImageFile() != null) {
            return new InputStreamResource(post.getImageFile().getBinaryStream());
        } else {
            throw new NoSuchElementException();
        }
    }

    public void replacePostImage(Long id, InputStream inputStream, Long size) {

        Post post = postRepository.findById(id).orElseThrow();

        if (post.getImage() == null) {
            throw new NoSuchElementException();
        }

        post.setImageFile(BlobProxy.generateProxy(inputStream, size));

        postRepository.save(post);
    }

    public String sanitizeHtml(String htmlContent) {
        // Use a predefined safelist to allow only basic HTML tags
        return Jsoup.clean(htmlContent, Safelist.relaxed());
    }

    public void deletePostImage(Long id) {

        Post post = postRepository.findById(id).orElseThrow();

        if (post.getImage() == null) {
            throw new NoSuchElementException();
        }

        post.setImageFile(null);
        post.setImage(null);

        postRepository.save(post);
    }

    public List<Map<String, Object>> preparePostSectionsForForm(Long id) {
        
        Post post = findById(id).orElseThrow();
        Collection<Section> allSections = sectionService.findAll();
        List<Section> postSections = post.getSections();

        Set<Long> postSectionIds = postSections.stream().map(Section::getId).collect(Collectors.toSet());

        List<Map<String, Object>> markedSections = allSections.stream().map(section -> {
            Map<String, Object> sectionData = new HashMap<>();
            sectionData.put("id", section.getId());
            sectionData.put("title", section.getTitle());
            sectionData.put("selected", postSectionIds.contains(section.getId()));
            return sectionData;
        }).toList();

        return markedSections;
    }

    public String contributorsToString(Long id) {

        Post post = findById(id).orElseThrow();
        String contributors = "";
        for (User user : post.getContributors()) {
            contributors += user.getUserName() + ", ";
        }

        return contributors;

    }

    public ResponseEntity<Object> getImageFileFromId(Long id) throws SQLException {
        Post post = findById(id).orElseThrow();
        Blob image = post.getImageFile();
        Resource file = new InputStreamResource(image.getBinaryStream());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(image.length()).body(file);
    }

    public void simpleSave(Post post) {
        postRepository.save(post);
    }

    private PostDTO toDTO(Post post) {
        return postMapper.toDTO(post);
    }

    private CreatePostDTO toCreatePostDTO(Post post) {
        return postMapper.toCreatePostDTO(post);
    }

    private Post toDomain(PostDTO postDTO) {
        return postMapper.toDomain(postDTO);
    }

    private Post toDomain(CreatePostDTO postDTO) {
        return postMapper.toDomain(postDTO);
    }

    private Collection<PostDTO> toDTOs(Collection<Post> posts) {
        return postMapper.toDTOs(posts);
    }

}
