const validateName = (name) => {
  if(!name) return false;
  let lengthValid = name.trim().length >= 3 && name.trim().length <= 200;
  
  return lengthValid;
}

const validateEmail = (email) => {
  if (!email) return false;
  let lengthValid = email.length <= 100;

  // validamos el formato
  let re = /^[\w.]+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$/;
  let formatValid = re.test(email);

  // devolvemos la lógica AND de las validaciones.
  return lengthValid && formatValid;
};

const validatePhoneNumber = (phoneNumber) => {
  if (!phoneNumber) return false;

  // validación de formato
  let re = /^\+\d{3}\.\d{7,10}$/;
  let formatValid = re.test(phoneNumber);

  // devolvemos la lógica AND de las validaciones.
  return formatValid;
};

const validateContactarPor = () => {
  // obtenemos todos los checkboxes de "contactar por"
  let checkboxes = document.querySelectorAll(
    'input[type="checkbox"][name="whatsapp"], \
     input[type="checkbox"][name="telegram"], \
     input[type="checkbox"][name="x"], \
     input[type="checkbox"][name="instagram"], \
     input[type="checkbox"][name="tiktok"], \
     input[type="checkbox"][name="otra"]'
  );

  let valid = false;

  let checkedCount = 0;
  checkboxes.forEach(chk => {
    if (chk.checked) {
      checkedCount++;
    }

    if(checkedCount > 0 && checkedCount<=5) valid = true;
  });
  return valid;
};

const validateCantidadEdad = (cantidad) => {
  let cantidadTipo = typeof cantidad;
  if(cantidadTipo != "number") return false;
  if(cantidad < 1) return false;
  return true;
}

const validateFiles = (files) => {
  if (!files) return false;

  // validación del número de archivos
  let lengthValid = 1 <= files.length && files.length <= 5;

  // validación del tipo de archivo
  let typeValid = true;

  for (const file of files) {
    // el tipo de archivo debe ser "image/<foo>" o "application/pdf"
    let fileFamily = file.type.split("/")[0];
    typeValid &&= fileFamily == "image";
  }

  // devolvemos la lógica AND de las validaciones.
  return lengthValid && typeValid;
};

const validateSelect = (select) => {
  if(!select) return false;
  return true
}

const validateForm = () => {
  // obtener elementos del DOM usando el nombre del formulario.
  let myForm = document.forms["myForm"];
  let email = myForm["email"].value;
  let numeroTel = myForm["numTel"].value;
  let name = myForm["nombre"].value;
  let files = myForm["input-foto"].files;
  let region = myForm["select-region"].value;
  let comuna = myForm["select-comuna"].value;
  let sector = myForm["input-sector"].value;
  let tipo = myForm["select-tipo"].value;
  let cantidad = parseInt(myForm["input-cantidad"].value);
  let edad = parseInt(myForm["input-edad"].value);
  let medidaEdad = myForm["select-medidaEdad"].value;
  let fechaEntrega = myForm["fecha-disponible-entrega"].value;
  let foto = myForm["input-foto"].value;

  // variables auxiliares de validación y función.
  let invalidInputs = [];
  let isValid = true;
  const setInvalidInput = (inputName) => {
    invalidInputs.push(inputName);
    isValid &&= false;
  };

  // lógica de validación
  if (!validateName(name)) {
    setInvalidInput("Nombre");
  }
  if (!validateEmail(email)) {
    setInvalidInput("Email");
  }
  if (!validatePhoneNumber(numeroTel)) {
    setInvalidInput("Número");
  }
  if (!validateFiles(files)) {
    setInvalidInput("Fotos");
  }
  if(!validateSelect(region)){
    setInvalidInput("Región");
  }
  if(!validateSelect(comuna)){
    setInvalidInput("Comuna");
  }
  if(!validateContactarPor()){
    setInvalidInput("Contactar por");
  }
  if(!validateSelect(tipo)){
    setInvalidInput("Tipo");
  }
  if(!validateCantidadEdad(cantidad)){
    setInvalidInput("Cantidad");
  }
  if(!validateCantidadEdad(edad)){
    setInvalidInput("Edad");
  }
  if(!validateSelect(medidaEdad)){
    setInvalidInput("Medida de Edad");
  }


  // finalmente mostrar la validación
  let validationBox = document.getElementById("val-box");
  let validationMessageElem = document.getElementById("val-msg");
  let validationListElem = document.getElementById("val-list");
  let formContainer = document.querySelector(".main-container");

  if (!isValid) {
    validationListElem.textContent = "";
    // agregar elementos inválidos al elemento val-list.
    for (input of invalidInputs) {
      let listElement = document.createElement("li");
      listElement.innerText = input;
      validationListElem.append(listElement);
    }
    // establecer val-msg
    validationMessageElem.innerText = "Los siguientes campos son inválidos:";

    // aplicar estilos de error
    validationBox.style.backgroundColor = "#ffdddd";
    validationBox.style.borderLeftColor = "#f44336";

    // hacer visible el mensaje de validación
    validationBox.hidden = false;
  } else {
    // Ocultar el formulario
    myForm.style.display = "none";

    // establecer mensaje de éxito
    validationMessageElem.innerText = "¿Está seguro que desea agregar esta actividad?";
    validationListElem.textContent = "";

    // aplicar estilos de éxito
    validationBox.style.backgroundColor = "#ddffdd";
    validationBox.style.borderLeftColor = "#4CAF50";

    // Agregar botones para enviar el formulario o volver
    let submitButton = document.createElement("button");
    submitButton.innerText = "Sí, estoy seguro";
    submitButton.style.marginRight = "10px";
    submitButton.addEventListener("click", () => {
      // myForm.submit();
      // no tenemos un backend al cual enviarle los datos
    });

    let backButton = document.createElement("button");
    backButton.innerText = "No,no estoy seguro, quiero volver al formulario.";
    backButton.addEventListener("click", () => {
      // Mostrar el formulario nuevamente
      myForm.style.display = "block";
      validationBox.hidden = true;
    });

    validationListElem.appendChild(submitButton);
    validationListElem.appendChild(backButton);

    // hacer visible el mensaje de validación
    validationBox.hidden = false;
  }
};


let submitBtn = document.getElementById("btn-enviar");
submitBtn.addEventListener("click", validateForm);
