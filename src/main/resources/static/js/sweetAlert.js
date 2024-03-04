
/*
* @icon success / error / warning / info / question
* @text text in modal
* @btn confirm button in modal true / false
*
* */
function showAlert(icon, text, btn) {
    Swal.fire({
        position: 'center',
        icon: icon,
        title: '',
        text: text,
        showConfirmButton: btn,
    });
}