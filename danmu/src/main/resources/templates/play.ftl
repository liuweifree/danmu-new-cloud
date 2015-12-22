<!doctype html>
<html>
<head>
  <title></title>
  <base href="/">
  <meta charset="utf-8"/>
  <link href="/css/play.css" type="text/css" rel="stylesheet">
</head>
<body>

<div class="player">
  <object type="application/x-shockwave-flash" allowfullscreeninteractive="true" allowfullscreen="true"
          allowscriptaccess="always" data="/swf/DanmuMovie.swf">
    <param name="allowFullscreenInteractive" value="true">
    <param name="allowfullscreen" value="true">
    <param name="allowscriptaccess" value="always">
    <param name="flashvars"
           value="widthPercent=${widthPercent}&heightPercent=${heightPercent}&maxSize=${maxSize}&screen=${index}&show=${show}&expressionScale=${expressionScale}&speed1=${speed1}&speed2=${speed2}&offY=${offY}">
  </object>
</div>
<script src="./bower_components/jquery/dist/jquery.min.js"></script>
<script src="/js/play.js"></script>
</body>
</html>