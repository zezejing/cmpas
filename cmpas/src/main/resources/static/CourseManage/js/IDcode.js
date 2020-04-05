
function draw(IdCodeStr){

    /*
    * 确认好随机字符串数组，创建画布
    * */

    var canvas_width=document.getElementById('canvas').width;
    var canvas_height=document.getElementById('canvas').height;
    var canvas=document.getElementById('canvas');
    var context=canvas.getContext('2d');
    canvas.width = canvas_width;
    canvas.height=canvas_height;
    var sCode="a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,1,2,3,4,5,6,7,8,9,0";
    var aCode=sCode.split(',');
    var aLength=aCode.length;

    for(var i = 0;i < 4; i++){
        var j = Math.floor(Math.random() * aLength);
        var deg = Math.random()-0.5;//产生一个随机弧度;
        var txt = aCode[j]; //得到一个随机的内容
        IdCodeStr[i] = txt.toLowerCase();
        var x = 20 + i * 60; //文字在canvas上的x坐标
        var y = 100 + Math.random() * 8; // 文字在canvas上的y坐标
        context.font = "bold 100px 微软雅黑";

        context.translate(x,y);//初始化位置，角度
        context.rotate(deg);

        context.fillStyle = randomColor();
        context.fillText(txt,0,0);

        context.rotate(-deg); //还原位置，角度
        context.translate(-x,-y);
    }
    for (var i = 0; i <= 30; i++) { //验证码上显示线条
        context.strokeStyle = randomColor();
        context.beginPath();
        context.moveTo(Math.random() * canvas_width, Math.random() * canvas_height);
        context.lineTo(Math.random() * canvas_width, Math.random() * canvas_height);
        context.stroke();
    }
    for (var i = 0; i <= 1000; i++) { //验证码上显示小点
        context.strokeStyle = randomColor();
        context.beginPath();
        var x = Math.random() * canvas_width;
        var y = Math.random() * canvas_height;
        context.moveTo(x, y);
        context.lineTo(x + 1, y + 1);
        context.stroke();
    }
}

function randomColor(){
    var ColorStr="#007bff,#6610f2,#6f42c1,e83e8c,#dc3545,#fd7e14,#ffc107,#28a745,#20c997,#17a2b8";
    var ColorArray=ColorStr.split(',');
    var MyColor=ColorArray[Math.floor(Math.random()*10)];
    return MyColor;
}