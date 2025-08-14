let checkPasswordYN = 'N';
let checkPasswordConfirmYN = 'N';

function checkPassword1() {

    const password = $("#password1").val();
    const regex = /^(?=.*[0-9])(?=.*[A-Za-z])(?=.*[`~!@#$%^&*\\(\\)\-_=+]).{8,20}$/g
    const result = regex.test(password);

    if (result === true && password.search(/\s/) === -1) {
        checkPasswordYN = "Y";
        $("#passwordHelpBlock1").hide();
    } else {
        checkPasswordYN = "N";
        $("#passwordHelpBlock1").show();
    }
}

function checkPassword2() {

    const password1 = $("#password1").val();
    const password2 = $("#password2").val();

    if (password2 !== '' && password1 === password2) {
        checkPasswordConfirmYN = 'Y';
        $("#passwordHelpBlock2").hide();
    } else {
        checkPasswordConfirmYN = 'N';
        $("#passwordHelpBlock2").show();
    }
}

function validImage() {
    const file = $("#image")[0].files[0];
    return file !== undefined
}

function checkImage() {
    const LIMIT = 3;
    const file = $("#image")[0].files[0];
    const maxSize = LIMIT * 1024 * 1024;

    if (file.size > maxSize) {
        alert("파일첨부 사이즈는 " + LIMIT + "MB 이내로 가능합니다.");
        $("#image").val('');
    }
}