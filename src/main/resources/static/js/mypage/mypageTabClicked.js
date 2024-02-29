window.onload = function() {
    let nowLocation = window.location.href;

    if (nowLocation.includes('/pwd')) {
        document.getElementById('mypage-tab-pwd').style.borderLeft = '3px solid #3CB728';
    } else if (nowLocation.includes('/writings')) {
        document.getElementById('mypage-tab-writings').style.borderLeft = '3px solid #3CB728';
    } else if (nowLocation.includes('/bookmarks')) {
        document.getElementById('mypage-tab-bookmarks').style.borderLeft = '3px solid #3CB728';
    } else {
        document.getElementById('mypage-tab-profile').style.borderLeft = '3px solid #3CB728';
    }
}