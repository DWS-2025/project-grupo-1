package es.codeurjc.web.restController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.web.dto.PostMapper;
import es.codeurjc.web.service.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/post")
public class PostRestController {
    
    @Autowired
    private PostService postService;

    @Autowired
    private PostMapper postMapper;

    @GetMapping("/")
    public Collection<PostDTO> getAllPosts() {
        return toDTOs(postService.findAll());
    }
    

}
