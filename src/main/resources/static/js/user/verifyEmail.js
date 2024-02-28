
/*
* 인증 번호 전송 & 이메일 중복 확인
* */
function sendMail() {
    var mailSendRequest = {
        email: document.getElementById("email").value
    };

    if (mailSendRequest.email == null || mailSendRequest.email.trim() === "") {
        showAlert('warning', '이메일을 입력해주세요.', true);

    } else {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "/email/available", true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    showAlert('success', '이메일 인증 번호 전송이 완료되었습니다. 전송된 인증 번호를 입력해주세요.', true);

                    // 인증 번호 입력란, 인증 번호 확인 버튼 노출
                    document.getElementById("email-verify-container").style.display = "block";

                } else {
                    showAlert('error', xhr.responseText, true);
                }
            }
        }
        xhr.send(JSON.stringify(mailSendRequest));
    }
}


/*
* 인증 번호 확인
* */
function checkMail() {
    var mailVerifyRequest = {
        email: document.getElementById("email").value,
        code: document.getElementById("email-verify").value
    };

    if (mailVerifyRequest.email === null || mailVerifyRequest.email.trim() === "") {
        showAlert('warning', '이메일을 입력해주세요.', true);

    } else if (mailVerifyRequest.code === null || mailVerifyRequest.code.trim() === "") {
        showAlert('warning', '인증번호를 입력해주세요.', true);

    } else if (mailVerifyRequest.code.trim().length < 6 || mailVerifyRequest.code.trim().length > 6) {
        showAlert('warning', '인증번호는 6글자입니다.', true);

    } else {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "/email/verify", true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    showAlert('success', '이메일 인증이 완료되었습니다.', true);

                    // 컨트롤러로 전달할 이메일 인증 상태 저장
                    document.getElementById('email-verify-status').value = true;

                    // 컨트롤러로 전달할 이메일 값 저장
                    document.getElementById("email-verify-dto").value = document.getElementById("email").value;

                    // 이메일 인증 완료 -> 이메일 수정, 인증 번호 전송/수정/확인 불가
                    let elements = document.getElementsByClassName("btn-verify");
                    for (var i = 0; i < elements.length; i++) {
                        elements[i].disabled = true;
                    }

                    if(document.getElementById('btn-for-reset-pwd') != null) {
                        document.getElementById('btn-for-reset-pwd').style.display = 'block';
                    }

                } else {
                    showAlert('error', xhr.responseText, true);
                }
            }
        }
        xhr.send(JSON.stringify(mailVerifyRequest));
    }
}


