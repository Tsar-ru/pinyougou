app.controller('indexController',function ($scope,contentService) {
    
    $scope.findByCategotyId=function (categoryId) {
        contentService.findByCategotyId(categoryId).success(function (response) {
            $scope.bannerList = response;
        })
    }
})