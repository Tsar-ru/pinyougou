app.service("uploadService",function ($http) {
    this.upload=function () {
        // 上传图片
        var formData = new FormData(); //html5  技术的对象
        //向formData对象中放入一个文件
        formData.append("file",file.files[0]);//file.files[0] 获取当前页面中的第一个文件
      return   $http({
          method:'post',
          url:'../upload/uploadFile',
          data:  formData,
          headers: {'Content-Type':undefined}, // form表单 enctype 一定是multipart/form-data
          transformRequest: angular.identity  //序列化传输的内容
        })

    }
})