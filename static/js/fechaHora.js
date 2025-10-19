window.onload = function() {
  const fechaInput = document.getElementById("fecha-disponible-entrega");

  // obtenemos fecha actual + 3 horas
  let fecha = new Date();
  fecha.setHours(fecha.getHours() + 3);

  
  let yyyy = fecha.getFullYear();
  let mm = String(fecha.getMonth() + 1).padStart(2, "0"); 
  let dd = String(fecha.getDate()).padStart(2, "0");
  let hh = String(fecha.getHours()).padStart(2, "0");
  let min = String(fecha.getMinutes()).padStart(2, "0");

  let valorPrellenado = `${yyyy}-${mm}-${dd}T${hh}:${min}`;
  fechaInput.value = valorPrellenado;

  fechaInput.min = valorPrellenado;
};