package es.codeurjc.web.service;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.util.NoSuchElementException;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import jakarta.annotation.PostConstruct;

@Service
public class Manager {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        try {
            userService.findByUserName("Admin");
            return; 
        } catch (NoSuchElementException e) {
       

            // Default user 
            User mainUser = new User("Admin", passwordEncoder.encode("1234"), "Admin@gmail.com", "USER", "ADMIN");
            // Other users
            User user2 = new User("user2", passwordEncoder.encode("pass2"), "user2@gmail.com", "USER");
            User user3 = new User("user3", passwordEncoder.encode("pass3"), "user3@gmail.com", "USER");
            User user4 = new User("user4", passwordEncoder.encode("pass4"), "user4@gmail.com", "USER");
            User user5 = new User("user5", passwordEncoder.encode("pass5"), "user5@gmail.com", "USER");
            User user6 = new User("user6", passwordEncoder.encode("pass6"), "user6@gmail.com", "USER");
            User user7 = new User("user7", passwordEncoder.encode("pass7"), "user7@gmail.com", "USER");

            mainUser.setUserImage(localImageToBlob("images/users/image-1.jpeg"));
            user2.setUserImage(localImageToBlob("images/users/image-2.jpeg"));
            user3.setUserImage(localImageToBlob("images/users/image-3.jpeg"));
            user4.setUserImage(localImageToBlob("images/users/image-4.jpeg"));
            user5.setUserImage(localImageToBlob("images/users/image-5.jpeg"));
            user6.setUserImage(localImageToBlob("images/users/image-6.jpeg"));
            user7.setUserImage(localImageToBlob("images/users/image-7.jpeg"));

            mainUser.setImage("/api/users/1/image");
            user2.setImage("/api/users/2/image");
            user3.setImage("/api/users/3/image");
            user4.setImage("/api/users/4/image");
            user5.setImage("/api/users/5/image");
            user6.setImage("/api/users/6/image");
            user7.setImage("/api/users/7/image");

            // Some examples of posts and comments
            //section reversing, hacking web, hardware hacking
            Post post1 = new Post("La Ingeniería Reversa como Puerta de Entrada al Hacking", "La ingeniería reversa es una de las habilidades más potentes en el mundo del hacking, permitiendo analizar y comprender cómo funciona un sistema para encontrar vulnerabilidades o mejorar su seguridad. En aplicaciones web, esto se usa para descubrir puntos débiles en algoritmos de autenticación o cifrado. A nivel de hardware, los atacantes pueden modificar firmware o extraer claves de cifrado de dispositivos embebidos. ¿Cómo se puede defender una empresa ante estos ataques? Aquí exploramos técnicas de análisis, herramientas como Ghidra y Radare2, y estrategias para mitigar amenazas en cada capa del sistema");
            //section Hacking Web, Escalada de Privilegios, Hardware Hacking
            Post post2 = new Post("Explotación de Vulnerabilidades en Aplicaciones Web y Sistemas Embebidos", "La seguridad de las aplicaciones web sigue siendo un punto crítico en la ciberseguridad moderna. Ataques como la inyección SQL, el XSS o la explotación de deserialización insegura pueden comprometer bases de datos enteras. Sin embargo, los sistemas embebidos tampoco están exentos de riesgos. Un atacante que explote una vulnerabilidad en el firmware de un dispositivo IoT puede obtener acceso privilegiado al sistema. En este post analizamos cómo se combinan técnicas de hacking web con ataques físicos a hardware para lograr escaladas de privilegios en entornos empresariales.");
            // section Escalada de Privilegios, Hacking Web, WiFi
            Post post3 = new Post("Métodos de Escalada de Privilegios en Windows y Linux", "No basta con obtener acceso a un sistema, el verdadero poder de un atacante radica en la escalada de privilegios. En este artículo exploramos cómo explotar configuraciones mal implementadas en sistemas Windows y Linux para elevar permisos, aprovechando vulnerabilidades en servicios, archivos SUID o claves mal protegidas en bases de datos. Además, vemos cómo ataques en redes WiFi pueden ser la puerta de entrada para obtener credenciales de administrador en redes corporativas.");
            // Hardware Hacking, Reversing, WiFi
            Post post4 = new Post("Hacking de Hardware: Modificación de Firmware y Puertas Traseras", "El hacking de hardware permite modificar dispositivos físicos para alterar su comportamiento o extraer información valiosa. En este post abordamos técnicas para modificar firmware en routers, dispositivos IoT y sistemas de control industrial. También exploramos cómo se pueden insertar puertas traseras en firmware legítimo para comprometer la seguridad de toda una red WiFi, permitiendo ataques MITM o la exfiltración de datos sin ser detectados.");
            // WiFi, Escalada de Privilegios, Hacking Web
            Post post5 = new Post("Red Team y Evaluación de Seguridad en Redes Inalámbricas", "En una evaluación de seguridad ofensiva, la explotación de redes WiFi es una de las primeras etapas del ataque. Desde la captura de handshakes hasta la manipulación de paquetes con herramientas como aircrack-ng y bettercap, los atacantes pueden obtener credenciales de acceso a redes corporativas. En este post analizamos cómo estos accesos pueden derivar en escaladas de privilegios en servidores web y bases de datos, comprometiendo sistemas críticos en entornos empresariales.");
            // Hardware Hacking, Reversing, Escalada de Privilegios
            Post post6 = new Post("Explotación de Dispositivos de Hardware en Entornos Industriales", "Muchas infraestructuras críticas dependen de dispositivos embebidos con firmware vulnerable. En este artículo exploramos cómo se pueden explotar fallos en sistemas industriales (ICS/SCADA), desde análisis de protocolos propietarios hasta la manipulación de dispositivos físicos mediante técnicas de reversing. La seguridad de estos entornos es fundamental, ya que un ataque exitoso podría comprometer sistemas eléctricos, plantas de tratamiento de agua o redes de transporte.");
            Post post7 = new Post("Seguridad en Aplicaciones Web: Análisis de Ataques en Tiempo Real", "Las aplicaciones web son el blanco principal de ataques en la actualidad. En este artículo realizamos un análisis en tiempo real de técnicas como la inyección SQL, la manipulación de sesiones y la explotación de servidores mal configurados. También exploramos cómo ataques de red, como la interceptación de tráfico WiFi, pueden ser utilizados para obtener credenciales y comprometer aplicaciones web críticas.");
            // WiFi, Hacking Web, Escalada de Privilegios, Reversing
            Post post8 = new Post("Cómo Proteger tus Redes Contra Hackers Éticos y Maliciosos", "La seguridad de una red depende de múltiples factores, desde la fortaleza de sus contraseñas hasta la correcta segmentación del tráfico. En este post explicamos cómo se pueden prevenir ataques de escalada de privilegios en entornos empresariales, detectando intentos de inyección SQL en aplicaciones web y protegiendo redes WiFi contra ataques de fuerza bruta. Además, veremos cómo la ingeniería reversa puede ser utilizada para analizar malware y reforzar la seguridad de los sistemas.");
// Post sobre análisis de malware
            Post post9 = new Post("Análisis de Malware: Técnicas y Herramientas para Detectar Amenazas",
                    "El análisis de malware es una disciplina esencial en la ciberseguridad moderna. En este artículo exploramos cómo identificar y desarmar amenazas utilizando herramientas como IDA Pro, Ghidra y Cuckoo Sandbox. También discutimos cómo los atacantes utilizan técnicas de ofuscación para evadir detección y cómo los analistas pueden contrarrestarlas.");

            //Create new sections
            Section defaultSection1 = new Section("Reversing", "Análisis y descompilación de binarios para entender su funcionamiento.");
            Section defaultSection2 = new Section("Hacking Web", "Explotación de vulnerabilidades en aplicaciones web.");
            Section defaultSection3 = new Section("Escalada de Privilegios", "Métodos para obtener acceso administrativo en Windows.");
            Section defaultSection4 = new Section("Hardware Hacking", "Explotación de vulnerabilidades a nivel de hardware.");
            Section defaultSection5 = new Section("WiFi", "Ataques y auditorías de seguridad en redes inalámbricas.");
            Section defaultSection6 = new Section("Criptografía", "Estudio y aplicación de técnicas para proteger la información mediante cifrado.");
            Section defaultSection7 = new Section("Pentesting", "Evaluación de seguridad en sistemas y redes mediante pruebas de intrusión.");
            Section defaultSection8 = new Section("Seguridad en IoT", "Protección de dispositivos conectados y redes en el Internet de las Cosas.");
            Section defaultSection9 = new Section("Análisis de Malware", "Identificación y desarme de amenazas mediante técnicas de análisis.");
            Section defaultSection10 = new Section("Seguridad en Redes", "Configuración de firewalls y detección de intrusos para proteger redes.");
            Section defaultSection11 = new Section("Ransomware", "Estudio de ataques de cifrado de datos y estrategias de prevención.");

            defaultSection1.setImageFile(localImageToBlob("images/sections/image-1.jpeg"));
            defaultSection2.setImageFile(localImageToBlob("images/sections/image-2.jpeg"));
            defaultSection3.setImageFile(localImageToBlob("images/sections/image-3.jpeg"));
            defaultSection4.setImageFile(localImageToBlob("images/sections/image-4.jpeg"));
            defaultSection5.setImageFile(localImageToBlob("images/sections/image-5.jpeg"));
            defaultSection6.setImageFile(localImageToBlob("images/sections/image-6.jpeg"));
            defaultSection7.setImageFile(localImageToBlob("images/sections/image-7.jpeg"));
            defaultSection8.setImageFile(localImageToBlob("images/sections/image-8.jpeg"));
            defaultSection9.setImageFile(localImageToBlob("images/sections/image-9.jpeg"));
            defaultSection10.setImageFile(localImageToBlob("images/sections/image-10.jpeg"));
            defaultSection11.setImageFile(localImageToBlob("images/sections/image-11.jpeg"));

            defaultSection1.setImage("/api/sections/1/image");
            defaultSection2.setImage("/api/sections/2/image");
            defaultSection3.setImage("/api/sections/3/image");
            defaultSection4.setImage("/api/sections/4/image");
            defaultSection5.setImage("/api/sections/5/image");
            defaultSection6.setImage("/api/sections/6/image");
            defaultSection7.setImage("/api/sections/7/image");
            defaultSection8.setImage("/api/sections/8/image");
            defaultSection9.setImage("/api/sections/9/image");
            defaultSection10.setImage("/api/sections/10/image");
            defaultSection11.setImage("/api/sections/11/image");

            defaultSection1.setAverageRating(10);
            defaultSection2.setAverageRating(9);
            defaultSection3.setAverageRating(2);
            defaultSection4.setAverageRating(7);
            defaultSection5.setAverageRating(20);
            defaultSection6.setAverageRating(5);
            defaultSection7.setAverageRating(4);
            defaultSection8.setAverageRating(3);
            defaultSection9.setAverageRating(2);
            defaultSection10.setAverageRating(1);
            defaultSection11.setAverageRating(0);

            userService.save(mainUser);
            userService.save(user2);
            userService.save(user3);
            userService.save(user4);
            userService.save(user5);
            userService.save(user6);
            userService.save(user7);

            post1.setImageFile(localImageToBlob("images/posts/image-0.jpeg"));
            post2.setImageFile(localImageToBlob("images/posts/image-1.jpeg"));
            post3.setImageFile(localImageToBlob("images/posts/image-2.jpeg"));
            post4.setImageFile(localImageToBlob("images/posts/image-3.jpeg"));
            post5.setImageFile(localImageToBlob("images/posts/image-4.jpeg"));
            post6.setImageFile(localImageToBlob("images/posts/image-5.jpeg"));
            post7.setImageFile(localImageToBlob("images/posts/image-6.jpeg"));
            post8.setImageFile(localImageToBlob("images/posts/image-7.jpeg"));
            post9.setImageFile(localImageToBlob("images/posts/image-8.jpeg"));

            post1.setImage("/api/posts/1/image");
            post2.setImage("/api/posts/2/image");
            post3.setImage("/api/posts/3/image");
            post4.setImage("/api/posts/4/image");
            post5.setImage("/api/posts/5/image");
            post6.setImage("/api/posts/6/image");
            post7.setImage("/api/posts/7/image");
            post8.setImage("/api/posts/8/image");
            post9.setImage("/api/posts/9/image");

            postService.saveOtherUsersPost(post1, mainUser);
            postService.saveOtherUsersPost(post2, mainUser);
            postService.saveOtherUsersPost(post3, user2);
            postService.saveOtherUsersPost(post4, user2);
            postService.saveOtherUsersPost(post5, user3);
            postService.saveOtherUsersPost(post6, user3);
            postService.saveOtherUsersPost(post7, user4);
            postService.saveOtherUsersPost(post8, user4);
            postService.saveOtherUsersPost(post9, user5);

            sectionService.saveSection(defaultSection1);
            sectionService.saveSection(defaultSection2);
            sectionService.saveSection(defaultSection3);
            sectionService.saveSection(defaultSection4);
            sectionService.saveSection(defaultSection5);
            sectionService.saveSection(defaultSection6);
            sectionService.saveSection(defaultSection7);
            sectionService.saveSection(defaultSection8);
            sectionService.saveSection(defaultSection9);
            sectionService.saveSection(defaultSection10);
            sectionService.saveSection(defaultSection11);

            defaultSection1.addPost(post1);

            defaultSection1.addPost(post2);

            defaultSection2.addPost(post3);

            defaultSection2.addPost(post4);

            defaultSection3.addPost(post5);

            defaultSection3.addPost(post6);

            defaultSection4.addPost(post7);

            defaultSection5.addPost(post8);

            defaultSection6.addPost(post9);

            sectionService.saveSection(defaultSection1);
            sectionService.saveSection(defaultSection2);
            sectionService.saveSection(defaultSection3);
            sectionService.saveSection(defaultSection4);
            sectionService.saveSection(defaultSection5);
            sectionService.saveSection(defaultSection6);

            // Follow users
            mainUser.follow(user2);
            mainUser.follow(user3);
            mainUser.follow(user4);
            mainUser.followSection(defaultSection5);
            mainUser.followSection(defaultSection4);
            mainUser.followSection(defaultSection3);

            user2.follow(user3);
            user2.follow(user4);
            user2.follow(user5);
            user2.followSection(defaultSection1);
            user2.followSection(defaultSection2);
            user2.followSection(defaultSection3);

            user3.follow(user4);
            user3.follow(user5);
            user3.follow(user6);
            user3.followSection(defaultSection5);
            user3.followSection(defaultSection1);
            user3.followSection(defaultSection3);

            user4.follow(user5);
            user4.follow(mainUser);
            user4.follow(user7);
            user4.followSection(defaultSection4);
            user4.followSection(defaultSection5);
            user4.followSection(defaultSection1);

            user5.follow(mainUser);
            user5.follow(user6);
            user5.follow(user7);
            user5.followSection(defaultSection2);
            user5.followSection(defaultSection3);
            user5.followSection(defaultSection4);

            userService.save(mainUser);
            userService.save(user4);
            userService.save(user3);
            userService.save(user2);
            userService.save(user5);
            userService.save(user6);
            userService.save(user7);

            post1.addContributor(user6);

            post1.addContributor(user2);

            post1.addContributor(user4);
            postService.saveForInit(post1);

            post2.addContributor(user7);
            postService.saveForInit(post2);

            post3.addContributor(user6);
            postService.saveForInit(post3);

            post4.addContributor(user6);
            postService.saveForInit(post4);

            post5.addContributor(user6);
            postService.saveForInit(post5);

            post6.addContributor(user3);
            post6.addContributor(user2);
            post6.addContributor(mainUser);
            postService.saveForInit(post6);
        }
    }

    public Blob localImageToBlob(String localFilePath) {
        File imageFile = new File(localFilePath);

        if (imageFile.exists()) {
            try {
                return BlobProxy.generateProxy(imageFile.toURI().toURL().openStream(), imageFile.length());

            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error at processing the image");
            }
        }
        return null;
    }
}
