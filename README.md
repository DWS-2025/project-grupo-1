# HackersAnónimos.com

## Integrantes del equipo de desarrollo
| Integrante               | Correo electrónico                 | GitHub Nickname |
|--------------------------|------------------------------------|-----------------|
| Alejandro Damas Pablos   | a.damasp.2023@alumnos.urjc.es      | aDamasPab       |
| Eva Fernández López      | e.fernandel.2023@alumnos.urjc.es   | EvaFdezLpz      |
| Markel Fernández Pinilla | m.fernandezpi.2024@alumnos.urjc.es | markel-f        |
| Julio López de Lucas     | j.lopezd.2023@alumnos.urjc.es      | Julioolpz       |

## Aspectos Principales

### Entidades
- **<u>Post</u>**: entidad que representa los post que créan los usuarios dentro de la web.
- **<u>Comment</u>**: pequeño texto para dar opinión sobre un post en concreto.
- **<u>User</u>**: entidad principal que se encarga de manipular los posts y comentarios.
- **<u>Section</u>**: categorías de los posts.

### Persmisos de los usuarios

- **<u>Admin</u>**: este tendrá control absoluto sobre cualquier entidad. Todo es de su propiedad.

- **<u>User</u>**: podrá crear posts y comentarios, además de poder editar y eliminar todos aquellos que se encuentren bajo su propiedad, es decir, posts y comentarios que haya creado ese mismo usuario. Por otro lado, podrá seguir a otros usuarios e incluso secciones.

### Imágenes
Las entidades con imágenes serán los **Post**, los **User** (foto de perfil) y las **Section** (foto representativa de la sección). Todas estas poseeran máximo una imagen.

### Esquema de la Base de Datos

#### Diagrama 
<img src="Diagrama MySQL.svg" alt="Diagrama de la base de datos generado por MySQL" style="background-color:white; display:block; margin:auto; max-width: 800pxs">

#### Esquema E/R
<img src="Esquema ER.svg" alt="Esquema E/R de las entidades" style="background-color:white; display:block; margin:auto; max-width: 800px">

## Desarrollo Colaborativo

### Alejandro Damas Pablos
Encargado principalmente de la clase Post, implementando cada aspecto referente a esta. Además, ha ido manipulando el resto de entidades en mayor o menor medida debido a la conexión de la entidad Post con todas las demás.

#### 5 commits más significativos
- 
- 
- 
- 
- 

#### 5 ficheros en los que más ha participado
- PostController
- 
- 
- 
- 

### Eva Fernández López
Encargada principalmente de la clase Section, implementando cada aspecto referente a esta. Además, ha ido aportando al resto de clases.

#### 5 commits más significativos
- [a2e647e](https://github.com/DWS-2025/project-grupo-1/commit/a2e647e484bcc8488691687a1e7ed37b3ab54007): Todas las entradas de los formularios sanitizadas.
- [b451183](https://github.com/DWS-2025/project-grupo-1/commit/b4511839b35c825199d79a65aed4a9c990bfd35d): Añadido el panel de administrador para que pueda ver todos los usuarios, solo el administrador podrá acceder a esta página.
- [ce467a1](https://github.com/DWS-2025/project-grupo-1/commit/ce467a10b8f21ef96a40e4fe3f083c64ce69dd10): Implementación de JWT y Configuración Detallada de Roles en Spring Security. 
- [0c12300](https://github.com/DWS-2025/project-grupo-1/commit/0c1230081c4b10d19d47b58353dc487fe2f3033b): Implementación de texto enriquecido y securizado en el frontend.
- [78ac530](https://github.com/DWS-2025/project-grupo-1/commit/78ac530723c9456de044ef6581cabc0e0d14234a): Implementación de la consulta dinámica.

#### 5 ficheros en los que más ha participado
- SectionService.java
- SectionRestController.java
- SectionController.java
- SectionRepository.java
- SecurityConfiguration.java

### Markel Fernández Pinilla
Encargado principalmente de la clase Comment, implementando cada aspecto referente a esta. Además, ha ido aportando al resto de clases.

#### 5 commits más significativos
- 
- 
- 
- 
- 

#### 5 ficheros en los que más ha participado
- 
- 
- 
- 
- 

### Julio López de Lucas
Encargado principalmente de la clase User, debida a su alta cantidad de funcionalidad, desarrollada junto con Markel. 
También ha aportado en cuestiones de seguridad, como control de acceso, content security policy y control de entradas del usuario.

#### 5 commits más significativos
- 
- 
- 
- 
- 

#### 5 ficheros en los que más ha participado
- UserController
- UserRestController
- UserService  
- SecurityConfiguration
- Profile.html