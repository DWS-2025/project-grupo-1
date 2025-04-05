document.addEventListener('DOMContentLoaded', function () {
    const loadMoreButton = document.getElementById('load-more-comments');
    const spinner = document.getElementById('loading-spinner');

    if (loadMoreButton) {
        loadMoreButton.addEventListener('click', function () {
            const postId = loadMoreButton.getAttribute('data-post-id'); // ID del post
            const page = parseInt(loadMoreButton.getAttribute('data-page')); // Página actual

            spinner.style.display = 'block'; // Mostrar el spinner

            // Realizar la solicitud AJAX
            fetch(`/api/comments/post/${postId}?page=${page}`)
                .then(response => response.json())
                .then(data => {
                    const commentsList = document.getElementById('comments-list');

                    // Agregar los nuevos comentarios al contenedor
                    data.content.forEach(comment => {
                        const li = document.createElement('li');
                        li.innerHTML = `<p>${comment.content}</p><small>Rating: ${comment.rating}</small>`;
                        commentsList.appendChild(li);
                    });

                    // Actualizar el número de página
                    loadMoreButton.setAttribute('data-page', page + 1);

                    // Ocultar el botón si no hay más resultados
                    if (data.last) {
                        loadMoreButton.style.display = 'none';
                    }
                })
                .catch(error => console.error('Error al cargar los comentarios:', error))
                .finally(() => {
                    spinner.style.display = 'none'; // Ocultar el spinner
                });
        });
    }
});