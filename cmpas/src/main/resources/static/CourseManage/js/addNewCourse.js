/* 
* @Author: wangjing
* @Date:   2019-05-19 20:02:35
* @Last Modified by:   Marte
* @Last Modified time: 2019-07-15 13:06:19
*/
var nameok = false;
var introductionok = false;
var instructionok = false;
var cycleok = false;
var creditok = false;
var invitation_codeok = false;
var ico_url = "d7b36073fe0e31683aeabf8c5fc2f8c7";
window.onload = function (){
    isLogined("/cmpas/user/isLogined");
    selectAllManage();
    selectAllStudy();
}

function isJoinCourse(course_id){
    //alert(course_id);
    $.ajax({
        type:"get",
        url:"/cmpas/course/isLinked",
        data:{"course_id":course_id},
        xhrFields:{withCredentials:true},
        dataType:"Json",
        success:function(data){
            //alert(data.result);
            if(data){
                $.cookie('course_id',course_id);
                window.location.href = "./course.html";
            }else{
                alert("请加入课程");
            }
        },
        beforeSend: function(request){
            $.cookie("course_id",)
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//查询课程下面有多少学生
function getStudentNumByCourseId(course_id){
    var callback;
    $.ajax({
        type:"get",
        url:"/cmpas/classJoin/getStudentNumByCourseId",
        xhrFields:{withCredentials:true},
        data:{'course_id':course_id},
        dataType:"Json",
        async:false,
        complete:function(data){
            //console.log(data);
            callback=data.responseText;
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
    if (callback) {return callback};
}

//获取课程创建者和学校
function getTeacherInfoByCourseId(course_id){
    var callback;
    $.ajax({
        type:"get",
        url:"/cmpas/course/getTeacherInfoByCourseId",
        xhrFields:{withCredentials:true},
        async:false,
        data:{"course_id":course_id},
        dataType:"Json",
        success:function(data){
            callback=data;
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
    if (callback) {return callback};
}
//获取我学习的课程
function selectAllStudy(){
    $.ajax({
        type:"get",
        url:"/cmpas/course/selectALL_Study",
        xhrFields:{withCredentials:true},
        dataType:"Json",
        success:function(data){
            var studyList = data;
            $.each(studyList,function(i,data){
                var teacherInfo = getTeacherInfoByCourseId(data.id);
                var personInfo = getStudentNumByCourseId(data.id);
                var html = '<div class="square-Item" style="margin-top: 25px;" onclick="isJoinCourse('+data.id+')">'+
                                '<div class="substance">'+
                                    '<p class="subName font-16"><span>'+data.name+'</span></p>'+
                                    '<span><img alt="用户" class="radius mt15" height="60" src="images/headSculpture.png" width="60">'+
                                    '</span>'+
                                    '<p class="font-14 mt10"><span>'+teacherInfo.name+'</span></p>'+
                                    '<p class="font-16 mb15 task-hide"><span class="color-grey-98" id="study_school">'+teacherInfo.school+'</span></p>'+
                                '</div>'+
                                '<div class="edu-txt-center course-bottom">'+
                                    '<div class="inline color-grey-6">'+
                                        '<span class="fl ml10 mr10 squareIconSpan" id="study_personnel">'+
                                            '<i class="iconfont icon-chengyuan fl mr3" data-tip-down="成员">'+personInfo+
                                            '</i>'+
                                        '</span>'+
                                    '</div>'+
                                '</div>'
                            '</div>';
                $('#myStudy').append(html);
            });
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}
//获取我管理的课程
function selectAllManage(){
    $.ajax({
        type:"get",
        url:"/cmpas/course/selectALL_Manage",
        xhrFields:{withCredentials:true},
        dataType:"Json",
        success:function(data){
            var manageList = data;
            $.each(manageList,function(i,data){
                var teacherInfo=getTeacherInfoByCourseId(data.id);
                var personInfo = getStudentNumByCourseId(data.id);
                var html = '<div class="square-Item" style="margin-top: 25px;" onclick="isJoinCourse('+data.id+')">'+
                                '<div class="substance">'+
                                    '<p class="subName font-16"><span>'+data.name+'</span></p>'+
                                    '<span><img alt="用户" class="radius mt15" height="60" src="images/headSculpture.png" width="60">'+
                                    '</span>'+
                                    '<p class="font-14 mt10"><span>'+teacherInfo.name+'</span></p>'+
                                    '<p class="font-16 mb15 task-hide"><span class="color-grey-98">'+teacherInfo.school+'</span></p>'+
                                '</div>'+
                                '<div class="edu-txt-center course-bottom">'+
                                    '<div class="inline color-grey-6">'+
                                        '<span class="fl ml10 mr10 squareIconSpan">'+
                                            '<i class="iconfont icon-chengyuan fl mr3" data-tip-down="成员">'+personInfo+
                                            '</i>'+
                                        '</span>'+
                                    '</div>'+
                                '</div>'+
                            '</div>';
                $('#myManage').append(html);
            });
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}
//新建课程提交事件
function submitCourse(){
    var name = $("#name").val();
    var icon = ico_url;
    var introduction = $('#introduction').val();
    var instruction = $('#instruction').val();
    var cycle = $("#cycle").val();
    var credit = $("#credit").val();
    if(nameok&&introductionok&&instructionok&&cycleok&&creditok){
        $.ajax({
            type:"get",
            url:"/cmpas/course/create",
            xhrFields:{withCredentials:true},
            data: {"name":name,"icon":icon,"introduction":introduction,"instruction":instruction,"cycle":cycle,"credit":credit},
            dataType:"Json",
            success:function(data){
                alert(data.result);
                if(data.result.indexOf('成功')>-1){
                    window.location.href = "./myCourse.html";
                }else{
                    window.location.reload();
                }
            },
            error: function(data) {
                console.log("请检查网络！");
            },
            
        });
    }
}

//上传课程图片
function upload_icon(){
    var formData = new FormData();
    formData.append("file",$('#file')[0].files[0]);
    formData.append('user_id',$.cookie('user_id'));
    $.ajax({
        url:'/cmpas/uploadFile/uploadFile', /*接口域名地址*/
        type:'post',
        data: formData,
        contentType: false,
        processData: false,
        complete:function(data){
            //console.log(data.responseText);
            if(data.responseText.length==32){
                alert("上传成功！");
                ico_url = data.responseText;
            }
        }
    });
}

//封装判断是否登录的ajax方法
function isLogined(url){
    $.ajax({
        type:"get",
        url:url,
        xhrFields:{withCredentials:true},
        dataType:"Json",
        success:function(data){
            console.log(data.result);
            if(data.result=='true'){
                getPerson_info();
            }else{
                window.location.href = "./index.html";
            }
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//获取个人信息
function getPerson_info(){
    $.ajax({
        type:"get",
        url:"/cmpas/user/getMyInfo",
        xhrFields:{withCredentials:true},
        dataType:"Json",
        complete:function(data){
            //console.log(data)
            var userList = data;
            $.each(userList,function(i,data){
                $('.currentName').text(data.nickname);
                $('#currentName').text(data.nickname);
                $('#teach_school').text(data.school);
            });
        },
        error: function(data) {
            //console.log("请检查网络");
        }
    });
}

//提交新建课程
$(document).ready(function(){

    //首页+效果
    $('#add').mouseover(function() {
        $('#addi').css('color', '#4cacff');
        $('.edu-menu-list').css('display', 'block');
    }).mouseout(function (){  
        $('#addi').css('color', '#fff');
        $('.edu-menu-list').css('display', 'none'); 
    });

    //头像效果
    $('#headerphone').mouseover(function() {
        $('.headSculpture-list').css('display', 'block');
    }).mouseout(function (){
        $('.headSculpture-list').css('display', 'none'); 
    });
    
    /*$('#new_course_end_date').dcalendarpicker({
        format:'yyyy-mm-dd'
    });*/
    $("#file").fileinput({
        language: 'zh', //设置语言
        allowedFileExtensions: ['jpg', 'png','gif','txt','zip','ico','jpeg','js','css','java','mp3','mp4','doc','docx'],//接收的文件后缀
        showUpload: false,
        dropZoneEnabled: false,
        maxFileCount: 10,
        mainClass: "input-group-lg",
    });

    //课程名称验证
    $('#name').blur(function(){
        var name = $('#name').val();
        var name_msg_reg = /^[a-zA-Z\u4e00-\u9fa5]+$/; 
        if(name == null || name == ""||name == undefined ){
            $("#name_msg").html("课程名称不能为空！");
            return nameok = false;
        }else{
            if(!name_msg_reg.test(name)){
                $("#name_msg").html("课程名称必须为中英文！");
                return nameok = false;
            }else{
                $("#name_msg").html("");
                return nameok = true;
            }
        }
    });

    //课程图片验证
    /*$('#file').blur(function(){
        var course_file = $('#file').val();
        //alert(course_file);
        if(course_file == 0){
            alert("请选择文件！");
        }
        if(course_file == null || course_file == ""||course_file == undefined ){
            $("#course_file_notice").html("课程图片不能为空！");
            return false;
        }else{
            $("#course_file_notice").html("");
            return true;
        }
    });*/

    //课程简介验证
    $('#introduction').blur(function(){
        var introduction = $('#introduction').val();
        if(introduction == null || introduction == ""||introduction == undefined ){
            $("#introduction_msg").html("课程简介不能为空！");
            return introductionok = false;
        }else{
            $("#introduction_msg").html("");
            return introductionok = true;
        }
    });

    //学习须知验证
    $('#instruction').blur(function(){
        var instruction = $('#instruction').val();
        if(instruction == null || instruction == ""||instruction == undefined ){
            $("#instruction_msg").html("学习须知不能为空！");
            return instructionok = false;
        }else{
            $("#instruction_msg").html("");
            return instructionok = true;
        }
    });

    //总学时验证
    $('#cycle').blur(function(){
        var cycle = $('#cycle').val();
        var reg = /(^[1-9]\d*$)/;
        if(cycle == null || cycle == ""||cycle == undefined){
            $("#cycle_msg").html("总学时不能为空！");
            return cycleok = false;
        }else{
            if(!reg.test(cycle)&&cycle>0){
                //alert("请输入正整数！");
                $("#cycle_msg").html("请输入正整数！");
                return cycleok = false;
            }else{
                $("#cycle_msg").html("");
                return cycleok = true;
            }
        }

        
    });

    //学分验证
    $('#credit').blur(function(){
        var credit = $('#credit').val();
        var reg = /^[0-9]+([.]{1}[0-9]{1})?$/; 
        if(credit == null || credit == ""){
            $("#credit_msg").html("学分不能为空！");
            return creditok = false;
        }else{
            if(!reg.test(credit)&&credit>0){
                //alert("请输入正整数！");
                $("#credit_msg").html("请输入正整数或保留一位小数的浮点数！");
                return creditok = false;
            }else{
                $("#credit_msg").html("");
                return creditok = true;
            }
        }
    });

    //班级邀请码验证
    $('#invitation_code').blur(function() {
        var invitation_code = $('#invitation_code').val();
        if(invitation_code == null || invitation_code == ""||invitation_code == undefined ){
            $("#invitation_code_msg").html("班级邀请码不能为空！");
            return invitation_codeok = false;
        }else{
            $("#invitation_code_msg").html("");
            return invitation_codeok = true;
        }
    });

    $('#addNewCourse').click(function() {
        $.ajax({
            type:"get",
            url:"/cmpas/user/isLogined",
            xhrFields:{withCredentials:true},
            dataType:"Json",
            success:function(data){
                if(data.result=='true'){
                    $.ajax({
                        type:"get",
                        url:"/cmpas/user/isTeacher",
                        xhrFields:{withCredentials:true},
                        dataType:"Json",
                        success:function(data){
                            if(data.result=='true'){
                                window.location.href = "./addNewCourse.html";
                            }else{
                                alert("请完善信息！");
                                window.location.href = "./improve_info.html";
                            }
                        },
                        error: function(data) {
                            alert("请检查网络");
                        }
                    });
                }else{
                    window.location.href = "./login.html";
                }
            },
            error: function(data) {
                alert("请检查网络");
            }
        });
    });

    $('#addClass').click(function() {
        $.ajax({
            type:"get",
            url:"/cmpas/user/isLogined",
            xhrFields:{withCredentials:true},
            dataType:"Json",
            success:function(data){
                if(data.result=='true'){
                    $('#invitationCode').on('click',function(){
                        var InvitationCode = $('#invitation_code').val();
                        $.ajax({
                            url: '/cmpas/classJoin/joinClass',
                            type: 'get',
                            dataType: 'Json',
                            data: {"InvitationCode": InvitationCode},
                            xhrFields:{withCredentials:true},
                            success:function(data){
                                alert(data.result);
                                if (data.result.indexOf('成功')>-1){
                                    window.location.href = "./myCourse.html";
                                }else{
                                    window.location.reload();
                                }
                            },
                            error: function(data) {
                                console.log("请检查网络");
                            }
                        });
                    });
                }else{
                    window.location.href = "./login.html";
                }
            },
            error: function(data) {
                alert("请检查网络");
            }
        });
    });

    //课程图片上传
    $('.kv-fileinput-upload').on('click', function() {
        var uploading = false;
        if(uploading){
            alert("文件正在上传中，请稍候");
        }
        var formData = new FormData();
        formData.append("file",$('#file')[0].files[0]);
        formData.append('user_id',$.cookie('user_id'));
        $.ajax({
            url:'/cmpas/uploadFile/uploadFile', /*接口域名地址*/
            type:'post',
            data: formData,
            contentType: false,
            processData: false,
            complete:function(data){
                if(data.responseText.length == 32){
                    alert("上传成功！");
                    ico_url = data.responseText;
                }
            }
        });
    });
    
    //退出登录
    $('#logout').on('click', function(event) {
        $.ajax({
            type:"get",
            url:"/cmpas/user/logout",
            xhrFields:{withCredentials:true},
            dataType:"Json",
            success:function(data){
                window.location.href = "./index.html";
            }
        });
    });

});

