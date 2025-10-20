const validateName = (name) => {
  if(!name) return false;
  let lengthValid = name.trim().length >= 3 && name.trim().length <= 200;
  return lengthValid;
}

const validateEmail = (email) => {
  if (!email) return false;
  let lengthValid = email.length <= 100;
  let re = /^[\w.]+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$/;
  let formatValid = re.test(email);
  return lengthValid && formatValid;
};

const validatePhoneNumber = (phoneNumber) => {
  if (!phoneNumber) return false;
  let re = /^\+569[0-9]{8}$/;
  let formatValid = re.test(phoneNumber);
  return formatValid;
};

const validateContactarPor = () => {
  let checkboxes = document.querySelectorAll(
    'input[type="checkbox"][name="whatsapp"], \
     input[type="checkbox"][name="telegram"], \
     input[type="checkbox"][name="x"], \
     input[type="checkbox"][name="instagram"], \
     input[type="checkbox"][name="tiktok"], \
     input[type="checkbox"][name="otra"]'
  );

  let checkedCount = 0;
  
  checkboxes.forEach(chk => {
    if (chk.checked) {
      let inputId = document.getElementById(chk.name);
      if (inputId && inputId.value.trim().length >= 4) {
        checkedCount++;
      }
    }
  });
  
  return checkedCount >= 1 && checkedCount <= 5;
};

const validateCantidadEdad = (cantidad) => {
  let num = parseInt(cantidad);
  if(isNaN(num)) return false;
  if(num < 1) return false;
  return true;
}

const validateAgregarFoto = () => {
  let foto1 = document.getElementById('input-foto');
  if (!foto1 || !foto1.files || foto1.files.length === 0) {
    return false;
  }
  
  let cantidadFotos = 1;
  for(let i = 2; i <= 5; i++) {
    let foto = document.getElementById(`foto${i}`);
    if(foto && foto.files && foto.files.length > 0) {
      cantidadFotos++;
    }
  }
  
  return cantidadFotos >= 1 && cantidadFotos <= 5;
}

const validateFechaHora = (fechaStr) => {
  if(!fechaStr) return false;
  
  let fecha = new Date(fechaStr);
  let ahora = new Date();
  let diferencia = (fecha - ahora) / 1000 / 3600; // horas
  
  return diferencia >= 3;
}

const validateSelect = (select) => {
  if(!select) return false;
  return select.trim().length > 0;
}

const validateForm = () => {
  let myForm = document.forms["myForm"];
  let email = myForm["email"].value;
  let numeroTel = myForm["numTel"].value;
  let name = myForm["nombre"].value;
  let region = myForm["select-region"].value;
  let comuna = myForm["select-comuna"].value;
  let tipo = myForm["select-tipo"].value;
  let cantidad = myForm["input-cantidad"].value;
  let edad = myForm["input-edad"].value;
  let medidaEdad = myForm["select-medidaEdad"].value;
  let fechaEntrega = myForm["fecha-disponible-entrega"].value;

  let invalidInputs = [];
  let isValid = true;
  
  const setInvalidInput = (inputName) => {
    invalidInputs.push(inputName);
    isValid = false;
  };

  if (!validateName(name)) {
    setInvalidInput("Nombre");
  }
  if (!validateEmail(email)) {
    setInvalidInput("Email");
  }
  if (!validatePhoneNumber(numeroTel)) {
    setInvalidInput("Número de teléfono (formato: +569XXXXXXXX)");
  }
  if(!validateSelect(region)){
    setInvalidInput("Región");
  }
  if(!validateSelect(comuna)){
    setInvalidInput("Comuna");
  }
  if(!validateContactarPor()){
    setInvalidInput("Contactar por (debe seleccionar al menos 1 y completar el ID)");
  }
  if(!validateSelect(tipo)){
    setInvalidInput("Tipo de mascota");
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
  if(!validateAgregarFoto()){
    setInvalidInput("Fotos (debe subir al menos 1)");
  }
  if (!validateFechaHora(fechaEntrega)) {
    setInvalidInput("Fecha de entrega (debe ser al menos 3 horas en el futuro)");
  }

  let validationBox = document.getElementById("val-box");
  let validationMessageElem = document.getElementById("val-msg");
  let validationListElem = document.getElementById("val-list");
  let modal = document.getElementById("confirmar");

  if (!isValid) {
    validationListElem.innerHTML = "";
    for (let input of invalidInputs) {
      let listElement = document.createElement("li");
      listElement.innerText = input;
      validationListElem.append(listElement);
    }
    validationMessageElem.innerText = "Los siguientes campos son inválidos:";
    validationBox.style.backgroundColor = "#ffdddd";
    validationBox.style.borderLeftColor = "#f44336";
    validationBox.hidden = false;
    window.scrollTo(0, 0);
  } else {
    validationBox.hidden = true;
    modal.style.display = "block";
  }
};

// Event listeners
let submitBtn = document.getElementById("btn-enviar");
if (submitBtn) {
  submitBtn.addEventListener("click", validateForm);
}

let btnNo = document.getElementById("btn-no");
if (btnNo) {
  btnNo.addEventListener("click", () => {
    document.getElementById("confirmar").style.display = "none";
  });
}