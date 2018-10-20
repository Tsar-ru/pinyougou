app.service("typeTemplateService",function($http){

    this.findSpecList=function(id){
        return $http.get("../typeTemplate/findSpecList/"+id);
    }
    this.findAll=function () {
        return $http.get("../typeTemplate/findAll");
    }

    this.search=function(pageNum,pageSize,searchEntity){
       //return $http.post("../typeTemplate/search?pageNum="+pageNum+"&pageSize="+pageSize,searchEntity);

        // restful风格
       return $http.post("../typeTemplate/search/"+pageNum+"/"+pageSize,searchEntity);
    }

    this.add=function (entity) {
        return $http.post("../typeTemplate/add",entity);
    }
    this.update=function (entity) {
        return $http.post("../typeTemplate/update",entity);
    }
    this.findOne=function (id) {
        return $http.get("../typeTemplate/findOne/"+id);
    }


})