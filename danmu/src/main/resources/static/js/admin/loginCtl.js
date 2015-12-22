app.controller('loginCtrl', function ($scope, $http, $location, UserDetail) {

  $scope.credentials = {};
  $scope.err = false;

  UserDetail.checkLogin(0, function () {
    $scope.login = function () {
      $http.post('/login', $scope.credentials)
        .success(function (res, a, b, c) {
          console.log(res);
          if (res.path == '/') {
            UserDetail.checkLogin();
          } else if (res.path == '/login') {
            $scope.err = true;
            $scope.credentials.password = '';
          }
        }).error(function (data, status, headers, config) {
          console.log(data);
        });
    };
  });
});