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
- [6b6c324](https://github.com/DWS-2025/project-grupo-1/commit/6b6c3240326a7ff946ae521ac0a7ae7a67f06393): se pasa a trabajar con DTOs en PostController y se implementan diversas funcionalidades.
- [1022180](https://github.com/DWS-2025/project-grupo-1/commit/1022180acca0da6b279753f7569b58c67ce55879): se implementan controles de acceso para los diferentes endpoints relacionados con la entidad Post.
- [c1dd118](https://github.com/DWS-2025/project-grupo-1/commit/c1dd1184a0e4ada0bf6729e47d54e80c89771179): se implementan controles de acceso para la API y se modifica el comportamiento de algunas plantillas HTML.
- [28dbd6b](https://github.com/DWS-2025/project-grupo-1/commit/28dbd6b50b4438b5af016ff4e7c2432e352ec82b): borrar un post suponía la aniquilación de las secciones a las que perteneciese.
- [4abd873](https://github.com/DWS-2025/project-grupo-1/commit/4abd8730ee5409c113fdc6a59ed6d53dbd576dab): corrección de errores que provocaban fallos a la hora de visualizar las imágenes de los posts.

#### 5 ficheros en los que más ha participado
- PostService.java
- PostController.java
- PostRestController.java
- PostRepository.java
- SectionService.java

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
Encargado principalmente de la clase Comment, implementando cada aspecto referente a esta como a las entidades relacionadas. Además, ha ido aportando al resto de clases.

#### 5 commits más significativos
- [6802ca47] (https://github.com/DWS-2025/project-grupo-1/commit/6802ca47c5e80803acd4a22095f4bcbc2e92e0cd): Implementada el control de acceso para los comentarios
- [e9184a36] (https://github.com/DWS-2025/project-grupo-1/commit/e9184a36ef6f2b2ba3de3347e45def28f7ee6ca9): Implementada la funcionalidad de texto enriquecido
- [e5540c9] (https://github.com/DWS-2025/project-grupo-1/commit/e5540c923d7f6603b0185426d5a91001098aa8e8): Implementado el inicio de sesión de la aplicación
- [7a53165](https://github.com/DWS-2025/project-grupo-1/commit/7a53165b47cb4a7660fb11a6439ab3605322fe62): Implementada la subida de archivos
- [2efbb25] (https://github.com/DWS-2025/project-grupo-1/commit/2efbb25469aa3212d95e1a769e18819d8aeecb5d): Implementación del header dinámico para la aplicación

#### 5 ficheros en los que más ha participado
- commentService.java
- userService.java
- postService.java
- SecurityConfiguration.java
- PostRestControler.java

### Julio López de Lucas
Encargado principalmente de la clase User, implementando cada aspecto referente a esta. Además, ha ido aportando al resto de clases.

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