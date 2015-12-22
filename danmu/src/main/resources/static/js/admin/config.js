var app = angular.module('app', ['ui.router']).config(["$httpProvider", function ($httpProvider) {
  $httpProvider.interceptors.push('securityInterceptor');

  $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
  var param = function (obj) {
    var query = '', name, value, fullSubName, subName, subValue, innerObj, i;
    for (name in obj) {
      value = obj[name];
      if (value instanceof Array) {
        for (i = 0; i < value.length; ++i) {
          subValue = value[i];
          fullSubName = name + '[' + i + ']';
          innerObj = {};
          innerObj[fullSubName] = subValue;
          query += param(innerObj) + '&';
        }
      }
      else if (value instanceof Object) {
        for (subName in value) {
          subValue = value[subName];
          fullSubName = name + '[' + subName + ']';
          innerObj = {};
          innerObj[fullSubName] = subValue;
          query += param(innerObj) + '&';
        }
      }
      else if (value !== undefined && value !== null)
        query += encodeURIComponent(name) + '=' + encodeURIComponent(value) + '&';
    }

    return query.length ? query.substr(0, query.length - 1) : query;
  };

  // Override $http service's default transformRequest
  $httpProvider.defaults.transformRequest = [function (data) {
    return angular.isObject(data) && String(data) !== '[object File]' ? param(data) : data;
  }];
}]).factory('securityInterceptor', ["$q", "$location", function ($q, $location) {
  var responseInterceptor = {
    'response': function (respose) {
      if (respose.status === 302 || respose.status === 401) {
        $location.path('/login');
        return $q.reject(respose);
      } else {
        return $q.resolve(respose);
      }
    },
    'responseError': function (rejection) {
      if (rejection.status === 403 || rejection.status === 401) {
        $location.path('/login');
        return $q.reject(rejection);
      } else {
        return $q.resolve(rejection);
      }
    },
  };

  return responseInterceptor;
}]).factory('UserDetail', ['$http', '$location', function ($http, $location) {
  var checkLogin = function (fn1, fn2) {
    $http.get('/v1/api/getPrincipal', {})
      .success(function (res, a, b, c) {
        if (res && res.principal && res.principal.username) {
          $location.path('/');
          if (fn1) {
            fn1();
          }
        } else {
          $location.path('/login');
          if (fn2) {
            fn2();
          }
        }
        console.log(res);
      }).error(function (data, status, headers, config) {
        console.log(data);
      });
  };
  return {
    'checkLogin': checkLogin
  }
}]);