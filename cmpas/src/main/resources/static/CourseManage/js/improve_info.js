/* 
* @Author: Marte
* @Date:   2019-06-03 11:38:58
* @Last Modified by:   Marte
* @Last Modified time: 2019-07-12 14:17:26
*/
var emailok = false;
var careerok = false;
var titleok = false;
var schoolok = false;
var institudeok = false;
$(document).ready(function(){

    //邮箱验证
    $('#email').blur(function(){
        var email = $('#email').val();
        var email_reg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
        if(email == null || email == "" || email == undefined ){
            $('#email_msg').html("邮箱不能为空！");
            return emailok = false;
        }else{
            if(!email_reg.test(email)){
                $('#email_msg').html("请输入正确的邮箱！");
                return emailok = false;
            }else{
                $('#email_msg').html("");
                return emailok = true;
            }
        }
    });

    //职业验证
    $('#career').blur(function() {
        var career = $('#career').val();
        var career_reg = /^[\u4e00-\u9fa5]+$/;
        if(career == null || career == "" || career == undefined){
            $('#career_msg').html("职业不能为空！");
            return careerok = false;
        }else{
            if(!career_reg.test(career)){
                $("#career_msg").html("职业必须为中文！");
                return careerok = false;
            }else{
                $("#career_msg").html("");
                return careerok = true;
            }
        }
    });

    //职称验证
    $('#title').blur(function() {
        var title = $('#title').val();
        var title_reg = /^[\u4e00-\u9fa5]+$/;
        if(title == null || title == "" || title == undefined){
            $('#title_msg').html("职称不能为空！");
            return titleok = false;
        }else{
            if(!title_reg.test(title)){
                $("#title_msg").html("职称必须为中文！");
                return titleok = false;
            }else{
                $("#title_msg").html("");
                return titleok = true;
            }
        }
    });

    //学校验证
    $('#school').blur(function() {
        var school = $('#school').val();
        var school_reg = /^[\u4e00-\u9fa5]+$/;
        if(school == null || school == "" || school == undefined){
            $('#school_msg').html("学校不能为空！");
            return schoolok = false;
        }else{
            if(!school_reg.test(school)){
                $("#school_msg").html("学校必须为中文！");
                return schoolok = false;
            }else{
                $("#school_msg").html("");
                return schoolok = true;
            }
        }
    });

    //学院验证
    $('#institude').blur(function() {
        var institude = $('#institude').val();
        var institude_reg = /^[\u4e00-\u9fa5]+$/;
        if(institude == null || institude == "" || institude == undefined){
            $('#institude_msg').html("学院不能为空！");
            return institudeok = false;
        }else{
            if(!institude_reg.test(institude)){
                $("#institude_msg").html("学院必须为中文！");
                return institudeok = false;
            }else{
                $("#institude_msg").html("");
                return institudeok = true;
            }
        }
    });

    $('#inprove_info').on('click', function() {
        //var login_form = $("#login_form").serialize();
        //alert("djhakjh");
        if (emailok&&careerok&&titleok&&schoolok&&institudeok) {
            var email = $('#email').val();
            var career = $('#career').val();
            var title = $('#title').val();
            var school = $('#school').val();
            var institude = $('#institude').val();
            $.ajax({
                type:"get",
                url:"/cmpas/user/improveInformation",
                xhrFields:{withCredentials:true},
                data:{"email":email,"career":career,"title":title,"school":school,"institude":institude},
                dataType:"Json",
                success:function(data){
                    // //注册成功的业务逻辑
                    // $.cookie('user_id', phone, { path: '/', expires: 365 });
                    alert(data.result);
                    if(data.result.indexOf('成功')>-1){
                        //完善成功的业务逻辑
                        window.location.href = './addNewCourse.html';
                    }else{
                        window.location.reload();
                    }
                },
                error: function(data) {
                    console.log("请检查网络");
                }
            });
        };
        
    });

});