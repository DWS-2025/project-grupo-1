document.addEventListener('DOMContentLoaded', function () {
    function setupLoadMore(buttonId, listId, apiUrl) {
        const loadMoreButton = document.getElementById(buttonId);
        const spinner = document.getElementById('loading-spinner');

        if (loadMoreButton) {
            loadMoreButton.addEventListener('click', function () {
                const page = parseInt(loadMoreButton.getAttribute('data-page')); // Página actual

                spinner.style.display = 'block'; // Mostrar el spinner

                // Realizar la solicitud AJAX
                fetch(`${apiUrl}?page=${page}`)
                    .then(response => response.json())
                    .then(data => {
                        const list = document.getElementById(listId);

                        // Agregar los nuevos elementos al contenedor
                        data.content.forEach(item => {
                            const li = document.createElement('li');
                            li.innerHTML = `<p>${item.content || item.name || item.title}</p>`;
                            list.appendChild(li);
                        });

                        // Actualizar el número de página
                        loadMoreButton.setAttribute('data-page', page + 1);

                        // Ocultar el botón si no hay más resultados
                        if (data.last) {
                            loadMoreButton.style.display = 'none';
                        }
                    })
                    .catch(error => console.error('Error al cargar los elementos:', error))
                    .finally(() => {
                        spinner.style.display = 'none'; // Ocultar el spinner
                    });
            });
        }
    }
// APIs URLs NEED TO BE CHANGED
  
    setupLoadMore('load-more-comments', 'comments-list', '/api/comments/post/1');

    setupLoadMore('load-more-posts', 'posts-list', '/api/posts');

    setupLoadMore('load-more-sections', 'sections-list', '/api/sections');

    setupLoadMore('load-more-followers', 'followers-list', '/api/users/followers');
});