document.addEventListener('DOMContentLoaded', function () {
    function setupLoadMore(buttonId, listId, apiUrl) {
        const loadMoreButton = document.getElementById(buttonId); // Get the "Load More" button by its ID
        const spinner = document.getElementById('loading-spinner'); // Get the spinner element for loading indication

        if (loadMoreButton) {
            loadMoreButton.addEventListener('click', function () {
                console.log('Load More button clicked'); // Debugging log
                const page = parseInt(loadMoreButton.getAttribute('data-page')); // Get the current page number
                console.log('Current page:', page); // Debugging log

                spinner.style.display = 'block'; // Show the spinner while loading

                // Perform the AJAX request
                fetch(`${apiUrl}?page=${page}`)
                    .then(response => {
                        console.log('Response status:', response.status); // Debugging log
                        return response.json();
                    })
                    .then(data => {
                        console.log('Data received:', data); // Debugging log
                        const list = document.getElementById(listId); // Get the list container by its ID

                        // Add the new items to the list
                        data.content.forEach(item => {
                            const sectionHTML = `
                                <div class="col-lg-4 col-md-6 col-sm-12">
                                    <div class="item">
                                        <div class="thumb">
                                            <img src="/section/${item.id}/image" alt="" class="section-image">
                                            <div class="hover-effect">
                                                <ul>
                                                    <li>
                                                        <a href="/section/${item.id}">Ver sección</a>
                                                    </li>
                                                    <li>
                                                        <a>
                                                            <form action="/section/${item.id}/delete" method="post" style="display:inline;">
                                                                <button type="submit"
                                                                    style="background-color: palevioletred; color: palevioletred; border: none; color: white">Borrar
                                                                    sección</button>
                                                            </form>
                                                        </a>
                                                    </li>
                                                    <li>
                                                        <a href="/section/${item.id}/edit">Editar</a>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>
                                        <div class="down-content">
                                            <h4 style="text-align: center;">${item.title}</h4>
                                            <p style="text-align: center;">${item.description}</p>
                                        </div>
                                    </div>
                                </div>
                            `;
                            list.insertAdjacentHTML('beforeend', sectionHTML); // Append the new section to the list
                        });

                        // Update the page number for the next request
                        loadMoreButton.setAttribute('data-page', page + 1);

                        // Hide the "Load More" button if there are no more results
                        if (data.last) {
                            loadMoreButton.style.display = 'none';
                        }
                    })
                    .catch(error => console.error('Error loading items:', error)) // Log any errors
                    .finally(() => {
                        spinner.style.display = 'none'; // Hide the spinner after loading
                    });
            });
        } else {
            console.error(`Button with ID "${buttonId}" not found`); // Debugging log
        }
    }

    // Set up the "Load More" button for sections
    setupLoadMore('load-more-sections', 'sections-list', '/api/sections');
});