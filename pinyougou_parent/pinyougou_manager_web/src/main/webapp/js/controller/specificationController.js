// controller 只和html交互 不直接和后台交互
app.controller("specificationController",function($scope,$controller, specificationService){  //直接把specificationService注入到controller中

    // 控制器的继承
      $controller('baseController',{$scope:$scope});//继承   本质上是共用同一个$scope

    // 动态添加行数据  本质就是向 entity.tbSpecificationOptionList中追加空对象
    // 注意 需要提前定义tbSpecificationOptionList属性  新建时ng-click="entity={tbSpecificationOptionList:[]}"
      $scope.addTbSpecificationOptionList=function () {
          $scope.entity.tbSpecificationOptionList.push({});
      }
// 动态删除行数据
    $scope.deleTbSpecificationOptionList=function (index) {
         $scope.entity.tbSpecificationOptionList.splice(index,1);
    }


        $scope.findPage=function(pageNum,pageSize){
            specificationService.findPage(pageNum,pageSize).success(function (response) {
                // 返回的数据 ：总条数 和 当前页的数据 {total:110,rows:[{},{},{}]}
                $scope.paginationConf.totalItems = response.total;
                $scope.list = response.rows;
            })
        }

        $scope.findAll=function () {
            specificationService.findAll().success(function(response){
                // response就是查询出的所有数据
                $scope.list = response;
            })
        }

        $scope.save=function () {
            var  obj = null;
            // entity.tbSpecification.id 是组合类
            if($scope.entity.tbSpecification.id!=null){
                obj = specificationService.update($scope.entity);
            }else{
                obj = specificationService.add($scope.entity);
            }
            obj.success(function (response) {
                if(response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            });
        }

        $scope.findOne=function(id){
            specificationService.findOne(id).success(function (response) {
                // response就应该是返回的一个品牌对象
                $scope.entity=response;
            })
        }


        $scope.dele=function(){
            // 判断数组是否为空 如果为空 return
            if($scope.selectedIds.length==0){
                return;
            }

            var flag = window.confirm("确认要删除您选择的数据吗?");
            if(flag){
                specificationService.dele($scope.selectedIds).success(function (response) {
                    if(response.success){
                        $scope.reloadList();
                        // 把数组清空
                        $scope.selectedIds=[];
                    }else{
                        alert(response.message);
                    }
                })
            }
        }
        $scope.searchEntity={};//需要初始化 不然是undefined
        $scope.search=function(pageNum,pageSize){
            // 向后台传的参数有：$scope.searchEntity
            // 当前页码$scope.paginationConf.currentPage,每页显示的条数$scope.paginationConf.itemsPerPage
            specificationService.search(pageNum,pageSize,$scope.searchEntity).success(function (response) {
                // 返回的数据 ：总条数 和 当前页的数据 {total:110,rows:[{},{},{}]}
                $scope.paginationConf.totalItems = response.total;
                $scope.list = response.rows;
            })

        }
    })
