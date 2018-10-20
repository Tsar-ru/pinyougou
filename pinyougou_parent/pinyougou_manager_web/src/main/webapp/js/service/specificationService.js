// service层不和html交互 只和后台交互  不用出现$scope  只出现$http
app.service("specificationService",function($http){

        this.findSpecList=function () {
            return $http.get("../specification/findSpecList");
        }
        this.findPage=function(pageNum,pageSize){
            return $http.get("../specification/findPage?pageNum="+pageNum+"&pageSize="+pageSize);
        }

        this.findAll=function () {
            return $http.get("../specification/findAll");
        }

        this.add=function (entity) {
            return  $http.post("../specification/add",entity);
        }

        this.update=function (entity) {
            return $http.post("../specification/update",entity);
        }

        this.findOne=function(id){
            return $http.get("../specification/findOne?id="+id);
        }

        this.dele=function(selectedIds){
            return    $http.get("../specification/dele?ids="+selectedIds);
        }

        this.search=function(pageNum,pageSize,searchEntity){
           return $http.post("../specification/search?pageNum="+pageNum+"&pageSize="+pageSize,searchEntity);
        }
    })