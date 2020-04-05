/* 
* @Author: wangjing
* @Date:   2019-05-19 20:02:35
* @Last Modified by:   Marte
* @Last Modified time: 2019-07-12 14:17:27
*/



//var IdCodeStr=[];

//手机号验证
function isPhoneNo(phone) {
    var pattern = /^1\d{10}$/;
    return pattern.test(phone);
}

var nicknameok = false;
var realnameok = false;
var stuidok = false;
var passwordok = false;
var passwordoks = false;
var phoneok = false;
var VerificationCodeok = false;
$(document).ready(function(){
    
    /*draw(IdCodeStr);
    $('#canvas').click(function(){
        draw(IdCodeStr);
    });*/

    //昵称验证
    $('#nickname').blur(function(){
        var nickname = $('#nickname').val();
        if(nickname == null || nickname == "" || nickname == undefined){
            $('#nicknameMs').html("昵称不能为空!");
            $('#nicknameMs').css("color","red");
            nicknameok = false;
        }else{
            $('#nicknameMs').html("");
            nicknameok = true;
            return nicknameok;
        }
    });

    //真实姓名验证
    $('#realname').blur(function(){
        var realname = $('#realname').val();
        var userName_reg = /^[\u4e00-\u9fa5]+$/;
        if(realname == null || realname == "" || realname == undefined){
            $('#realnameMs').html("真实姓名不能为空!");
            $('#realnameMs').css("color","red");
            return realnameok = false;
        }else{
            if(!userName_reg.test(realname)){
                $("#realnameMs").html("真实姓名必须为中文！");
                return realnameok = false;
            }else{
                $("#realnameMs").html("");
                return realnameok = true;
            }
        }
    });

    //学号验证
    $('#stuid').blur(function(){
        var stuid = $('#stuid').val();
        var stuid = $('#stuid').val();
        var stuid_reg = /(^[1-9]\d*$)/;
        if(stuid == null || stuid == "" || stuid == undefined){
            $('#stuidMs').html("学号不能为空!");
            $('#stuidMs').css("color","red");
            return stuidok = false;
        }else{
            if(!stuid_reg.test(stuid)){
                $("#stuidMs").html("请输入正整数！");
                $('#stuidMs').css("color","red");
                return stuidok = false;
            }else{
                $("#stuidMs").html("");
                return stuidok = true;
            }
        }
    });
    //验证密码验证
    $('#password').blur(function(){
        var password=$('#password').val();
        var reg=/^[a-zA-Z0-9]{8,20}$/;
        var flag=reg.test(password);
        if(!flag){
            $('#passwordMs').html('请输入由字母数字组成的8-20位的字符密码！');
            $('#passwordMs').css("color","red");
            return passwordok = false;
        }else{
            $('#passwordMs').html("");
            return passwordok = true;
        }
    });

    //确认密码验证
    $('#passwords').blur(function(){
        var password=$('#password').val();
        var passwords=$("#passwords").val();
        if(password==passwords){
            $('#rePasswordMs').html("");
            return passwordoks = true;
        }else{
            $('#rePasswordMs').html('两次密码不一致！');
            $('#rePasswordMs').css("color","red");
            return passwordoks = false;
        }
    });

    //验证码验证
    /*$('#inputIdCode').blur(function(){
        var IdCodeInput=$('#inputIdCode').val().toLowerCase();
        var IdCodePreSet=IdCodeStr.join("");
        if(IdCodeInput==IdCodePreSet){
            $('#codeMs').html("");
            return true;
        }
        else{
            $('#codeMs').html('请输入验证码！');
            $('#codeMs').css("color","red");
            return false;
        }
    });*/

    //手机号
    var $phone_correct = false;
        $('#phone').blur(function(){
            if ($.trim($('#phone').val()).length == 0) {
            $('#phoneMs').html('请输入手机号码!');
            $('#phoneMs').css("color","red");
            return phoneok=false;
        } else {
            $('#phoneMs').html('');
            if (isPhoneNo($('#phone').val()) == false) {
                $('#phoneMs').html('请输入有效的11位手机号码！');
                $('#phoneMs').css("color","red");
                return phoneok=false;
            } else {
                $('#phoneMs').html('');
                $('.gain-code').attr("disabled",true); 
                $('.gain-code').css("background","#459be6");
                return phoneok=true;
            }
        }
    });
        
    $('#VerificationCode').blur(function(){
        var VerificationCode = $('#VerificationCode').val();
        if(VerificationCode == null || VerificationCode == "" || VerificationCode == undefined){
            $('#verifMs').html("验证码不能为空!");
            $('#verifMs').css("color","red");
            return VerificationCodeok=false;
        }else{
            $("#verifMs").html("");
            return VerificationCodeok=true;
        }
    });

    $("#register").on('click',function()  {
        //获取页面数据
        var nickname = $('#nickname').val();
        var realname = $('#realname').val();
        var stuid = $('#stuid').val();
        var gender = $('input:radio[name="gender"]:checked').val();
        var password = $('#password').val();
        var phone = $('#phone').val();
        var VerificationCode = $('#VerificationCode').val();
        if(nicknameok&&realnameok&&stuidok&&passwordok&&passwordoks&&phoneok&&VerificationCodeok){
            $.ajax({
                type:"get",
                url:"/cmpas/user/register",
                data:{"nickname":nickname,"realname":realname,"stuid":stuid,"gender":gender,"password":password,"phone":phone,"VerificationCode":VerificationCode},
                dataType:"Json",
                success:function(data){
                    alert(data.result);
                    if(data.result.indexOf("成功")>-1){
                        //注册成功的业务逻辑
                        setTimeout("location='./login.html'",2000);
                    }
                },
                error: function(data) {
                    alert("请检查网络");
                }
            });
        }
    });

    /*$("body").onkeydown(function() {
        if (event.keyCode==13) { 
            $('#register').click();
        } 
    });*/

});

//接收验证码
function get_verification_code() {
    var phone = $('#phone').val();
    $.ajax({
        type:"get",
        url:"/cmpas/message/sendVerificationMessage",
        data:{"phone":phone},
        dataType:"Json",
        success:function(data){
            $('#verifMs').html('发送成功！');
        }
    });
}
