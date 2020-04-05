/* 
* @Author: wangjing
* @Date:   2019-04-20 10:54:29
* @Last Modified by:   Marte
* @Last Modified time: 2019-07-12 14:17:27
*/
var phoneok=false;
var passwordok=false;
$(document).ready(function(){

    //手机号验证
    $('#phone').blur(function(){
        var phone = $('#phone').val();
        var telephone_reg = /^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\d{8}$/;
        if(phone == null || phone == "" || phone == undefined ){
            $('#telephoneMs').html("手机号不能为空!");
            $('#telephoneMs').css("color","red");
            phoneok=false;
        }else{
            if(!telephone_reg.test(phone)){
                $('#telephoneMs').html("请输入11位格式的手机号！");
                $('#telephoneMs').css("color","red");
                phoneok=false;
            }else{
                $('#telephoneMs').html("");
                phoneok=true;
            }
            return phoneok;
        }
    });
    //密码验证
    $('#password').blur(function(){
        var password = $('#password').val();
        var reg=/^[a-zA-Z0-9]{8,20}$/;
        if(password == null || password == "" || password == undefined){
            $('#pwdMs').html("密码不能为空!");
            $('#pwdMs').css("color","red");
            passwordok=false;
        }else{
            if(!reg.test(password)){
                $('#pwdMs').html('请输入8-20位的字符密码！');
                $('#pwdMs').css("color","red");
                passwordok=false;
            }else{
                $('#pwdMs').html("");
                passwordok=true;
            }
            return passwordok;
        }
    });

    $('#login').on('click', function() {
        //var login_form = $("#login_form").serialize();
        if (phoneok&&passwordok) {
            var phone = $("#phone").val();
            var password = $("#password").val();
            $.ajax({
                type:"get",
                url:"/cmpas/user/login",
                xhrFields:{withCredentials:true},
                data:{"phone":phone,"password":password},
                dataType:"Json",
                success:function(data){
                    setUserId();
                    alert(data.result);
                    // //注册成功的业务逻辑
                    // $.cookie('user_id', phone, { path: '/', expires: 365 });
                    if(data.result.indexOf('成功')>-1){
                        window.open("./index.html","_self");
                    }else{
                        window.open("./login.html","_self");
                    }
                },
                error: function(data) {
                    console.log("请检查网络");
                }
            });
        };
        
    });
    /*$("#password").onkeydown(function() {
        //alert("message");
        if (event.keyCode==13) { 
            $('#login').click();
        } 
    });*/
});

function setUserId(){
    $.ajax({
        type:"get",
        url:"/cmpas/user/getMyInfo",
        xhrFields:{withCredentials:true},
        dataType:"Json",
        success:function(data){
            $.cookie('user_id',data.id);
        },
        error: function(data) {
            //console.log("请检查网络");
        }
    });
}
