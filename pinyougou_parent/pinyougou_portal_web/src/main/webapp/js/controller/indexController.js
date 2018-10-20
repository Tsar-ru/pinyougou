app.controller('indexController',function ($scope,contentService) {
    
    $scope.findByCategotyId=function (categoryId) {
        contentService.findByCategotyId(categoryId).success(function (response) {
            $scope.bannerList = response;
        })
    }
    $scope.searchByKeyword=function () {
       location.href="http://search.pinyougou.com/search.html#?keyword="+$scope.keyword;
    }
})