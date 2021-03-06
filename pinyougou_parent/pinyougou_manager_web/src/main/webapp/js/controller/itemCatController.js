 //控制层 
app.controller('itemCatController' ,function($scope ,itemCatService,typeTemplateService){

	$scope.grade=1; //记录的是列表显示的是第几级数据 默认显示第一级数据
    $scope.parentId=0; //记录的是新建分类的父id
	$scope.entity1=null; //记录面包屑上的一级分类对象
	$scope.entity2=null; //记录面包屑上的二级分类对象
    $scope.setGrade=function(grade,pojo){
        if(grade==1){
            $scope.parentId=0
            $scope.entity1 = null;
            $scope.entity2 = null;
        }
    	if(grade==2){
            $scope.parentId=pojo.id;
            $scope.entity1 = pojo;
            $scope.entity2 = null;
		}
        if(grade==3){
            $scope.parentId=pojo.id;
            $scope.entity2 = pojo;
        }

        $scope.grade=grade;
	}

	$scope.findAllTypeTemplate=function () {
        typeTemplateService.findAll().success(function (response) {
			$scope.typeTemplateList = response;
        })
    }

    $scope.findByParentId=function (parentId) {
        itemCatService.findByParentId(parentId).success(function (response) {
			$scope.list=response;
        })
    }

    //读取列表数据绑定到表单中
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
		    // 追加parentId属性 并且赋值
            $scope.entity['parentId'] = $scope.parentId;
            serviceObject=itemCatService.add( $scope.entity  );//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
                    // $scope.reloadList();//重新加载
                    $scope.findByParentId($scope.parentId);
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
