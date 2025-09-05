window.onload = function () {
    const datos = JSON.parse(localStorage.getItem("infoActividad"));

    if (datos) {
        document.getElementById("fecha-publicacion").innerText = "Fecha Publicación: " + datos.inicio;
        document.getElementById("fecha-entrega").innerText = "Fecha Entrega: " + datos.Termino;
        document.getElementById("comuna").innerText = "Comuna: " + datos.Comuna;
        document.getElementById("sector").innerText = "Sector: " + datos.Sector;
        document.getElementById("C-T-E").innerText = "Cantidad Tipo Edad: " + datos.Tema;
        document.getElementById("nombre-Contacto").innerText = "Nombre Contacto: " + datos.Nombre_Organizador;

        const fotosContainer = document.getElementById("total-fotos");
        fotosContainer.innerHTML = ""; // Limpiar contenido anterior

        datos.Total_fotos.forEach((src) => {
            const img = document.createElement("img");
            img.src = src;
            img.width = 320;
            img.height = 240;
            img.style.margin = "10px";
            img.style.cursor = "pointer";

            // Evento para agrandar imagen al hacer clic
            img.onclick = () => {
                const overlay = document.createElement("div");
                overlay.style.position = "fixed";
                overlay.style.top = "0";
                overlay.style.left = "0";
                overlay.style.width = "100%";
                overlay.style.height = "100%";
                overlay.style.backgroundColor = "rgba(0,0,0,0.8)";
                overlay.style.display = "flex";
                overlay.style.alignItems = "center";
                overlay.style.justifyContent = "center";

                const grande = document.createElement("img");
                grande.src = src;
                grande.width = 800;
                grande.height = 600;

                // Botón cerrar
                const cerrarBtn = document.createElement("button");
                cerrarBtn.innerText = "Cerrar";
                cerrarBtn.onclick = () => overlay.remove();

                overlay.appendChild(grande);
                overlay.appendChild(cerrarBtn);
                document.body.appendChild(overlay);
            };

            fotosContainer.appendChild(img);
        });
    }
};