/* 
* @Author: wangjing
* @Date:   2019-04-21 12:46:24
* @Last Modified by:   Marte
* @Last Modified time: 2019-07-15 12:01:43
*/

window.onload = function (){
    isLogined("/cmpas/user/isLogined");
    getAllLatestCourse("/cmpas/course/getAllLatest8Course");
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
            if(data.result == 'true'){
                $('#login').addClass('hidden');
                $('#headerphone').removeClass('hidden');
                $('#headerphone').addClass('show');
                getPerson_info("/cmpas/user/getMyInfo");
            }else{
                $('#login').removeClass('hidden');
                $('#headerphone').removeClass('show');
                $('#headerphone').addClass('hidden');
            }
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//获取个人信息
function getPerson_info(url){
    $.ajax({
        type:"get",
        url:url,
        xhrFields:{withCredentials:true},
        dataType:"Json",
        complete:function(data){
            var userList = data;
            $.each(userList,function(i,data){
                $('.currentName').text(data.nickname);
            });
        },
        error: function(data) {
            //console.log("请检查网络");
        }
    });
}
//查询最新的8个课程
function getAllLatestCourse(url){
     $.ajax({
        type:"get",
        url:url,
        xhrFields:{withCredentials:true},
        dataType:"Json",
        success:function(data){
            var courseList = data;
            $.each(courseList,function(i,data){
                //console.log(data);
                var prefix='/cmpas/uploadFile/downloadByURL?url=';
                var html = '<div class="square-Item" onclick="openCourse(this)" course_id="'+data.id+'">'+
                                '<a href="#" class="square-img" id="square-img">'+
                                    '<img src="'+prefix+data.icon+'" alt="" />'+
                                '</a>'+
                                '<div class="square-main">'+
                                    '<p class="task-hide">'+data.name+'</p>'
                                '</div>'+
                            '</div>';

                $('.square_list').append(html);
            });
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}
//点击首页最新课程判断是否登录
function openCourse(div){
    $.ajax({
        type:"get",
        url:"/cmpas/user/isLogined",
        xhrFields:{withCredentials:true},
        dataType:"Json",
        success:function(data){
            //alert(data.result);
            if(data.result=='true'){
                //window.location.href = "./course.html";
                isJoinCourse(div.getAttribute("course_id"));
            }else{
                alert("请先登录");
                window.location.href = "./login.html";
            }
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}
//判断是否加入了课程
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

var invitation_codeok = false;
$(document).ready(function(){
    
    //首页搜索效果
    $('#sear').mouseover(function() {
        $('#search').css('color', '#4cacff');
    }).mouseout(function (){  
        $('#search').css('color', '#fff');
    });

    //首页+效果
    $('#add').mouseover(function() {
        $('#addi').css('color', '#4cacff');
        $('.edu-menu-list').css('display', 'block');
    }).mouseout(function (){  
        $('#addi').css('color', '#fff');
        $('.edu-menu-list').css('display', 'none'); 
    });

    //登录后
    //首页搜索效果
    $('#sears').mouseover(function() {
        $('#searchs').css('color', '#4cacff');
    }).mouseout(function (){  
        $('#searchs').css('color', '#fff');
    });
    
    //头像效果
    $('#headerphone').mouseover(function() {
        $('.headSculpture-list').css('display', 'block');
    }).mouseout(function (){
        $('.headSculpture-list').css('display', 'none'); 
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

    $('#invitationCode').on('click', function() {
        if(invitation_codeok){
            
        }
    });


    $('#pageLimit').bootstrapPaginator({
        currentPage: 1,//当前的请求页面。
        totalPages: 5,//一共多少页。
        size:"normal",//应该是页眉的大小。
        bootstrapMajorVersion: 3,//bootstrap的版本要求。
        alignment:"right",
        numberOfPages:5,//一页列出多少数据。
        itemTexts: function (type, page, current) {//如下的代码是将页眉显示的中文显示我们自定义的中文。
            switch (type) {
            case "first": return "首页";
            case "prev": return "上一页";
            case "next": return "下一页";
            case "last": return "末页";
            case "page": return page;
            }
        }
    });
    
    $('#addNewCourse').on('click',function() {
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
                            alert("请先登录");
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

    $('#addClass').on('click',function() {
        $.ajax({
            type:"get",
            url:"/cmpas/user/isLogined",
            xhrFields:{withCredentials:true},
            dataType:"Json",
            success:function(data){
                if(data.result=='true'){
                    $('#invitationCode').on('click',function(){
                        var InvitationCode = $('#invitation_code').val()
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
                    alert("请先登录");
                    window.location.href = "./login.html";
                }
            },
            error: function(data) {
                console.log("请检查网络");
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