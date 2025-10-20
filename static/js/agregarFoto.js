let contadorFotos = 1;

function agregarFoto() {
    if (contadorFotos >= 5) {
        alert('Máximo 5 fotos permitidas');
        return;
    }
    
    contadorFotos++;
    const contenedor = document.getElementById('mas-fotos');
    const nuevoInput = document.createElement('input');
    nuevoInput.type = 'file';
    nuevoInput.name = `foto${contadorFotos}`;
    nuevoInput.id = `foto${contadorFotos}`;
    nuevoInput.accept = 'image/*';
    nuevoInput.style.display = 'block';
    nuevoInput.style.marginTop = '10px';
    
    contenedor.appendChild(nuevoInput);
    
    if (contadorFotos >= 5) {
        document.getElementById('btn-agregar-foto').disabled = true;
    }
}