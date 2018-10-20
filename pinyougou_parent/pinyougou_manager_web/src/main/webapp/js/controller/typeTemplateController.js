app.controller("typeTemplateController",function ($scope,$controller,specificationService,brandService, typeTemplateService) {

    $controller("baseController",{$scope:$scope});

    $scope.brandList={data:[{'id':1,'text':'aaaa'},{'id':2,'text':'bbbb'},{'id':3,'text':'cccc'}]};

    $scope.findBrandList=function(){
        // [{'id':1,'text':'aaaa'},{'id':2,'text':'bbbb'},{'id':3,'text':'cccc'}]
        brandService.findBrandList().success(function (response) {
            $scope.brandList = {data:response};
        })
    }
    $scope.findSpecList=function(){
        // [{'id':1,'text':'aaaa'},{'id':2,'text':'bbbb'},{'id':3,'text':'cccc'}]
        specificationService.findSpecList().success(function (response) {
            $scope.specList = {data:response};
        })
    }

    $scope.findOne=function (id) {
        typeTemplateService.findOne(id).success(function (response) {
            // 把字符串转成对象 JSON.parse（字符串）;   是JS的方法
            response.brandIds = JSON.parse(response.brandIds);
            response.specIds = JSON.parse( response.specIds)
            response.customAttributeItems =  JSON.parse( response.customAttributeItems);
            $scope.entity=response;
        })
    }
     //   [{"id":32,"text":"富光"},{"id":33,"text":"希乐"}]--->富光，希乐  or  [{"text":"包邮方式"},{"text":"颜色"}]--->包邮方式，颜色
    $scope.arrayListToString=function (list) {
        list = JSON.parse(list);//需要把字符串转成对象
        var str = "";
        for (var i = 0; i < list.length; i++) {
            if(i==(list.length-1)){
                str+= list[i].text;
            }else{
                str+= list[i].text+",";
            }
        }
        return str;
    }


    $scope.save=function () {
        // JSON.stringify($scope.entity);//把对象转字符串  是JS的方法
        // alert(JSON.stringify($scope.entity));
        var obj=null;
        if($scope.entity.id!=null){
            obj = typeTemplateService.update($scope.entity);
        }else{
            obj = typeTemplateService.add($scope.entity);
        }
        obj.success(function (response) {
            if(response.success){
                $scope.reloadList();
            }else{
                alert(response.message);
            }
        })
    }

    $scope.addCustomAttributeItems=function () {
        $scope.entity.customAttributeItems.push({});
    }
    $scope.deleCustomAttributeItems=function (index) {
        $scope.entity.customAttributeItems.splice(index,1);
    }
    $scope.searchEntity={};
    $scope.search=function(pageNum,pageSize){
        typeTemplateService.search(pageNum,pageSize,$scope.searchEntity).success(function (response) {
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        })
    }
})