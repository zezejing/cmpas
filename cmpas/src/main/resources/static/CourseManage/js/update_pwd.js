/* 
* @Author: Marte
* @Date:   2019-06-02 17:03:13
* @Last Modified by:   Marte
* @Last Modified time: 2019-07-12 14:17:27
*/
var phoneok=false;
var VerificationCodeok = false;
var newpasswordok = false;
var newpasswordsok = false;
//手机号验证
function isPhoneNo(phone) {
    var pattern = /^1\d{10}$/;
    return pattern.test(phone);
}

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

$(document).ready(function(){

    //手机号验证
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

    //手机验证码
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

    //新密码验证
    $('#newpassword').blur(function(){
        var newpassword=$('#newpassword').val();
        var reg=/^[a-zA-Z0-9]{8,20}$/;
        if(newpassword == null || newpassword == ""||newpassword == undefined ){
            $('#newpasswordMs').html('新密码不能为空！');
            $('#newpasswordMs').css("color","red");
            return newpasswordok = false;
        }else{
            if(!reg.test(newpassword)){
                $('#newpasswordMs').html('请输入由字母数字组成的8-20位的字符密码！');
                $('#newpasswordMs').css("color","red");
                return newpasswordok = false;
            }else{
                $('#newpasswordMs').html("");
                return newpasswordok = true;
            }
        }
    });


    //确认新密码验证
    $('#newpasswords').blur(function(){
        var newpassword=$('#newpassword').val();
        var newpasswords=$("#newpasswords").val();
        if(newpassword==newpasswords){
            $('#newpasswordsMs').html("");
            return newpasswordsok = true;
        }else{
            $('#newpasswordsMs').html('两次密码不一致！');
            $('#newpasswordsMs').css("color","red");
            return newpasswordsok = false;
        }
    });

    $("#update_pwd").on('click',function()  {
        //获取页面数据
        var phone = $('#phone').val()
        var VerificationCode=$('#VerificationCode').val();
        var newpassword=$('#newpassword').val();

        if(phoneok&&VerificationCodeok&&newpasswordok&&newpasswordsok){
            $.ajax({
                type:"get",
                url:"/cmpas/user/retrieveByMessage",
                xhrFields:{withCredentials:true},
                data:{"phone":phone,"VerificationCode":VerificationCode,"newpassword":newpassword},
                dataType:"Json",
                success:function(data){
                    alert(data.result);
                    if (data.result.indexOf('成功')>-1){
                        window.open('./login.html','_self');
                    }
                },
                error: function(data) {
                    console.log("请检查网络");
                }
            });
        }
    });

});