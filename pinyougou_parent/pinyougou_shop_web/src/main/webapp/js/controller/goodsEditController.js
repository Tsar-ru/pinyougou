app.controller("goodsEditController",function ($scope,itemCatService,goodsService,uploadService,typeTemplateService) {

    // 初始化entity  entity.tbGoods.isEnableSpec
    $scope.entity={tbGoods:{isEnableSpec:'1'}, tbGoodsDesc:{itemImages:[],specificationItems:[]}};


    $scope.uploadFile=function () {
        uploadService.upload().success(function (response) {
            if(response.success){
               $scope.image.url = response.message; //带着图片的地址
            }else{
                alert(response.message);
            }

        })
    }
    $scope.addItemImages=function () {
        $scope.entity.tbGoodsDesc.itemImages.push($scope.image);
    }
    $scope.deleItemImages=function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index);
    }

    // 查询一级分类数据
    $scope.findCategory1List=function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.category1List=response;
        })
    }
    // $watch 相当于 onChange事件
    // 监听一级数据的变化，查询此一级分类下的二级分类数据
    // p1:要监听的数据    方法： p1:变化后的数据  p2:变化之前的数据
    $scope.$watch("entity.tbGoods.category1Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.category2List=response;
            $scope.category3List=[]; //一级发生变化 三级数据清空
            $scope.entity.tbGoods.typeTemplateId=null;
        })
    })

    // 监听二级数据的变化，查询此二级分类下的三级分类数据
    $scope.$watch("entity.tbGoods.category2Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.category3List=response;
        })
    })
    // 监听三级数据的变化，查询此三级分类对应的模板id
    $scope.$watch("entity.tbGoods.category3Id",function (newValue,oldValue) {
        // newValue代表是三级分类的id  根据分类id查询分类对象
        for (var i = 0; i < $scope.category3List.length; i++) {
            if(newValue == $scope.category3List[i].id){
               $scope.entity.tbGoods.typeTemplateId = $scope.category3List[i].typeId;
               break;
            }
        }
    })

    // 监听模板id的变化，根据模板id查询模板对象
    $scope.$watch("entity.tbGoods.typeTemplateId",function (newValue,oldValue) {
        // newValue代表是模板id
        typeTemplateService.findOne(newValue).success(function (response) {
            // response:一个模板对象
            $scope.brandList =JSON.parse( response.brandIds); //[{id:1,text:''}]
            $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems); //[{"text":"是否双层"},{"text":"是否带过滤网"}]
              //[{"text":"是否双层",value="},{"text":"是否带过滤网"}]
        })
            // 根据模板获取规格数据，但是从模板中获取的的数据格式是：
            // [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
            // 我们应该拼凑出这样的数据格式才能把规格和规格小项一起显示到页面上
            // [{"id":27,"text":"网络",options:[{id:1,optionName:'移动3G'},{},{}]},{"id":32,"text":"机身内存"}]
        typeTemplateService.findSpecList(newValue).success(function (response) {
            // response就是我们想要的数据格式
            $scope.specIdsWithOptions = response;
        })

    })

//key:水杯容量  value:1000ml  | key:水杯颜色  value:透明
    $scope.updateSpecificationItems=function(event,key,value){

// 假如目前的entity.tbGoodsDesc.specificationItems数据是[{"attributeName":"水杯容量","attributeValue":["1000ml"]}]
//     如果传来的值 key:水杯容量  value:800ml 我们想要的效果是  [{"attributeName":"水杯容量","attributeValue":["1000ml","800ml"]}]
//  需要判断 key:水杯容量 是否出现在大数组中
//          如果没有出现：向大数组中追加一个对象
        if(event.target.checked){  //勾选
            var specification  = specificationItemsIfHasAttributeName(key);
            if(specification==null){
                $scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":key,"attributeValue":[value]});
            }else{
                // 如果出现 应该是向水杯容量对应的对象的attributeValue中追加 value
                specification.attributeValue.push(value);
            }
        }else{  //取消勾选
            // key:网络  value:移动3G
            // [{"attributeName":"网络","attributeValue":["移动3G","移动4G"]},{"attributeName":"机身内存","attributeValue":["16G","32G"]}]
            var specification  = specificationItemsIfHasAttributeName(key);
            var index = specification.attributeValue.indexOf(value);
            specification.attributeValue.splice(index,1);
            // 判断对象中是否还有attributeValue
            if(specification.attributeValue.length==0){
                var _index =  $scope.entity.tbGoodsDesc.specificationItems.indexOf(specification);
                $scope.entity.tbGoodsDesc.specificationItems.splice(_index,1); //从大数组中移除对象
            }

        }

        // [{"attributeName":"水杯容量","attributeValue":["1000ml"]},{"attributeName":"水杯容量","attributeValue":["800ml"]},
        //     {"attributeName":"水平颜色","attributeValue":["透明"]},{"attributeName":"水平颜色","attributeValue":["蓝色"]}]
        createItemList();//产生sku列表

    }

    function createItemList(){
        var items = $scope.entity.tbGoodsDesc.specificationItems;
        $scope.entity.itemList=[{spec:{},price:0,num:9999,status:"1",isDefault:"0"}]
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
        }
    }
                                 //网络           [移动3G，移动4G]
    function addColumn(itemList,attributeName,attributeValue){
       var newItemList = [];
        for (var i = 0; i < itemList.length; i++) {
            // itemList[i].spec={网络：移动3G}
            var row = itemList[i];
            for (var j = 0; j < attributeValue.length; j++) {
                var newRow = JSON.parse(JSON.stringify(row)) ; //深克隆
                newRow.spec[attributeName] = attributeValue[j];
                newItemList.push(newRow);
            }
        }
        return newItemList;
    }

    function specificationItemsIfHasAttributeName(key){
       var items = $scope.entity.tbGoodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            if(items[i].attributeName ==key){
                return items[i];
            }
        }
        return null;
    }


    $scope.save=function () {
        // alert(editor.html());
        // 把 从富文本编辑器中取到的值放到 $scope.entity.goodsDesc.introduction中
        $scope.entity.tbGoodsDesc['introduction'] = editor.html();
        // $scope.entity 组合类 其中有三个表的信息
        goodsService.add($scope.entity).success(function (response) {
            if(response.success){
                location.href="goods.html";
            }else{
                alert(response.message);
            }
        })
    }
})