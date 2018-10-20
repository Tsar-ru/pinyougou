app.controller("baseController",function($scope){

    $scope.paginationConf = {
        currentPage: 1,  //当前页码
        totalItems: 10,  //总条数 需要从后台查询赋值
        itemsPerPage: 10,//每页显示的条数
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };


    $scope.reloadList=function(){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }


    // 准备一个空数组，存放的是即将删除的id
    $scope.selectedIds=[];
    // 点击复选框修改数组的内容
    $scope.updateSelection=function(event,id){
        // event.target   //返回触发此事件的元素  在此event.target指的就是复选框
        if(event.target.checked){
            //代表复选框是勾选  把id放到数组中 数组的push方法
            $scope.selectedIds.push(id);
        }else{
            //代表复选框取消勾选  把id 从数组中移除 splice(移除值在数组中的索引,移除的数量)
            // 获取索引值得方式：  indexOf
            var index = $scope.selectedIds.indexOf(id);
            $scope.selectedIds.splice(index,1);
        }
    }

    // 判断id是否出现在数组中
    $scope.isChecked=function(id){
        // “abc”  d
        var index = $scope.selectedIds.indexOf(id);
        // if(index!=-1){
        //     return true;
        // }else{
        //     return false;
        // }
        return index !=-1;
    }

    // 判断当前页的数据是否都在数组中 如果但凡有一个不在 就返回false
    $scope.isCheckedAll=function(){
        for (var i = 0; i < $scope.list.length; i++) {
           if(! $scope.isChecked($scope.list[i].id)){
               return false;
           }
        }
        return true;
    }

    $scope.selectAll=function(event){
        if(event.target.checked){
            for (var i = 0; i < $scope.list.length; i++) {
                // 添加之前想判读id是否存在数组中
               if(! $scope.isChecked($scope.list[i].id)){
                   $scope.selectedIds.push( $scope.list[i].id);
               }
            }
        }else{
            for (var i = 0; i < $scope.list.length; i++) {
                // 添加之前想判读id是否存在数组中
                var index = $scope.selectedIds.indexOf($scope.list[i].id)
                $scope.selectedIds.splice( index,1);
            }
        }
    }
})