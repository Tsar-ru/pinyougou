app.controller('searchController',function ($scope,$location,searchService) {

    $scope.paramMap={keyword:'老王',category:'',brand:'',price:'',order:'asc',page:1,spec:{}};



    $scope.addParamToMap=function(key,value){
        $scope.paramMap[key] = value;
        $scope.search();
    }
    $scope.deleParamToMap=function(key){
        $scope.paramMap[key] = '';
        $scope.search();
    }

    $scope.addSpecParamToMap=function(key,value){
        // 网络    移动3G
        // $scope.paramMap.spec['网络'] = ' 移动3G'
        // $scope.paramMap.spec['机身内存'] = ' 16G'  ----->  spec={'网络':' 移动3G','机身内存':'16G'}
        $scope.paramMap.spec[key] = value;
        $scope.search();
    }

    $scope.deleSpecParamToMap=function(key){
           // $scope.paramMap.spec={'网络':' 移动3G','机身内存':'16G'}
        // key:网络---------->$scope.paramMap.spec={'机身内存':'16G'}
        delete $scope.paramMap.spec[key];  //从map中移除一对数据
        $scope.search();
    }

    $scope.initSearch=function () {
        // 从url上获取带过来的参数
        // http://search.pinyougou.com/search.html#?keyword=手机
        if($location.search()['keyword']!=null){
            $scope.keyword=$location.search()['keyword'];
            $scope.paramMap.keyword=$location.search()['keyword'];
        }
        $scope.search()
    }

    $scope.searchByKeyword=function () {
        $scope.paramMap={keyword:'',category:'',brand:'',price:'',order:'asc',page:1,spec:{}};
        $scope.paramMap.keyword=$scope.keyword;
        $scope.search();
    }

    $scope.search=function () {
        searchService.searchByParamMap($scope.paramMap).success(function (response) {
            $scope.resultMap = response;
            // resultMap.totalPages = 3
           /* $scope.pageLabel=[];
            for (var i = 0; i < response.totalPages; i++) {
                $scope.pageLabel.push(i+1);
            }*/
            buildPageLabel();

        })
    }

    function buildPageLabel() {
        $scope.pageLabel = [];//新增分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;//得到最后页码
        var firstPage = 1;//开始页码
        var lastPage = maxPageNo;//截止页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后边有点
        if ($scope.resultMap.totalPages > 5) { //如果总页数大于 5 页,显示部分页码
            if ($scope.paramMap.page <= 3) {//如果当前页小于等于 3
                lastPage = 5; //前 5 页
                $scope.firstDot = false;//前面没点
            } else if ($scope.paramMap.page >= lastPage - 2) {//如果当前页大于等于最大页码-2
                firstPage = maxPageNo - 4;  //后 5 页
                $scope.lastDot = false;//后边没点
            } else { //显示当前页为中心的 5 页
                firstPage = $scope.paramMap.page - 2;
                lastPage = $scope.paramMap.page + 2;
            }
        } else {
            $scope.firstDot = false;//前面无点
            $scope.lastDot = false;//后边无点
        }
        //循环产生页码标签
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }

})