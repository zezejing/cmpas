/* 
* @Author: Marte
* @Date:   2019-05-31 18:32:24
* @Last Modified by:   Marte
* @Last Modified time: 2019-07-14 11:39:48
*/
var gradeok = false;
var gauge_outfitok = false;
var student_rowok = false;
var achievementBasis_ok = false;
var full_creditok = false;
var weight_creditok = false;
var textareaok = false;
var ico_url = "";
$(document).ready(function(){

    //详情页面
    $(".white-panel li").click(function () {
        $(".white-panel li").removeClass("active");
        $(this).addClass("active");
        var n = $(this).attr("index");
        $(".edu-tab-con").addClass("undis");
        $("#edu-tab-con-" + n).removeClass("undis");
    })

    //提交作业的内容
    $('#textarea').blur(function(){
        var textarea = $('#textarea').val();
        if(textarea == null || textarea == ""||textarea == undefined ){
            $("#textarea_msg").html("提交的作业内容不能为空！");
            return textareaok = false;
        }else{
            $("#textarea_msg").html("");
            return textareaok = true;
        }
    });

    //作业打分验证
    $('#grade').blur(function(){
        var grade = $('#grade').val();
        var reg = /^([1]?\d{1,2})$/;
        if(grade == null || grade == ""||grade == undefined ){
            $("#grade_msg").html("分数不能为空！");
            return gradeok = false;
        }else{
            if(!reg.test(grade)){
                $("#grade_msg").html("请输入0-100以内的整数！");
                return gradeok = false;
            }else{
                $("#grade_msg").html("");
                return gradeok = true;
            }
        }
    });


    //表头所在行验证
    /*$('#gauge_outfit').blur(function(){
        var gauge_outfit = $('#gauge_outfit').val();
        if(gauge_outfit == null || gauge_outfit == ""||gauge_outfit == undefined ){
            $("#gauge_outfit_msg").html("表头所在行不能为空！");
            return gauge_outfitok = false;
        }else{
            $("#gauge_outfit_msg").html("");
            return gauge_outfitok = true;
        }
    });

    //学号所在列验证
    $('#student_row').blur(function(){
        var student_row = $('#student_row').val();
        if(student_row == null || student_row == ""||student_row == undefined ){
            $("#student_row_msg").html("学号所在列不能为空！");
            return student_rowok = false;
        }else{
            $("#student_row_msg").html("");
            return student_rowok = true;
        }
    });
    */
   
    //成绩依据名称验证
    $('#achievementBasis').blur(function(){
        var achievementBasis = $('#achievementBasis').val();
        if(achievementBasis == null || achievementBasis == ""||achievementBasis == undefined ){
            $("#achievementBasis_msg").html("成绩依据名称不能为空！");
            return achievementBasis_ok = false;
        }else{
            $("#achievementBasis_msg").html("");
            return achievementBasis_ok = true;
        }
    });
    
    //总分验证
    $('#full_credit').blur(function(){
        var full_credit = $('#full_credit').val();
        if(full_credit == null || full_credit == ""||full_credit == undefined ){
            $("#full_credit_msg").html("依据总分不能为空！");
            return full_creditok = false;
        }else{
            $("#full_credit_msg").html("");
            return full_creditok = true;
        }
    });

    //权重验证
    $('#weight_credit').blur(function(){
        var weight_credit = $('#weight_credit').val();
        if(weight_credit == null || weight_credit == ""||weight_credit == undefined ){
            $("#weight_credit_msg").html("依据权重不能为空！");
            return weight_creditok = false;
        }else{
            $("#weight_credit_msg").html("");
            return weight_creditok = true;
        }
    });

});

//结算成绩上传文档
function uploadExcel(){
    var fileAchieve = $('#fileAchieve').val();
    if(fileAchieve==''){
        alert('请先选择上传附件！');
    }else{
        var formData = new FormData();
        formData.append("file",$('#fileAchieve')[0].files[0]);
        formData.append('user_id',$.cookie('user_id'));
        $.ajax({
            url:'/cmpas/uploadFile/uploadFile', /*接口域名地址*/
            type:'post',
            data: formData,
            contentType: false,
            processData: false,
            complete:function(data){
                if(data.responseText.length==32){
                    alert("上传成功！");
                    ico_url = data.responseText;
                }
            }
        });
    }
}


//导入成绩
function importAchievementSubmit(){
    var url = ico_url;
    var head_row = $('#gauge_outfit').val();
    var stuid_line = $('#student_row').val();
    $.ajax({
        type:"get",
        url:"/cmpas/courseOuterData/addByURL",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id'),"url":url,"head_row":head_row,"stuid_line":stuid_line},
        dataType:"Json",
        success:function(data){
            //console.log(data);
            alert(data.result);
            if(data.result.indexOf("成功")){
                window.location.href = './achievementSet.html';
            }
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//选择数据表
function selectDataList(){
    if ($('#dataList option').length==0){
        $.ajax({
            type:"get",
            url:"/cmpas/courseOuterData/getAllOuterData",
            xhrFields:{withCredentials:true},
            data:{"course_id":$.cookie('course_id')},
            dataType:"Json",
            success:function(data){
                $('#dataList').empty();
                var dataList = data;
                $.each(dataList,function(i,data){
                    var html = "<option outer_data_id="+data.outer_data_id+" file_id="+data.file_id+">"+data.filename+"</option>";
                    $('#dataList').append(html);
                });
            },
            error: function(data) {
                console.log("请检查网络");
            }
        });
    }
}

//更新字段的select
function changeDataList(){
    selectDataFieldList();
}

//提交作业
function submitHomework(){
    //var content = UE.getEditor('myEditor').getPlainTxt();
    var content = $('#textarea').val().replace(/\n|\r\n/g,'<br>');
    if(content.length==0){
        $("#textarea_msg").html("提交的作业内容不能为空！");
    }else{
        $.ajax({
            type:"post",
            url:"/cmpas/submitTask/submitTask",
            xhrFields:{withCredentials:true},
            dataType:"Json",
            data:{"sectionId":GetQueryString("section_id"),"content":content},
            success:function(data){
                alert(data.result);
                if(data.result.indexOf("成功")>-1){
                    window.location.reload('./courseDetails.html');
                }
            },
            error: function(data) {
                console.log("请检查网络");
            }
        });
    }
    /*if(content.length<=10){
        $('#editorMs').html("提交作业的内容不能少于10字！");
    }else{
        $('#editorMs').html("");
        $.ajax({
            type:"post",
            url:"/cmpas/submitTask/submitTask",
            xhrFields:{withCredentials:true},
            dataType:"Json",
            data:{"sectionId":GetQueryString("section_id"),"content":content},
            success:function(data){
                alert(data.result);
                if(data.result.indexOf("成功")>-1){
                    window.location.reload('./courseDetails.html');
                }
            },
            error: function(data) {
                console.log("请检查网络");
            }
        });
    }*/
}

//获取数据字段
function selectDataFieldList(){
    if ($('#dataField option').length==0){
        var upload_file_id = $("select option:selected").attr("file_id");
        $.ajax({
            type:"get",
            url:"/cmpas/courseOuterData/getAllColumnName",
            xhrFields:{withCredentials:true},
            data:{"upload_file_id":upload_file_id},
            dataType:"Json",
            success:function(data){
                //console.log(data);
                $('#dataField').empty();
                var dataField = data;
                $.each(dataField,function(i,data){
                    var html = "<option>"+data+"</option>";
                    $('#dataField').append(html);
                });
            },
            error: function(data) {
                console.log("请检查网络");
            }
        });
    }
}
function GetQueryString(name) {
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  unescape(r[2]); return null;
}
//添加成绩依据
function addAchievement(){
    var  name= $('#achievementBasis').val();
    var full_credit = $('#full_credit').val();
    var weight_credit = $('#weight_credit').val();
    var dataFile = ico_url;
    console.log(achievementBasis_ok)
    if(achievementBasis_ok&&full_creditok&&weight_creditok){
        $.ajax({
            type:"get",
            url:"/cmpas/dependency/addScoreDependency",
            xhrFields:{withCredentials:true},
            data:{"courseId":GetQueryString("course_id"),"name":name,"fullScore":full_credit,"weight":weight_credit,"dataFile":dataFile},
            dataType:"Json",
            success:function(data){
                alert(data.result);
                if(data.result.indexOf('成功')>-1){
                    window.location.reload();
                }
            },
            error: function(data) {
                console.log("请检查网络");
            }
        });
    }
}

