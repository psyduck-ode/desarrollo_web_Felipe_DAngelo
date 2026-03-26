// Cargar regiones desde la API al cargar la página
const poblarRegion = async () => {
    try {
        const response = await fetch('/api/regiones');
        if (!response.ok) throw new Error('Error al cargar regiones');
        
        const regiones = await response.json();
        let regionSelect = document.getElementById("select-region");
        
        regiones.forEach(region => {
            let option = document.createElement("option");
            option.value = region.nombre;
            option.textContent = region.nombre;
            regionSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error al cargar regiones:', error);
    }
};

const updateComuna = async () => {
    let regionSelect = document.getElementById("select-region");
    let comunaSelect = document.getElementById("select-comuna");
    let selectedRegion = regionSelect.value;
    
    comunaSelect.innerHTML = '<option value="">Seleccione una comuna</option>';

    if(selectedRegion){
        try {
            const response = await fetch(`/api/regiones/${encodeURIComponent(selectedRegion)}/comunas`);
            if (!response.ok) throw new Error('Error al cargar comunas');
            
            const comunas = await response.json();
            comunas.forEach(comuna => {
                let option = document.createElement("option");
                option.value = comuna.nombre;
                option.text = comuna.nombre;
                comunaSelect.appendChild(option);
            });
        } catch (error) {
            console.error('Error al cargar comunas:', error);
        }
    }
    changeArguments();
};

function changeArguments(){
    const regionSelect = document.getElementById("select-region");
    const reasonLabel = document.querySelector("label[for='reason']");

    if(regionSelect && reasonLabel) {
        if(regionSelect.value !== ""){
            reasonLabel.style.display = "block";
        }
        else{
            reasonLabel.style.display = "none";
        }
    }
}

document.getElementById("select-region").addEventListener("change", updateComuna);

if(document.querySelector("label[for='reason']")) {
    document.getElementById("select-region").addEventListener("change", changeArguments);
}
    
document.addEventListener("DOMContentLoaded", () => {
    poblarRegion();
    if(document.querySelector("label[for='reason']")) {
        changeArguments();
    }
});