package es.codeurjc.web.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<PostDTO> findAllAsDTO(Pageable pageable) {
        return postRepository.findAll(pageable).map(this::toDTO);
    }

    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }

    public Optional<PostDTO> findByIdDTO(long id) {
        return postRepository.findById(id).map(this::toDTO);
    }

    public Post save(Post post, MultipartFile imageFile) throws IOException { // Swapped from Post to void

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

        return post;

    }

    public PostDTO save(PostDTO postDTO, MultipartFile imagFile) throws IOException {
        return toDTO(save(toDomain(postDTO), imagFile));

    }

    public Post save(Post post) { // Swapped from Post to void

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

    public Post updatePost(Post oldPost, String newTitle, String newContent, List<Long> newSectionIds, String[] newContributorsStrings, MultipartFile newImage) throws IOException {
        oldPost.setTitle(newTitle);
        oldPost.setContent(newContent);

        
        oldPost.getSections().clear();

        oldPost.setSections(new ArrayList<>(sectionService.getSectionsFromIdsList(newSectionIds)));
        addSections(oldPost, newSectionIds);


        oldPost.setContributors(new ArrayList<>(userService.getUsersFromUserNamesList(newContributorsStrings)));

        if (!newImage.isEmpty()) {
            oldPost.setPostImage(BlobProxy.generateProxy(newImage.getInputStream(), newImage.getSize()));
        }

        postRepository.save(oldPost);

        return oldPost;
    }

    public Post updatePost(Post oldPost, Post newPost, List<Long> newSectionIds, String[] newContributorsStrings, MultipartFile newImage) throws IOException {
        oldPost.setTitle(newPost.getTitle());
        oldPost.setContent(newPost.getContent());

        oldPost.setSections(new ArrayList<>(sectionService.getSectionsFromIdsList(newSectionIds)));
        oldPost.setContributors(new ArrayList<>(userService.getUsersFromUserNamesList(newContributorsStrings)));

        if (!newImage.isEmpty()) {
            oldPost.setPostImage(BlobProxy.generateProxy(newImage.getInputStream(), newImage.getSize()));
        }

        postRepository.save(oldPost);

        return oldPost;
    }

    public PostDTO updatePost(PostDTO oldPost, PostDTO newPost, List<Long> newSectionIds, String[] newContributorsStrings, MultipartFile newImage) throws IOException {
        return toDTO(updatePost(toDomain(oldPost), toDomain(newPost), newSectionIds, newContributorsStrings, newImage));
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
        if (!sectionIds.isEmpty()) {
            for (long sectionId : sectionIds) {
                post.addSection(sectionService.toDomain(sectionService.findById(sectionId).get()));
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

    public PostDTO replacePost(long id, PostDTO updatedPostDTO) throws SQLException {

		Post oldPost = postRepository.findById(id).orElseThrow();
		Post updatedPost = toDomain(updatedPostDTO);
		updatedPost.setId(id);

		if (oldPost.getImage() != null) {

			//Set the image in the updated post
			updatedPost.setPostImage(BlobProxy.generateProxy(oldPost.getPostImage().getBinaryStream(), oldPost.getPostImage().length()));
			updatedPost.setImage(oldPost.getImage());
            
		}

		postRepository.save(updatedPost);

		return toDTO(updatedPost);
	}
    
    public void createPostImage(long id, URI location, InputStream inputStream, long size) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setImage(location.toString());
        post.setPostImage(BlobProxy.generateProxy(inputStream, size));
    
        postRepository.save(post);
    }

    public Resource getPostImage(long id) throws SQLException {

		Post post = postRepository.findById(id).orElseThrow();

		if (post.getPostImage() != null) {
			return new InputStreamResource(post.getPostImage().getBinaryStream());
		} else {
			throw new NoSuchElementException();
		}
	}

    public void replacePostImage(long id, InputStream inputStream, long size) {

		Post post = postRepository.findById(id).orElseThrow();

		if(post.getImage() == null){
			throw new NoSuchElementException();
		}

		post.setPostImage(BlobProxy.generateProxy(inputStream, size));

		postRepository.save(post);
	}

	public void deletePostImage(long id) {

		Post post = postRepository.findById(id).orElseThrow();

		if(post.getImage() == null){
			throw new NoSuchElementException();
		}

		post.setPostImage(null);
		post.setImage(null);

		postRepository.save(post);
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