<!doctype html>
<html lang="en">
<head>
  <title>视频播放管理</title>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <meta charset="UTF-8">
  <base href="/">
  <link href="bootstrap-3.3.5/css/bootstrap.min.css" rel="stylesheet">
  <link href="index.css" rel="stylesheet">
</head>
<body>

<div class="container">
  <div class="row">
    <div class="col-md-6">

      <h2>视频列表 <span id="status" class="small">未连接</span></h2>

      <table class="table table-hover" id="video-list">
        <tr>
          <th>#</th>
          <th>名称</th>
          <th>状态</th>
          <th></th>
        </tr>

        <tr>
          <th>0</th>
          <td>片花</td>
          <td>播放中</td>
          <td>
            <button type="button" class="btn btn-default btn-sm">
              <span class="glyphicon glyphicon-pause" aria-hidden="true"></span>
            </button>
          </td>
        </tr>

      <#list [0,1,2,3,4,5,6,7,8,9] as num>
        <tr>
          <th>${num+1}</th>
          <td>top${num}</td>
          <td>未播放</td>
          <td>
            <button type="button" class="btn btn-default btn-sm">
              <span class="glyphicon glyphicon-play" aria-hidden="true"></span>
            </button>
          </td>
        </tr>
      </#list>

      </table>

    </div>
    <div class="col-sm-5">

    </div>
  </div>
</div>

<script src="jquery.min.js"></script>
<script src="bootstrap-3.3.5/js/bootstrap.min.js"></script>
<script src="socket.io.min.js"></script>
<script src="index.js"></script>

</body>
</html>