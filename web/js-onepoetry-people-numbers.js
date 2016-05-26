var URL="ws://127.0.0.1:8080/chat/index";
var webSocket=null;
if('WebSocket' in window) {
    webSocket = new WebSocket(URL);
}else {
    alert("浏览器版本太低，不支持游戏，请升级浏览器后进行游戏！");
}

function setMessageInnerHTML(innerHTML){
    document.getElementById("userNumber").innerHTML+=innerHTML+"</br>";
}
webSocket.onmessage= function (event) {
    setMessageInnerHTML(event.data);
}

function startGame() {
    webSocket.close();
}
