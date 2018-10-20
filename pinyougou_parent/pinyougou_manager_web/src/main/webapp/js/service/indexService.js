app.service("indexService",function ($http) {

    this.showUsername=function () {
         return $http.get("../index/showUsername");
    }
})