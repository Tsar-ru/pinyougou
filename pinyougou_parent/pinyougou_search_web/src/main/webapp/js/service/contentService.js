app.service('contentService',function ($http) {
    this.findByCategotyId=function (categoryId) {
       return $http.get("./index/findByCategotyId?categoryId="+categoryId);
    }
})