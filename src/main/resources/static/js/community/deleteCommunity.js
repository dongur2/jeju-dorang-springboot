
/*
* 삭제 요청
* */
function deletePost(type, communityId, from) {
    let xhr = new XMLHttpRequest();

    var url;
    if(type === 'PARTY') {
        url = '/community/boards/parties/' + communityId;
    } else if (type === 'CHAT') {
        url = '/community/boards/chats/' + communityId;
    }

    xhr.open('DELETE', url, true);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                Swal.fire({
                    position: 'center',
                    icon: 'question',
                    title: '',
                    text: '게시글을 삭제하시겠습니까?.',
                    showConfirmButton: true,

                }).then((result) => {
                    if (result.isConfirmed) {
                        if (from === 'detail') {
                            window.location.href = xhr.responseText;
                        } else {
                            window.location.reload();
                        }
                    }
                });
            } else { // 그 외의 경우
                showAlert('error', xhr.responseText, true);
            }
        }
    };
    xhr.send();
}