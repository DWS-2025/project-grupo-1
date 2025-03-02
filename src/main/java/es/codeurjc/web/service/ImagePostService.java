package es.codeurjc.web.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagePostService {
    
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");

    private Path createImagePath(long imageId, Path folder) {
        return folder.resolve("image-" + imageId + ".jpeg");
    }

    public void saveImage(String folderName, long imageId, MultipartFile image) throws IOException {
        Path folder = IMAGES_FOLDER.resolve(folderName);
        Files.createDirectories(folder);
        Path newImage = createImagePath(imageId, folder);
        image.transferTo(newImage);
    }

    public ResponseEntity<Object> createResponseFromImage(String folderName, long imageId) throws MalformedURLException {
        Path folder = IMAGES_FOLDER.resolve(folderName);
        Path imagePath = createImagePath(imageId, folder);
        UrlResource file = new UrlResource(imagePath.toUri());
        
        if(!Files.exists(imagePath)) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(file);
        }
    }

    public void deleteImage(String folderName, long imageId) throws IOException {
        Path folder = IMAGES_FOLDER.resolve(folderName);
        Path imagePath = createImagePath(imageId, folder);
        Files.deleteIfExists(imagePath);
    }

}
