package es.codeurjc.web.service;

import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.dto.PostDTO;
import es.codeurjc.web.dto.PostMapper;
import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.dto.UserMapper;
import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.PostRepository;

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

    public Collection<PostDTO> findAllDTO() {
        return toDTOs(postRepository.findAll());
    }

    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }

    public Optional<PostDTO> findByIdDTO(long id) {
        return postRepository.findById(id).map(this::toDTO);
    }

    protected void save(Post post, MultipartFile imageFile) throws IOException { // Swapped from Post to void

        if (!imageFile.isEmpty()) {
            post.setPostImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }

        User currentUser = userMapper.toDomain(userService.getLoggedUser());
        post.setOwner(currentUser);
        currentUser.getPosts().add(post);

        List<Section> sections = post.getSections();
        for (Section section : sections) {
            section.addPost(post);
        }

        List<User> contributors = post.getContributors();
        for (User contributor : contributors) {
            contributor.addCollaboratedPosts(post);
        }
        postRepository.save(post);

    }

    public void save(PostDTO postDTO, MultipartFile imagFile) throws IOException {
        save(toDomain(postDTO), imagFile);

    }

    public void saveForInit(Post post) {
        postRepository.save(post);
    }

    public void saveOtherUsersPost(Post post, User user) {
        post.setOwner(user);
        post.setOwnerName(user.getUserName());
        postRepository.save(post);
    }

    public void deletePost(Post post) {
        // for (Comment comment : post.getComments()) {
        //     commentService.deleteCommentFromPost(post, comment.getId());
        // }

        // for (Section section : post.getSections()) {
        //     section.deletePost(post);
        // }
        post.getContributors().clear();
        post.getSections().clear();
        postRepository.deleteById(post.getId());
        // post.getComments().clear();
    }

    public void deletePost(PostDTO postDTO) {
        deletePost(toDomain(postDTO));
    }

    public void updatePost(Post post, String newTitle, String newContent, List<Long> newSectionIds, String[] newContributorsStrings, MultipartFile newImage) throws IOException {
        post.setTitle(newTitle);
        post.setContent(newContent);

        List<Section> newSections = sectionService.getSectionsFromIdsList(newSectionIds);
        post.getSections().clear();
        System.out.println("==========================\n\n");
        for(Section section : newSections) {
            System.out.println(section.getId());
        }
        System.out.println("==========================\n\n");

        // post.setSections(newSections);
        addSections(post, newSectionIds);

        List<User> newContributors = userService.getUsersFromUserNamesList(newContributorsStrings);
        post.setContributors(newContributors);

        if (!newImage.isEmpty()) {
            post.setPostImage(BlobProxy.generateProxy(newImage.getInputStream(), newImage.getSize()));
        }

        postRepository.save(post);
    }


    public CommentService getCommentService() {
        return this.commentService;
    }

    public void setAverageRatingPost(long postId) {
        Post post = postRepository.findById(postId).get();
        if (!post.getComments().isEmpty()) {
                post.setAverageRating(postRepository.findAverageRatingByPostId(postId));
                postRepository.save(post);
        } else {
            post.setAverageRating(0);
            postRepository.save(post);
        }
    }  
    
    public void addSections(Post post, List<Long> sectionIds) {
        if (sectionIds != null) {
            for (long sectionId : sectionIds) {
                post.addSection(sectionService.findById(sectionId).get());
            }
        }
    }

    public void addContributors(Post post, String[] contributorNames) {
        UserDTO user;
        for (String colaborator : contributorNames) {
            user = userService.findByUserName(colaborator);
            if (user != null) {
                post.addContributor(user);
            }
        }
    }

    public void updateSections(Post post, List<Section> oldSections, List<Section> newSections) {
        for (Section section : newSections) {
            if (!post.getSections().contains(section)) {
                post.addSection(section);
                section.addPost(post);
            }
        }
    }   

    private PostDTO toDTO(Post post) {
        return postMapper.toDTO(post);
    }

    private Post toDomain(PostDTO postDTO) {
        return postMapper.toDomain(postDTO);
    }

    private Collection<PostDTO> toDTOs(Collection<Post> posts) {
        return postMapper.toDTOs(posts);
    }

}
