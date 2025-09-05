document.querySelectorAll('#actividades-mas tbody tr').forEach((row) => {
    row.addEventListener('click', function(event){
        const actividad = row.querySelectorAll('td');
        const infoActividad ={
            inicio: actividad[0].innerText,
            Termino: actividad[1].innerText,
            Comuna: actividad[2].innerText,
            Sector: actividad[3].innerText,
            Tema: actividad[4].innerText,
            Nombre_Organizador: actividad[5].innerText,
            Total_fotos: []
        }
        
        const Total_fotos = row.querySelectorAll('td img');
        Total_fotos.forEach((foto) => {
            infoActividad.Total_fotos.push(foto.src);
        })

        //Guardamos la informacion en lcoal storage
        localStorage.setItem("infoActividad", JSON.stringify(infoActividad));

        //Redireccion
        window.location.href = 'informacio_actividad.html'
    })
})