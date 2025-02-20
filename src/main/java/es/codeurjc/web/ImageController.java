package es.codeurjc.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


// Falta mucho por implementar, quizas sea mejor quitar las fotos de los posts y que la foto de los posts sea la foto de perfil del usuario que lo ha subido.
@Controller
// Pensado para que las imagenes se guarden asi -> images/users/username/profile.jpg, images/users/username/post1.jpg, etc.
public class ImageController {
    @Autowired
    // This is the manager that contains all the information of the application. With @Autowired we are telling Spring to inject the manager here, and it creates only one instance of the manager.
    private Manager manager;

    // The directory where the images are stored.
    private static final Path BASE_FOLDER = Paths.get(System.getProperty("user.dir"), "images");
    // The directory where the user images are stored. (Post image, profile image, etc.)
    private static final Path USERS_FOLDER = BASE_FOLDER.resolve("users");

    // This method is called when the user wants to upload an image. The image is stored in the BASE_FOLDER/USERS_FOLDER directory. NO TERMINADO
    @PostMapping("/upload")
    public String uploadUserImage(
            @RequestParam String imageName,
            @RequestParam MultipartFile image,
            Model model) throws IOException {

        Path userFolder = USERS_FOLDER.resolve(manager.getMainUser().getName());
        // Create the user folder if it does not exist.
        Files.createDirectories(userFolder); 

        Path imagePath = userFolder.resolve(imageName);
        image.transferTo(imagePath);

        model.addAttribute("imageName", imageName);
        model.addAttribute("username", manager.getMainUser().getName());

        return "uploaded_image";
    }

  

}
