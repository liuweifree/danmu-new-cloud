$(function () {
  var statusE = $('#status');

  if (WebSocket) {
    var client = new WebSocket('ws://' + window.location.host + '/ws/admin');

    client.onopen = function (event) {

      client.onmessage = function (event) {
        console.log(event.data);
      };

      client.onclose = function (event) {
        console.log(event.data);
        statusE.text('已断开，请刷新页面重试');
      };

      client.onerror = function (event) {
        console.log(event.data);
      };

      client.send("ok");

      statusE.text('连接中');
    };

  } else {
    alert('浏览器不支持webscoket，请使用支持html5的浏览器');
  }
});

