document.addEventListener('DOMContentLoaded', () => {
    // Inicializar Quill
    const quill = new Quill('#editor', {
        theme: 'snow',
        placeholder: 'Escribe tu descripción aquí...',
        modules: {
            toolbar: [
                [{ 'header': [1, 2, false] }],
                ['bold', 'italic', 'underline'],
                ['clean']
            ]
        }
    });

    // Manejar el envío del formulario
    const form = document.querySelector('form');
    form.addEventListener('submit', function(e) {
        // Sincronizar el contenido de Quill al campo oculto
        const rawDescription = quill.root.innerHTML;
        const cleanedDescription = DOMPurify.sanitize(rawDescription);
        
        // Validar que haya contenido
        if (quill.getText().trim().length === 0) {
            e.preventDefault();
            alert('La descripción no puede estar vacía');
            return;
        }
        
        document.getElementById('description').value = cleanedDescription;
    });
});
