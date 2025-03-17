package es.codeurjc.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @PostConstruct
    public void init() {

        // Default user
        User mainUser = new User("mainUser", "1234", "mainUser@gmail.com");
        // Other users
        User user1 = new User("user1", "Contraseña1", "user1@gmail.com");
        User user2 = new User("user2", "Contraseña2", "user2@gmail.com");
        User user3 = new User("user3", "Contraseña3", "user3@gmail.com");
        User user4 = new User("user4", "Contraseña4", "user4@gmail.com");
        User user5 = new User("user5", "Contraseña5", "user5@gmail.com");
        User user6 = new User("user6", "Contraseña6", "user6@gmail.com");

        // Some examples of posts and comments
        //section reversing, hacking web, hardware hacking
        Post post1 = new Post("La Ingeniería Reversa como Puerta de Entrada al Hacking", "La ingeniería reversa es una de las habilidades más potentes en el mundo del hacking, permitiendo analizar y comprender cómo funciona un sistema para encontrar vulnerabilidades o mejorar su seguridad. En aplicaciones web, esto se usa para descubrir puntos débiles en algoritmos de autenticación o cifrado. A nivel de hardware, los atacantes pueden modificar firmware o extraer claves de cifrado de dispositivos embebidos. ¿Cómo se puede defender una empresa ante estos ataques? Aquí exploramos técnicas de análisis, herramientas como Ghidra y Radare2, y estrategias para mitigar amenazas en cada capa del sistema", "image-1.jpeg");
        //section Hacking Web, Escalada de Privilegios, Hardware Hacking
        Post post2 = new Post("Explotación de Vulnerabilidades en Aplicaciones Web y Sistemas Embebidos", "La seguridad de las aplicaciones web sigue siendo un punto crítico en la ciberseguridad moderna. Ataques como la inyección SQL, el XSS o la explotación de deserialización insegura pueden comprometer bases de datos enteras. Sin embargo, los sistemas embebidos tampoco están exentos de riesgos. Un atacante que explote una vulnerabilidad en el firmware de un dispositivo IoT puede obtener acceso privilegiado al sistema. En este post analizamos cómo se combinan técnicas de hacking web con ataques físicos a hardware para lograr escaladas de privilegios en entornos empresariales.", "image-2.jpeg");
        // section Escalada de Privilegios, Hacking Web, WiFi
        Post post3 = new Post("Métodos de Escalada de Privilegios en Windows y Linux", "No basta con obtener acceso a un sistema, el verdadero poder de un atacante radica en la escalada de privilegios. En este artículo exploramos cómo explotar configuraciones mal implementadas en sistemas Windows y Linux para elevar permisos, aprovechando vulnerabilidades en servicios, archivos SUID o claves mal protegidas en bases de datos. Además, vemos cómo ataques en redes WiFi pueden ser la puerta de entrada para obtener credenciales de administrador en redes corporativas.", "image-3.jpeg");
        // Hardware Hacking, Reversing, WiFi
        Post post4 = new Post("Hacking de Hardware: Modificación de Firmware y Puertas Traseras", "El hacking de hardware permite modificar dispositivos físicos para alterar su comportamiento o extraer información valiosa. En este post abordamos técnicas para modificar firmware en routers, dispositivos IoT y sistemas de control industrial. También exploramos cómo se pueden insertar puertas traseras en firmware legítimo para comprometer la seguridad de toda una red WiFi, permitiendo ataques MITM o la exfiltración de datos sin ser detectados.", "/assets/images/stream-04.jpg");
        // WiFi, Escalada de Privilegios, Hacking Web
        Post post5 = new Post("Red Team y Evaluación de Seguridad en Redes Inalámbricas", "En una evaluación de seguridad ofensiva, la explotación de redes WiFi es una de las primeras etapas del ataque. Desde la captura de handshakes hasta la manipulación de paquetes con herramientas como aircrack-ng y bettercap, los atacantes pueden obtener credenciales de acceso a redes corporativas. En este post analizamos cómo estos accesos pueden derivar en escaladas de privilegios en servidores web y bases de datos, comprometiendo sistemas críticos en entornos empresariales.", "/assets/images/stream-05.jpg");
		// Hardware Hacking, Reversing, Escalada de Privilegios
        Post post6 = new Post("Explotación de Dispositivos de Hardware en Entornos Industriales", "Muchas infraestructuras críticas dependen de dispositivos embebidos con firmware vulnerable. En este artículo exploramos cómo se pueden explotar fallos en sistemas industriales (ICS/SCADA), desde análisis de protocolos propietarios hasta la manipulación de dispositivos físicos mediante técnicas de reversing. La seguridad de estos entornos es fundamental, ya que un ataque exitoso podría comprometer sistemas eléctricos, plantas de tratamiento de agua o redes de transporte.", "/assets/images/stream-06.jpg");
		// Hacking Web, Reversing, WiFi
        Post post7 = new Post("Seguridad en Aplicaciones Web: Análisis de Ataques en Tiempo Real", "Las aplicaciones web son el blanco principal de ataques en la actualidad. En este artículo realizamos un análisis en tiempo real de técnicas como la inyección SQL, la manipulación de sesiones y la explotación de servidores mal configurados. También exploramos cómo ataques de red, como la interceptación de tráfico WiFi, pueden ser utilizados para obtener credenciales y comprometer aplicaciones web críticas.", "image-1.jpeg");
		// WiFi, Hacking Web, Escalada de Privilegios, Reversing
        Post post8 = new Post("Cómo Proteger tus Redes Contra Hackers Éticos y Maliciosos", "La seguridad de una red depende de múltiples factores, desde la fortaleza de sus contraseñas hasta la correcta segmentación del tráfico. En este post explicamos cómo se pueden prevenir ataques de escalada de privilegios en entornos empresariales, detectando intentos de inyección SQL en aplicaciones web y protegiendo redes WiFi contra ataques de fuerza bruta. Además, veremos cómo la ingeniería reversa puede ser utilizada para analizar malware y reforzar la seguridad de los sistemas.", "/assets/images/stream-08.jpg");

        //Create new sections
        Section defaultSection1 = new Section("Reversing", "Análisis y descompilación de binarios para entender su funcionamiento.", "image-1.jpeg");
        Section defaultSection2 = new Section("Hacking Web", "Explotación de vulnerabilidades en aplicaciones web.", "image-2.jpeg");
        Section defaultSection3 = new Section("Escalada de Privilegios", "Métodos para obtener acceso administrativo en Windows.", "image-3.jpeg");
        Section defaultSection4 = new Section("Hardware Hacking", "Explotación de vulnerabilidades a nivel de hardware.", "image-4.jpeg");
        Section defaultSection5 = new Section("WiFi", "Ataques y auditorías de seguridad en redes inalámbricas.", "image-5.jpeg");

        userService.save(mainUser);
        userService.save(user1);
        userService.save(user2);
        userService.save(user3);
        userService.save(user4);
        userService.save(user5);
        userService.save(user6);

        postService.saveOtherUsersPost(post1, mainUser);
        postService.saveOtherUsersPost(post2, mainUser);
        postService.saveOtherUsersPost(post3, user2);
        postService.saveOtherUsersPost(post4, user2);
        postService.saveOtherUsersPost(post5, user3);
        postService.saveOtherUsersPost(post6, user3);
        postService.saveOtherUsersPost(post7, user4);
        postService.saveOtherUsersPost(post8, user4);

        sectionService.saveSection(defaultSection1);
        sectionService.saveSection(defaultSection2);
        sectionService.saveSection(defaultSection3);
        sectionService.saveSection(defaultSection4);
        sectionService.saveSection(defaultSection5);

        defaultSection1.addPost(post1);
    
        defaultSection1.addPost(post2);

        defaultSection2.addPost(post3);

        defaultSection2.addPost(post4);
    
        defaultSection3.addPost(post5);

        defaultSection3.addPost(post6);

        defaultSection4.addPost(post7);

        defaultSection5.addPost(post8);

        sectionService.saveSection(defaultSection1);
        sectionService.saveSection(defaultSection2);
        sectionService.saveSection(defaultSection3);
        sectionService.saveSection(defaultSection4);
        sectionService.saveSection(defaultSection5);


        post1.addContributor(user6);
        post1.addContributor(user2);
        post1.addContributor(user4);
        post2.addContributor(user1);
        post3.addContributor(user6);
        post4.addContributor(user6);
        post5.addContributor(user6);
        post6.addContributor(user3);
        post6.addContributor(user2);

     //    this.followSectionAutomated();
      //  this.followUsersAutomated();

    }

    public void followSectionAutomated() {
        Random random = new Random();
        List<Section> sections = sectionService.findAll();
        for (int i = 1; i <= userService.findAllUsers().size(); i++) {
            if (userService.getUserById(i) != null) {
                int numberOfSectionsToFollow = random.nextInt(sections.size() - 1) + 1; // At least one section
                Set<Section> followedSections = new HashSet<>();
                for (int j = 0; j < numberOfSectionsToFollow; j++) {
                    Section sectionToFollow;
                    do {
                        sectionToFollow = sections.get(random.nextInt(sections.size()));
                    } while (followedSections.contains(sectionToFollow)); // Ensure a user does not follow the same section more than once
                    userService.getUserById(i).followSection(sectionToFollow);
                    followedSections.add(sectionToFollow);
                }
            }
        }
    }

    public void followUsersAutomated() {
        Random random = new Random();
        List<User> users = userService.findAllUsers();
        for (int i = 1; i <= userService.findAllUsers().size(); i++) {
            int numberOfUsersToFollow = random.nextInt(users.size() - 1) + 1;
            Set<User> followedUsers = new HashSet<>();

            for (int j = 0; j < numberOfUsersToFollow; j++) {
                User userToFollow;
                do {
                    userToFollow = users.get(random.nextInt(users.size()));
                } while (followedUsers.contains(userToFollow) || userService.getUserById(i).equals(userToFollow)); // Evitar seguir al mismo usuario

                userService.getUserById(i).follow(userToFollow);
                followedUsers.add(userToFollow);
            }
        }
    }
}
