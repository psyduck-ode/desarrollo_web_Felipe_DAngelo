const avisoId = document.getElementById('comentarios-section').getAttribute('data-aviso-id');

document.addEventListener('DOMContentLoaded', () => {
    cargarComentarios(avisoId);
    document.getElementById('btn-agregar-comentario').addEventListener('click', agregarComentario);
});

function cargarComentarios(avisoId) {
    fetch(`/api/avisos/${avisoId}/comentarios`)
        .then(response => {
            if (!response.ok) throw new Error('Error al cargar comentarios');
            return response.json();
        })
        .then(comentarios => mostrarComentarios(comentarios))
        .catch(error => {
            const lista = document.getElementById('comentarios-lista');
            if (lista) lista.innerHTML = '<p style="color: red;">Error al cargar comentarios</p>';
        });
}

function mostrarComentarios(comentarios) {
    const lista = document.getElementById('comentarios-lista');
    if (!lista) return;
    
    if (comentarios.length === 0) {
        lista.innerHTML = '<p>No hay comentarios aún.</p>';
        return;
    }
    
    let html = '<ul style="list-style: none; padding: 0;">';
    comentarios.forEach(c => {
        html += `
            <li style="border-bottom: 1px solid #ccc; padding: 10px 0;">
                <strong>${c.nombre}</strong> 
                <span style="color: #666;">(${c.fecha})</span>
                <p>${c.texto}</p>
            </li>
        `;
    });
    html += '</ul>';
    lista.innerHTML = html;
}

async function agregarComentario() {
    const nombre = document.getElementById('comentario-nombre').value.trim();
    const texto = document.getElementById('comentario-texto').value.trim();
    const errores = [];
    
    if (!nombre || nombre.length < 3 || nombre.length > 80) errores.push("El nombre debe tener entre 3 y 80 caracteres.");
    if (!texto || texto.length < 5 || texto.length > 300) errores.push("El comentario debe tener entre 5 y 300 caracteres.");
    if (errores.length > 0) {
        mostrarErrores(errores);
        return;
    }
    
    ocultarErrores();
    
    fetch(`/api/avisos/${avisoId}/comentarios`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ nombre, texto })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                const mensajes = [];
                if (data.errores) for (const key in data.errores) mensajes.push(data.errores[key]);
                throw new Error(mensajes.join('\n') || 'Error al agregar comentario');
            });
        }
        return response.json();
    })
    .then(data => {
        document.getElementById('comentario-form').reset();
        cargarComentarios(avisoId);
        const mensaje = document.createElement('div');
        mensaje.style.backgroundColor = '#ddffdd';
        mensaje.style.padding = '10px';
        mensaje.style.margin = '10px 0';
        mensaje.style.borderLeft = '5px solid #4CAF50';
        mensaje.textContent = '¡Comentario agregado exitosamente!';
        const form = document.getElementById('comentario-form');
        form.parentNode.insertBefore(mensaje, form);
        setTimeout(() => mensaje.remove(), 3000);
    })
    .catch(error => mostrarErrores([error.message]));
}

function mostrarErrores(errores) {
    const divErrores = document.getElementById('comentario-errores');
    const listaErrores = document.getElementById('comentario-errores-lista');
    if (!divErrores || !listaErrores) return;
    listaErrores.innerHTML = '';
    errores.forEach(e => {
        const li = document.createElement('li');
        li.textContent = e;
        listaErrores.appendChild(li);
    });
    divErrores.style.display = 'block';
}

function ocultarErrores() {
    const divErrores = document.getElementById('comentario-errores');
    if (divErrores) divErrores.style.display = 'none';
}
