var client;
app.controller('dashboardCtl', function ($scope, $http, $interval, UserDetail) {
  $scope.wsStatus;//1未连接，2已连接，3已断开
  $scope.danmuList;
  $scope.danmuMsg;
  $scope.isBlockEnable;//屏蔽词功能是否开启
  $scope.isDelayEnable;//延时功能是否开启
  $scope.delaySecond;
  $scope.playerStatus;//0 关闭 1 显示视频 2 显示弹幕
  $scope.blockKeywords = [];
  $scope.blockKeyword;
  $scope.playerIndexList = [];
  $scope.colors = [];
  $scope.color = 0;
  $scope.testIsOpen = false;

  $scope.danmuListScrollTop = 0;

  $scope.expressions = [];

  UserDetail.checkLogin(function () {

    var setDanmuLeftTime = function (danmu, nowTime) {
      if (!danmu.s || danmu.s > 0) {
        if (!danmu.createTime) {
          danmu.createTime = new Date().getTime();
        }
        danmu.s = parseInt($scope.delaySecond - ( nowTime - danmu.createTime) / 1000);
        if (danmu.s == 0) {
          danmu.s = -1;
        }
      }
      return danmu;
    };

    var danmuListE = document.getElementById('www');

    var t;
    //刷新弹幕列表的时间
    var refreshDanmuList = function () {
      if ($scope.isDelayEnable && $scope.danmuList && $scope.danmuList.length > 0) {
        var nowTime = new Date().getTime();
        for (var i = 0; i < $scope.danmuList.length; i++) {
          setDanmuLeftTime($scope.danmuList[i], nowTime)
        }
        $scope.danmuListScrollTop = danmuListE.scrollTop;
      }
    };

    var checkConnect = function () {
      if ($scope.wsStatus == 2) {
        return true;
      } else {
        alert("服务器连接已中断，确定后刷新页面");
        window.location.reload();
        return false;
      }
    };

    $scope.clearAndTurnUp = function () {
      var danmuList = [];
      for (var i = 0; i < $scope.danmuList.length; i++) {
        var danmu = $scope.danmuList[i];
        if (!danmu.s || danmu.s > 0) {
          danmuList.push(danmu);
        }
      }
      $scope.danmuList = danmuList;
      danmuListE.scrollTop = 0;
      document.body.scrollTop = 0;
    };

    if (WebSocket) {
      if (client) {
        client.close();
      }
      client = new WebSocket('ws://' + window.location.host + '/ws/admin');
      client.onopen = function (event) {

        //定时请求，防止掉线
        setInterval(function () {
          if ($scope.wsStatus == 2) {
            client.send(JSON.stringify({type: 'isOk'}));
          }
        }, 30 * 1000);

        client.onmessage = function (event) {
          var json = JSON.parse(event.data);
          if (json.type == 'error') {
            alert('发生错误：' + json.data);
            return;
          } else if (json.data == 'fail') {
            alert('操作失败！');
            return;
          } else if (json.type == 'init') {
            $scope.delaySecond = parseInt(json.data.delaySecond);
            $scope.isDelayEnable = json.data.isDelayEnable;
            $scope.playerStatus = json.data.playerStatus;
            $scope.isBlockEnable = json.data.isBlockEnable;
            $scope.danmuList = json.data.danmuList;
            $scope.playerIndexList = json.data.playerIndexList;
            $scope.colors = json.data.colors;
            $scope.expressions = json.data.expressions;
            $scope.testIsOpen = json.data.test;

            $interval(refreshDanmuList, 500);

          } else if (json.type == 'isDelayEnable') {
            $scope.isDelayEnable = json.data;
            if (!$scope.isDelayEnable) {
              $scope.danmuList = [];
            }
          } else if (json.type == 'delaySecond') {
            $scope.delaySecond = parseInt(json.data);
          } else if (json.type == 'playerStatus') {
            $scope.playerStatus = json.data;
          } else if (json.type == 'isBlockEnable') {
            $scope.isBlockEnable = json.data;
          } else if (json.type == 'playerIndexList') {
            $scope.playerIndexList = json.data;
          } else if (json.type == 'danmuAdd') {
            $scope.danmuList.push(setDanmuLeftTime(json.data, new Date().getTime()));
            if ($scope.danmuList.length > 1000) {
              $scope.clearAndTurnUp();
            }
          } else if (json.type == 'test') {
            $scope.testIsOpen = json.data;
            $scope.danmuList = [];
          } else if (json.type == 'sendDm') {
            $scope.danmuSendStatus = json.data ? 1 : 2;
            $scope.danmuMsg = "";
            if (t) {
              clearTimeout(t);
            }
            t = setTimeout(function () {
              $scope.danmuSendStatus = 0;
            }, 1000);

          } else if (json.type == 'danmuBlock') {
            if (json.data) {
              for (var i = 0; i < $scope.danmuList.length; i++) {
                var danmu = $scope.danmuList[i];
                if (danmu.id == json.data) {
                  danmu.isBlocked = true;
                  danmu.s = -10;
                  break;
                }
              }
            }
          } else if (json.type == 'danmuUnBlock') {
            if (json.data) {
              for (var i = 0; i < $scope.danmuList.length; i++) {
                var danmu = $scope.danmuList[i];
                if (danmu.id == json.data) {
                  danmu.isBlocked = false;
                  break;
                }
              }
            }
          } else if (json.type == 'danmuSent') {
            for (var i = 0; i < $scope.danmuList.length; i++) {
              var danmu = $scope.danmuList[i];
              if (danmu.id == json.data) {
                danmu.s = -1;
                break;
              }
            }
          } else {
            return;
          }
          $scope.$apply();
        }

        client.onclose = function (event) {
          $scope.wsStatus = 3;
          $scope.$apply();
        };

        client.onerror = function (event) {
          console.log(event.data);
        };

        $scope.setDelaySecond = function (add) {
          if (checkConnect()) {
            client.send(JSON.stringify({type: 'delaySecond', add: add}));
          }
        };

        $scope.setIsDelayEnable = function (status) {
          if (status != $scope.isDelayEnable && checkConnect()) {
            client.send(JSON.stringify({type: 'isDelayEnable', status: status}));
          }
        };

        $scope.setPlayerStatus = function (status) {
          if (status != $scope.playerStatus && checkConnect()) {
            client.send(JSON.stringify({type: 'playerStatus', status: status}));
          }
        };
        $scope.setDanmuBlocked = function (id) {
          if (status != $scope.isBlockEnable && checkConnect()) {
            client.send(JSON.stringify({type: 'danmuBlock', id: id}));
          }
        };
        $scope.setIsBlockEnable = function (status) {
          if (status != $scope.isBlockEnable && checkConnect()) {
            client.send(JSON.stringify({type: 'isBlockEnable', status: status}));
          }
        };
        $scope.sendDm = function () {
          if (checkConnect()) {
            if ($scope.danmuMsg && $scope.danmuMsg.length <= 40
              && (!$scope.testIsOpen || confirm('当前为测试模式，发送弹幕不会显示，确认要发送吗？'))) {
              client.send(JSON.stringify({
                type: 'sendDm',
                msg: $scope.danmuMsg,
                "color": $scope.colors[$scope.color]
              }));
              $scope.blockKeyword = "";
            }
          }
        };

        $scope.sendEx = function (ex) {
          if (checkConnect()) {
            client.send(JSON.stringify({type: 'sendEx', "expression": ex}));
          }
        };

        $scope.setTest = function (status) {
          if (checkConnect()) {
            client.send(JSON.stringify({type: 'test', "status": status}));
          }
        };

        $scope.wsStatus = 2;
        $scope.$apply();
      }
    } else {
      alert('浏览器不支持webscoket，请使用支持html5的浏览器');
    }

    var refershBlockKeyword = function () {
      $http.get('/v1/api/admin/blockKeywords')
        .success(function (data) {
          $scope.blockKeywords = data.data;
        }).error(function (data, status, headers, config) {
          console.log(data);
        });
    };
    refershBlockKeyword();

    $scope.addBlockKeyword = function () {
      if (!$scope.blockKeyword) {
        alert('屏蔽词不能为空');
      } else {
        $http.post('/v1/api/admin/blockKeywords', {'word': $scope.blockKeyword})
          .success(function (data) {
            if (data.result == 200) {
              refershBlockKeyword();
            }
          }).error(function (data, status, headers, config) {
            console.log(data);
          });
        $scope.blockKeyword = "";
      }
    };

    $scope.delBlockKeyword = function (id, word) {
      if (confirm('确认要删除屏蔽词“' + word + '”吗？')) {
        $http.delete('/v1/api/admin/blockKeywords/' + id, {})
          .success(function (data) {
            refershBlockKeyword();
          }).error(function (data, status, headers, config) {
            console.log(data);
          });
      }
    };
    $scope.setEx = function (index) {
      $scope.color = index;
    };
  });

});