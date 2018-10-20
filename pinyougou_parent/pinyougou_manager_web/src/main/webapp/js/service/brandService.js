// service层不和html交互 只和后台交互  不用出现$scope  只出现$http
app.service("brandService",function($http){
         // 此方法是select2要求的  需要的格式 [{'id':1,'text':'aaaa'},{'id':2,'text':'bbbb'},{'id':3,'text':'cccc'}]
        this.findBrandList=function () {
            return $http.get("../brand/findBrandList");
        }
        this.findPage=function(pageNum,pageSize){
            return $http.get("../brand/findPage?pageNum="+pageNum+"&pageSize="+pageSize);
        }

        this.findAll=function () {
            return $http.get("../brand/findAll");
        }

        this.add=function (entity) {
            return  $http.post("../brand/add",entity);
        }

        this.update=function (entity) {
            return $http.post("../brand/update",entity);
        }

        this.findOne=function(id){
            return $http.get("../brand/findOne?id="+id);
        }

        this.dele=function(selectedIds){
            return    $http.get("../brand/dele?ids="+selectedIds);
        }

        this.search=function(pageNum,pageSize,searchEntity){
           return $http.post("../brand/search?pageNum="+pageNum+"&pageSize="+pageSize,searchEntity);
        }
    })