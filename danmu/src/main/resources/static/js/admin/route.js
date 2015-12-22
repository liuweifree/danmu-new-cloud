app.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  $urlRouterProvider.otherwise('/');

  $stateProvider
    .state('index', {
      url: '/',
      templateUrl: '/tpl/dashboard.html'
    })
    .state('login', {
      url: '/login',
      templateUrl: '/tpl/login.html'
    });

}]);