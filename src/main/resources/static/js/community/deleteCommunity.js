
/*
* 삭제 요청
* */
function deletePost(type, communityId, from) {
    alert('delete');

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
                alert('게시글 삭제가 완료되었습니다.');

                if(from === 'detail') {
                    window.location.href = xhr.responseText;
                } else {
                    window.location.reload();
                }

            } else { // 그 외의 경우
                alert(xhr.responseText);
            }
        }
    };
    xhr.send();
}