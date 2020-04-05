/* 
* @Author: wangjing
* @Date:   2019-05-28 16:01:02
* @Last Modified by:   Marte
*/

var classnameok = false;
var chapter_nameok = false;
var chapter_descriptionok = false;
var myEditorok = false;

function GetQueryString(name) {
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  decodeURI(r[2]); return null;
}

//返回
function goBack(){
    window.history.go(-1);
}
//点击结算成绩跳转页面
function achievementSet(){
    window.location.href='./achievementSet.html?course_id='+$.cookie('course_id');
}

window.onload = function (){
    isLogined("/cmpas/user/isLogined");
    if(window.location.href.indexOf('course.html')){
        getStudentNumByCourseId();
        getCourseById();
        getClassNumByCourseId();
        chapter_info();
        getAllClassByCourseId();
        recourceList();
        isMyCourseManage();
        isMyCourseStudy();
        isCourseHomework();
        getCourseScore();
    }
    if(window.location.href.indexOf('teacherDetails.html?section_id=')>-1 || window.location.href.indexOf('courseDetails.html?section_id=')>-1){
        getSectionById();
    }
    if(window.location.href.indexOf('courseDetails.html?section_id=')>-1){
        isCourseHomework();
        getTaskDetail();
    }
    if(window.location.href.indexOf('teacherDetails.html?section_id=')>-1){
        getAllSubmitTaskByCourseId();
        getAllClassByCourse();
    }
    if(window.location.href.indexOf('myCourse.html')){
        isMyCourseManage();
        isMyCourseStudy();
    }
    if(window.location.href.indexOf('correct_homework.html')){
        getTaskDetails();
        getTeacherTaskDetail();
        duplicationCheckRate();
    }
    if(window.location.href.indexOf('correct_details.html')){
        getSectionById();
        correctDetails();
    }
    if(window.location.href.indexOf('achievementSet.html')){
        achievementBasisList();
    }
    if(window.location.href.indexOf('detailAchievementBasic.html')){
        getCourseScoreDetail();
    }
}

//作业查重
function duplicationCheckRate(){
    $.ajax({
        type:"get",
        url:"/cmpas/submitTask/duplicateDetection",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id'),"section_id":GetQueryString("section_id"),"user_id":GetQueryString("user_id")},
        dataType:"text",
        async:false,
        success:function(data){
            $('#duplicationCheck').html(data);
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
} 

//导出成绩
function derivedItem(){
    window.open('/cmpas/courseScore/getCourseScoreExcel?course_id='+$.cookie('course_id'),'_self');
}

//根据学生id获取学生成绩依据详情
function getCourseScoreDetail(){
    $.ajax({
        type:"get",
        url:"/cmpas/courseScore/getCourseScoreDetail",
        xhrFields:{withCredentials:true}, 
        data:{"course_id":$.cookie('course_id'),"user_id":GetQueryString("user_id")},
        dataType:"Json",
        async:false,
        success:function(data){
            //console.log(data);
            $('#studentClass').html(GetQueryString("classname"));
            $('#studentName').html(GetQueryString("name"));
            var achieveBasicList = data;
            $.each(achieveBasicList,function(i,data){
                var html = '<tr>'+
                                '<td>'+(i+1)+'</td>'+
                                '<td>'+data.name+'</td>'+
                                '<td>'+data.self_score+'</td>'+
                                '<td>'+data.full_score+'</td>'+
                                '<td>'+data.weight+'</td>'+
                                '<td>'+data.final_score+'</td>'+
                            '</tr>';
                $('#achieveBasic_table_tbody').append(html);
            });
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//根据学生id、班级、姓名跳转页面
function viewDetails(user_id,classname,name){
    window.location.href = './detailAchievementBasic.html?user_id='+user_id+'&classname='+classname+'&name='+name;
}

//获取老师、学生所看的课程成绩列表
function getCourseScore(){
    $.ajax({
        type:"get",
        url:"/cmpas/courseScore/getCourseScore",
        xhrFields:{withCredentials:true}, 
        data:{"course_id":$.cookie('course_id')},
        dataType:"Json",
        async:false,
        success:function(data){
            //console.log(data);
            var courseList = data;
            $.each(courseList,function(i,data){
                var html = '<tr>'+
                                '<td>'+(i+1)+'</td>'+
                                '<td>'+data.name+'</td>'+
                                '<td>'+data.stuid+'</td>'+
                                '<td>'+data.classname+'</td>'+
                                '<td>'+data.score+'</td>'+
                                '<td>'+
                                    '<a href="#" class="btn btn-default" onclick="viewDetails('+data.user_id+',\''+data.classname+'\',\''+data.name+'\')">查看详情</a>'+
                                '</td>'+
                            '</tr>';
                $('#teacher_achieve_tbody').append(html);
            });
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//判断是否已经结算成绩
function isPublished(){
    var course_id = $.cookie("course_id");
    $.ajax({
        type:"get",
        url:"/cmpas/courseScore/isPublished",
        xhrFields:{withCredentials:true}, 
        data:{"course_id":course_id},
        dataType:"Json",
        success:function(data){
            //console.log(data);
            if(data == false){
                $('#lesson-saved-list-item table').remove();
                var html = '<img src="images/errors.jpg" height="300" width="534" alt="" />';
                $('#lesson-saved-list-item').empty();
                $('#lesson-saved-list-item').append(html);
                $('#derivedItem').remove();
            }
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//重置成绩
/*function resetResult(){
    var result = confirm("是否重置成绩！");
    if(result){
        $.ajax({
            type:"get",
            url:"/cmpas/courseScore/resetCourseScore",
            xhrFields:{withCredentials:true}, 
            data:{"course_id":GetQueryString("course_id")},
            dataType:"Json",
            async:false,
            success:function(data){
                alert(data.result);
                if(data.result.indexOf('成功')>-1){
                    window.location.reload('./achievementSet.html');
                }
            },
            error: function(data) {
                console.log("请检查网络");
            }
        });
    }
}*/

//发布成绩
function releaseResult(){
    $.ajax({
        type:"get",
        url:"/cmpas/courseScore/publishCourseScore",
        xhrFields:{withCredentials:true}, 
        data:{"course_id":GetQueryString("course_id")},
        dataType:"Json",
        async:false,
        success:function(data){
            alert(data.result);
            if(data.result.indexOf('成功')>-1){
                window.location.href = './course.html';
            }
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//导出实训任任务成绩
function derivationAchievement(){
    window.open('/cmpas/submitTask/getSubmitTaskScoreExcel?course_id='+GetQueryString("course_id"));
}

//获取结算成绩页面的成绩依据列表
function achievementBasisList(){
    $.ajax({
        type:"get",
        url:"/cmpas/dependency/getAllScoreDependency",
        xhrFields:{withCredentials:true}, 
        data:{"course_id":$.cookie('course_id')},
        dataType:"Json",
        async:false,
        success:function(data){
            //console.log(data);
            var achieveList = data;
            $.each(achieveList,function(i,data){
                var course_id = $.cookie("course_id");
                var html = '<tr>'+
                                '<td>'+(i+1)+'</td>'+
                                '<td>'+data.name+'</td>'+
                                '<td>'+data.fullScore+'</td>'+
                                '<td>'+data.weight+'</td>'+
                                '<td class="student_delete">'+
                                    '<a href="#" class="btn btn-default" onclick="achievementBasis('+data.course_id+','+data.id+')">删除</a>'+
                                '</td>'+
                            '</tr>';
                $('#achieve_table_tbody').append(html);
            });
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}
//删除成绩依据
function achievementBasis(course_id,dependency_id){
    var  result = confirm("是否确认删除该成绩依据");
    if(result){
        $.ajax({
            type:"get",
            url:"/cmpas/dependency/delScoreDependency",
            xhrFields:{withCredentials:true},
            dataType:"Json",
            data:{"course_id":$.cookie('course_id'),"dependency_id":dependency_id},
            success:function(data){
                alert(data.result);
                if(data.result.indexOf('成功')>-1){
                    window.location.reload('./achievementSet.html');
                }
            },
            error: function(data) {
                console.log("请检查网络");
            }
        });
    }
}

function correctDetails(){
    $.ajax({
        type:"get",
        url:"/cmpas/submitTask/getSubmitTaskBySectionId",
        xhrFields:{withCredentials:true},
        dataType:"Json",
        data:{"section_id":GetQueryString("section_id"),"user_id":GetQueryString("user_id")},
        success:function(data){
            //console.log(data); 
            $('#correct_grade').html(data.score);
            $('#correct_courseDetail').html(data.content);
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

function openCorrectDetails(user_id,section_id){
    window.location.href = './correct_details.html?user_id='+user_id+'&section_id='+section_id;
}
//批改作业
function correctHomeword(){
    var grade = $('#grade').val();
    if(grade == ''){
        $('#grade_msg').html("分数不能为空！");
    }else{
        var result = confirm("是否确认打分，打分后不能再修改！");
        if(result){
            $.ajax({
                type:"get",
                url:"/cmpas/submitTask/reviewSubmitTask",
                xhrFields:{withCredentials:true},
                dataType:"Json",
                data:{"section_id":GetQueryString("section_id"),"user_id":GetQueryString("user_id"),"score":grade},
                success:function(data){
                    //console.log(data.result);
                    alert(data.result);
                    if(data.result.indexOf('成功')>-1){
                        window.location.href = './teacherDetails.html?section_id='+GetQueryString("section_id");
                    }
                },
                error: function(data) {
                    console.log("请检查网络");
                }
            });
        }
    }
    
}

function getTaskDetails(){
    $.ajax({
        type:"get",
        url:"/cmpas/section/getSectionById",
        xhrFields:{withCredentials:true},
        dataType:"Json",
        data:{"section_id":GetQueryString("section_id")},
        success:function(data){
            $('#submit_homework_title').html(data.task);
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}
//老师批改页面详情
function getTeacherTaskDetail(){
    $.ajax({
        type:"get",
        url:"/cmpas/submitTask/getSubmitTaskBySectionId",
        xhrFields:{withCredentials:true},
        dataType:"Json",
        data:{"section_id":GetQueryString("section_id"),"user_id":GetQueryString("user_id")},
        success:function(data){
            //console.log(data);
            var submitTime = data.submitTime;
            var time = new Date(submitTime);
            var times=time.getFullYear() + '-' + (time.getMonth() + 1) + '-' + time.getDate()+ ' ' + time.getHours() + ':' + time.getMinutes() + ':' + time.getSeconds();
            $('#submit_homework_time').html(times);
            $('#submit_homework_content').html(data.content);
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//点击批改跳转
function openSubmitTaskDetails(user_id,section_id){
    window.location.href = './correct_homework.html?section_id='+section_id+'&user_id='+user_id;
}

//筛选作品状态的
function filterStatus(all){
    var result=[];
    //筛选代码
    var count=0;
    var radio_value = $("input:radio[name='status_radio']:checked").val();
    if(radio_value == 3){
        result = all;
    }else if(radio_value == 1){
        for(var i=0;i<all.length;i++){
            if(all[i].status==true){
                result[count]=all[i];
                count++;
            }
        }
    }else if(radio_value == 2){
        for(var i=0;i<all.length;i++){
            if(all[i].status==false){
                result[count]=all[i];
                count++;
            }
        }
    }
    return result;
}
//筛选班级的
function filterClass(all){
    var result=[];
    var classes=[];
    //筛选代码
    var count=0;
    var checkbox = $("input:checkbox[name='checkbox']:checked");
    for(var i=0;i<checkbox.length;i++){
        classes[i] = checkbox[i].value;
    }
    if(checkbox.length == 0){
        return all;
    }else{
        for(var i=0;i<all.length;i++){
            for(var j=0;j<classes.length;j++){
                if(all[i].classname == classes[j]){
                    result[count]=all[i];
                    count++;
                }
            }
        }
    }
    return result;
}
function renderTaskList(array){
    //遍历array渲染
    $('#task_tbody').empty();
    var top_index = 0;
    for(var i=0;i<array.length;i++){
        var eventClick = 'onclick="openSubmitTaskDetails('+array[i].user_id+','+(GetQueryString("section_id"))+')"';
        var a='<a href="#" class="btn btn-info" ';
        if (array[i].status) {
            a+=eventClick;
        }else
        {
            a+="disabled";
        }
        a+='>批改</a>';
        var b='<a href="#" class="btn btn-info" onclick="openCorrectDetails('+array[i].user_id+','+(GetQueryString("section_id"))+')">查看详情</a>';
        var html = '<tr>'+
                        '<td>'+(++top_index)+'</td>'+
                        '<td>'+array[i].name+'</td>'+
                        '<td>'+array[i].classname+'</td>'+
                        '<td>'+array[i].stuid+'</td>'+
                        '<td>'+(array[i].status?"已提交":"未提交")+'</td>'+
                        '<td>'+array[i].score+'</td>'+
                        '<td>'+(array[i].score>0?b:a)+'</td>'+
                    '</tr>';
        $('#task_tbody').append(html);
    }
}
function filterEvent(){
    var all=[];
    //拿到所有的作业信息
    $.ajax({
        type:"get",
        url:"/cmpas/submitTask/getAllSubmitTaskByCourseId",
        xhrFields:{withCredentials:true}, 
        data:{"course_id":$.cookie('course_id'),"section_id":GetQueryString("section_id")},
        dataType:"Json",
        async:false,
        success:function(data){
           all=data;
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
    all=filterStatus(all);
    all=filterClass(all);
    renderTaskList(all);
}

//获取所有班级情况
function getAllClassByCourse(){
    $.ajax({
        type:"get",
        url:"/cmpas/courseClass/getAllClassByCourseId",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id')},
        dataType:"Json",
        success:function(data){
            //console.log(data);
            var classList = data;
            $.each(classList,function(i,data){
                var html = '<span class="fl mr10 color-grey-8">'+
                                '<input type="checkbox" name="checkbox" class="magic-checkbox fl"  classId="'+data.id+'" id="class_checkbox_'+data.id+'" value="'+data.name+'" onclick="filterEvent()"/>'+
                                '<label  class="fl mr25" for="class_checkbox_'+data.id+'">'+data.name+'</label>'+
                            '</span>';
                $('#allClass').append(html);
            });
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//获取所有班级下面学生作业情况
function getAllSubmitTaskByCourseId(){
    var sum1=0;
    var sum2=0;
    $.ajax({
        type:"get",
        url:"/cmpas/submitTask/getAllSubmitTaskByCourseId",
        xhrFields:{withCredentials:true}, 
        data:{"course_id":$.cookie('course_id'),"section_id":GetQueryString("section_id")},
        dataType:"Json",
        async:false,
        success:function(data){
            //console.log(data);
            var taskList = data;
            $.each(taskList,function(i,data){
                if (data.status) {
                    sum1++;
                }else{
                    sum2++;
                }
                var eventClick = 'onclick="openSubmitTaskDetails('+data.user_id+','+(GetQueryString("section_id"))+')"';
                var a='<a href="#" class="btn btn-info" ';
                if (data.status) {
                    a+=eventClick;
                }else
                {
                    a+="disabled";
                }
                a+='>批改</a>';
                var b='<a href="#" class="btn btn-info" onclick="openCorrectDetails('+data.user_id+','+(GetQueryString("section_id"))+')">查看详情</a>';

                var html = '<tr>'+
                                '<td>'+(i+1)+'</td>'+
                                '<td>'+data.name+'</td>'+
                                '<td>'+data.classname+'</td>'+
                                '<td>'+data.stuid+'</td>'+
                                '<td>'+(data.status?"已提交":"未提交")+'</td>'+
                                '<td>'+data.score+'</td>'+
                                '<td>'+(data.score>0?b:a)+'</td>'+
                            '</tr>';
                $('#task_tbody').append(html);
            });
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
    $('#submited_label_1').text('已提交（'+sum1+'）');
    $('#submited_label_2').text('未提交（'+sum2+'）');
    $('#submited_label_3').text('全部（'+(sum1+sum2)+'）');

}
//获取作业详情
function getTaskDetail(){
    $.ajax({
        type:"get",
        url:"/cmpas/submitTask/getSubmitTaskBySectionId",
        xhrFields:{withCredentials:true},
        dataType:"Json",
        data:{"section_id":GetQueryString("section_id"),"user_id":$.cookie('user_id')},
        success:function(data){
            //console.log(data);
            $('#courseDetail').html(data.content);
            $('#submit_homework_title').html(data.content);
            if(data.reviewTime!=undefined && data.reviewTime.length>4){
                $('#grade').html(data.score);
            }else{
                $('#grade').html("未批阅");
                $('#score').remove();
                $('#divide').remove();
            }
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}
//判断是否提交了作业
function isCourseHomework(){
    $.ajax({
        type:"get",
        url:"/cmpas/submitTask/isSubmited",
        xhrFields:{withCredentials:true},
        dataType:"Json",
        data:{"section_id":GetQueryString("section_id")},
        success:function(data){
            //console.log(data);
            if(data == true){
                $('#edu-tab-nav-2').remove();
                $('#edu-tab-con-6').remove();
            }else{
                $('#edu-tab-nav-3').remove();
                $('#edu-tab-con-7').remove();
            }
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//判断是学习的课程
function isMyCourseStudy(){
    var callback;
    $.ajax({
        type:"get",
        url:"/cmpas/course/isMyCourse_Study",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id')},
        dataType:"Json",
        async:false,
        success:function(data){
            callback = data.result;
            if(data.result == 'ture'){
                $('#settlement_results').remove();
                $('#addChapter').remove();
                $('#addSection').remove();
                $('#add_resource').remove();
                $('#insert_class').remove();
                $('#delete_class').remove();
                $('.chapterNo').remove();
                $('.section_delete').remove();
                $('.student_delete').remove();
                $('.recource_delete').remove();
                $('#caozuo').remove();
            }
        },
        error: function(data){
            console.log("请检查网络");
        }
    });
    if (callback) {return callback};
}

//判断是管理的课程
function isMyCourseManage(){
    var callback;
    $.ajax({
        type:"get",
        url:"/cmpas/course/isMyCourse_Manage",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id')},
        dataType:"Json",
        async:false,
        success:function(data){
            callback = data.result;
            if(data.result == 'false'){
                $('#settlement_results').remove();
                $('#addChapter').remove();
                $('#addSection').remove();
                $('#add_resource').remove();
                $('#release_results').remove();
                $('#achievements_basis').remove();
                $('#import_achievements').remove();
                $('#insert_class').remove();
                $('#delete_class').remove();
                $('.chapterNo').remove();
                $('.section_delete').remove();
                $('.student_delete').remove();
                $('.recource_delete').remove();
                $('#caozuo').remove();
            }
        },
        error: function(data){
            console.log("请检查网络");
        }
    });
    if (callback) {return callback};
}

//获取资源列表
function recourceList(){
    $.ajax({
        type:"get",
        url:"/cmpas/course/getCourseResource",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id')},
        dataType:"Json",
        success:function(data){
            //console.log(data);
            var recourceList = data;
            $('#resource_list').empty();
            $.each(recourceList,function(i,data){
                var html = '<li>'+
                                '<a href="/cmpas/uploadFile/downloadByURL?url='+data.url+'">'+data.fileName+'</a>'+
                                '<a href="#" class="btn btn-primary fr recource_delete" onclick="delResourse(\''+data.url+'\','+$.cookie("course_id")+')">删除</a>'+
                            '</li>';
                $('#resource_list').append(html);
            });
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}
//删除课程资源的函数
function delResourse(url,course_id)
{
    $.ajax({
        type:"get",
        url:"/cmpas/course/delResource",
        xhrFields:{withCredentials:true},
        data:{"url":url,"course_id":course_id},
        dataType:"Json",
        success:function(data){
            //console.log(data);
           if (data)
           {
               alert("删除成功");
               recourceList();
           }
           else
           {
               alert("删除失败");
           }
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}
//删除学生
function deleteStudent(user_id,course_class_id){
    var  result = confirm("是否确认删除该学生");
    if(result){
        $.ajax({
            type:"get",
            url:"/cmpas/classJoin/deleteStudentById",
            xhrFields:{withCredentials:true},
            dataType:"Json",
            data:{"course_id":$.cookie('course_id'),"course_class_id":course_class_id,"user_id":user_id},
            success:function(data){
                alert(data.result);
                if(data.result.indexOf('成功')>-1){
                    window.location.reload('./course.html');
                }
            },
            error: function(data) {
                console.log("请检查网络");
            }
        });
    }
}

//根据节ID获取节所有的信息
function getSectionById(){
    $.ajax({
        type:"get",
        url:"/cmpas/section/getSectionById",
        xhrFields:{withCredentials:true},
        dataType:"Json",
        data:{"section_id":GetQueryString("section_id")},
        success:function(data){
            //console.log(data);
            $('#top_detail_section').text(data.name);
            $('#detail_section').text(data.name);
            $('#detail_section_content').text(data.instruction);
            $('#detail_section_description').text(data.task);
            $('#detail_description').text(data.task);
            $('#detailDescription').text(data.task);
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//点击查看详情判断是老师还是学生
function view_course_detail(section_id){
    $.ajax({
        type:"get",
        url:"/cmpas/course/isMyCourse_Manage",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id')},
        dataType:"Json",
        success:function(data){
            //alert(data.result);
            if(data.result=='true'){
                window.location.href = './teacherDetails.html?section_id='+section_id;
            }else{
                window.location.href = './courseDetails.html?section_id='+section_id;
            }
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}
//删除班级
function deleteCourseClassById(){
    var c = confirm("是否确认删除此班级？");
    if(c){
        $.ajax({
            type:"get",
            url:"/cmpas/courseClass/deleteCourseClassById",
            xhrFields:{withCredentials:true},
            data:{"course_id":$.cookie('course_id'),"course_class_id":$("#className").attr("class_id")},
            dataType:"Json",
            success:function(data){
                alert(data.result);
                if (data.result.indexOf('成功')>-1){
                    window.location.href = "./course.html";
                }else{
                    window.location.reload();
                }
            },
            error: function(data) {
                console.log("请检查网络");
            }
        });
    }
}

//删除课程章
function delete_chapter(chapter_id){
    var c = confirm("是否确认删除本章？");
    if(c){
        $.ajax({
            type:"get",
            url:"/cmpas/part/deletePartById",
            xhrFields:{withCredentials:true},
            data:{"part_id":chapter_id},
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
//删除课程节
function delete_section(section_id){
    var c = confirm("是否确认删除本节？");
    if(c){
        $.ajax({
            type:"get",
            url:"/cmpas/section/deleteSectionById",
            xhrFields:{withCredentials:true},
            data:{"id":section_id},
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
//course.html左边列表渲染
function render_operation_background(){
    $(".edu-class-leftnav li").click(function () {
        $(".edu-class-leftnav li").removeClass("active");
        $(this).addClass("active");
        var n = $(this).attr("index");
        $(".edu-tab-con").addClass("hidden");
        $("#edu-tab-con-" + n).removeClass("hidden");
        $('#banji').removeClass('hidden');
        $('#fenban').addClass('hidden');
    })

    $(".devide_class li").click(function () {
        $("#edu-tab-con-3").removeClass("hidden");
        $('#banji').addClass('hidden');
        $('#fenban').removeClass('hidden');
    })
}

//获取某个班级下学生信息的列表
function getCourseClassStudent(course_class_id){
    $.ajax({
        type:"get",
        url:"/cmpas/courseClass/getCourseClassStudent",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id'),"course_class_id":course_class_id},
        dataType:"Json",
        success:function(data){
            //console.log(data);
            var classList = data;
            $('#student_table_tbody').empty();
            $.each(classList,function(i,data){
                var html = '<tr>'+
                                '<td>'+(i+1)+'</td>'+
                                '<td>'+data.realname+'</td>'+
                                '<td>'+data.stuid+'</td>'+
                                '<td>'+data.course_class_name+'</td>'+
                                '<td class="student_delete">'+
                                    '<a href="#" class="btn btn-default" onclick="deleteStudent('+data.id+','+data.course_class_id+')">删除学生</a>'+
                                '</td>'+
                            '</tr>';
                $('#student_table_tbody').append(html);
            });
            $.ajax({
                type:"get",
                url:"/cmpas/courseClass/getAllClassByCourseId",
                xhrFields:{withCredentials:true},
                data:{"course_id":$.cookie('course_id')},
                dataType:"Json",
                success:function(data){
                    //console.log(data);
                    var classList = data;
                    $.each(classList,function(i,data){
                        if (data.id==course_class_id){
                            $('#className').text(data.name);
                            $('#invitationCodes').text(data.invitationCode);
                            $('#className').attr('class_id',data.id);
                        }
                    });
                },
                error: function(data) {
                    console.log("请检查网络");
                }
            });
            isMyCourseManage();
            isMyCourseStudy();
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//获取所有班级下学生信息的列表
function getAllCourseClassStudent(){
    $.ajax({
        type:"get",
        url:"/cmpas/courseClass/getAllCourseClassStudent",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id')},
        dataType:"Json",
        success:function(data){var stuid="";
            var classList = data;
            $('#student_table_tbody').empty();
            $.each(classList,function(i,data){/*
            stuid+=data.stuid+"\n";*/
                var html = '<tr>'+
                                '<td>'+(i+1)+'</td>'+
                                '<td>'+data.realname+'</td>'+
                                '<td>'+data.stuid+'</td>'+
                                '<td>'+data.course_class_name+'</td>'+
                                '<td class="student_delete">'+
                                    '<a href="#" class="btn btn-default" onclick="deleteStudent('+data.id+','+data.course_class_id+')">删除学生</a>'+
                                '</td>'+
                            '</tr>';
                $('#student_table_tbody').append(html);
            });
            isMyCourseManage();
            isMyCourseStudy();
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//获取本课程下的班级
function getAllClassByCourseId(){
    $.ajax({
        type:"get",
        url:"/cmpas/courseClass/getAllClassByCourseId",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id')},
        dataType:"Json",
        success:function(data){
            //console.log(data);
            var classList = data;
            $.each(classList,function(i,data){
                var html = '<li onclick="getCourseClassStudent('+data.id+')" class="clearfix">'+
                            '<a href="#" id="class_fl" class="fl">'+
                                '<span class="task-hide activity-left-name">'+data.name+'</span>'+
                            '</a>'+
                        '</li>';
                $('#devide_class').append(html);
            });
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });

    render_operation_background();
    setTimeout(function(){
        render_operation_background();
    },1000);
}

//新建班级
function createClass(){
    var name = $('#class_name').val();
    $.ajax({
        type:"get",
        url:"/cmpas/courseClass/create",
        xhrFields:{withCredentials:true},
        data: {"courseId":$.cookie('course_id'),"name":name},
        dataType:"Json",
        success:function(data){
            //console.log(data);
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

function render_section(){
    //章节列表鼠标放上去效果
    $(".paragraph").hover(function(){
        if($(this).find(".status_li a").length>0){
            $(this).find(".status_li a").show();
            $(this).find(".status_li i").hide();
            $(this).find(".status_li span").hide();
            $(this).css('background', '#B3DCFF');
        }
    },function(){
        $(this).find(".status_li a").hide();
        $(this).find(".status_li i").show();
        $(this).find(".status_li span").show();
        $(this).css('background', '');
    });
}
//获取节信息
function section_info(part_id){
    $.ajax({
        type:"get",
        url:"/cmpas/section/getAllSectionByPartId",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id'),"part_id":part_id},
        dataType:"Json",
        success:function(dataList){
            //console.log(dataList);
            var sectionList = dataList;
            $.each(sectionList,function(i,data){
                var html = '<div class="clearfix paragraph">'+
                                '<li class="fl li-width63">'+
                                    '<span class="progressRing mt7 mr10">'+
                                        '<i class="iconfont icon-bofang progressRing-part font-18"></i>'+
                                        '<em class="downline"></em>'+
                                    '</span>'+
                                    '<span class="paragraph_name color-grey3">'+data.name+'</span>'+
                                '</li>'+
                                '<li class="fr status_li">'+
                                    '<a href="#" onclick="view_course_detail('+data.id+')" class="btn_auto user_bluebg_btn fl none">查看详情</a>'+
                                    '<a href="#" onclick="delete_section('+data.id+')" class="btn_auto user_bluebg_btn fl none section_delete">删除</a>'+
                                '</li>'+
                            '</div>';
                $('#'+part_id).append(html);
            });
            isMyCourseManage();
            isMyCourseStudy();
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

//添加节
function createSection(){
    var partId = $('#chapterList').find("option:selected").attr("partId");
    var name = $('#section_name').val();
    var instruction = $('#section_content').val();
    var task = $('#section_description').val();
    $.ajax({
        type:"get",
        url:"/cmpas/section/create",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id'),"partId":partId,"name":name,"instruction":instruction,"task":task},
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

//获取所有的章列表
function getAllPartByCourseId(){
    
    if ($('#chapterList option').length==0){
        $.ajax({
            type:"get",
            url:"/cmpas/part/getAllPartByCourseId",
            xhrFields:{withCredentials:true},
            data:{"course_id":$.cookie('course_id')},
            dataType:"Json",
            success:function(data){
                $('#chapterList').empty();
                var chapterList = data;
                $.each(chapterList,function(i,data){
                    var html = "<option partId="+data.id+">"+data.name+"</option>";
                    $('#chapterList').append(html);
                });
            },
            error: function(data) {
                console.log("请检查网络");
            }
        });
    }
}

//获取章信息
function chapter_info(){
    $.ajax({
        type:"get",
        url:"/cmpas/part/getAllPartByCourseId",
        xhrFields:{withCredentials:true},
        data:{"course_id":$.cookie('course_id')},
        dataType:"Json",
        success:function(data){
            //console.log(data);
            var chapterLists = data;
            $.each(chapterLists,function(i,data){
                var html = '<div class="lesson-saved-list-item" id="'+data.id+'">'+
                                '<p class="clearfix title-line">'+
                                    '<span class="font-18 font-bd">'+data.name+'</span>'+
                                    '<a href="#" class="chapterNo fr" onclick="delete_chapter('+data.id+')" class="fr">删除</a>'+
                                    '<p class="part_description">'+data.introduction+'</p>'+
                                '</p>'+
                                '<div class="detail_for_paragraph clearfix"></div>'+
                            '</div>';
                $('#edu-tab-con-1').append(html);
                section_info(data.id);
            });
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
    render_section();
    setTimeout(function(){
        render_section();
    },1000);
}
//添加章
function createChapter(){
    var name = $('#chapter_name').val();
    var introduction = $('#chapter_description').val();
    if(chapter_nameok && chapter_descriptionok){
        $.ajax({
            type:"get",
            url:"/cmpas/part/create",
            xhrFields:{withCredentials:true},
            data:{'courseId':$.cookie('course_id'),'name':name,'introduction':introduction},
            dataType:"Json",
            success:function(data){
                //console.log(data.result);
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
//通过课程id获取课程的信息
function getCourseById(){
    $.ajax({
        type:"get",
        url:"/cmpas/course/getCourseById",
        xhrFields:{withCredentials:true},
        data:{'course_id':$.cookie('course_id')},
        dataType:"Json",
        success:function(data){
            //console.log(data);
            $('#header_name').text(data.name);
            $('#name').text(data.name);
            $('#introduction').text(data.introduction);
            $('#instruction').text(data.instruction);
            $('#cycle').text(data.cycle);
            $('#credit').text(data.credit);
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}
//查询课程下面有多少班级
function getClassNumByCourseId(){
    $.ajax({
        type:"get",
        url:"/cmpas/courseClass/getClassNumByCourseId",
        xhrFields:{withCredentials:true},
        data:{'course_id':$.cookie('course_id')},
        dataType:"Json",
        success:function(data){
            $('#class_num').text(data);
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}
//查询课程下面有多少学生
function getStudentNumByCourseId(){
    $.ajax({
        type:"get",
        url:"/cmpas/classJoin/getStudentNumByCourseId",
        xhrFields:{withCredentials:true},
        data:{'course_id':$.cookie('course_id')},
        dataType:"Json",
        success:function(data){
            //console.log(data);
            $('#student_num').text(data);
        },
        error: function(data) {
            console.log("请检查网络");
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
                getTeacherInfoByCourseId();
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

//获取课程创建者和学校
function getTeacherInfoByCourseId(){
    $.ajax({
        type:"get",
        url:"/cmpas/course/getTeacherInfoByCourseId",
        xhrFields:{withCredentials:true},
        async:false,
        data:{"course_id":$.cookie('course_id')},
        dataType:"Json",
        complete:function(data){
            var userList = data;
            $.each(userList,function(i,data){
                $('.currentName').text(data.name);
                $('#currentName').text(data.name);
                $('#teach_school').text(data.school);
            });
        },
        error: function(data) {
            console.log("请检查网络");
        }
    });
}

function getPerson_info(){
    $.ajax({
        type:"get",
        url:"/cmpas/user/getMyInfo",
        xhrFields:{withCredentials:true},
        dataType:"Json",
        complete:function(data){
            var userList = data;
            $.each(userList,function(i,data){
                $('.currentName').text(data.nickname);/*
                $('#currentName').text(data.nickname);
                $('#teach_school').text(data.school);*/
            });
        },
        error: function(data) {
            //console.log("请检查网络");
        }
    });
}

//添加资源
function addResource(urldata){
    $.ajax({
        type:"get",
        url:"/cmpas/course/addResource",
        xhrFields:{withCredentials:true},
        data:{'course_id':$.cookie("course_id"),'resourse_url':urldata},
        dataType:"Json",
        success:function(data){
                //window.location.reload();
        },
        error: function(data) {
            alert("添加资源失败！");
        }
    });
}
//上传单个文件
function uploadOneFile(i){
    var formData = new FormData();
    formData.append("file",$('#fileinput')[0].files[i]);
    formData.append("user_id",$.cookie('user_id'));
    $.ajax({
        url:'/cmpas/uploadFile/uploadFile', /*接口域名地址*/
        type:'post',
        data: formData,
        async: false,
        contentType: false,
        processData: false,
        xhrFields: {withCredentials: true},
        xhr:function(){                        
                myXhr = $.ajaxSettings.xhr();
                if(myXhr.upload){ 
                    myXhr.upload.addEventListener('progress',function(e){                            
                        var loaded = e.loaded;                  //已经上传大小情况 
                        var total = e.total;                      //附件总大小 
                        var percent = Math.floor(100*loaded/total)+"%";     //已经上传的百分比  
                        $(".upload_msg").text('正在上传第'+(i+1)+"个:"+percent);                       
                    }, false);
                }
                return myXhr;
            },
        success:function(res){
            console.log(res);
            $(".upload_msg").text("第"+(i+1)+'个文件上传完成');
            addResource(res+";");
        }
    });
}
//清空上传资源框
function clearUploadComponent() {
    $('#fileinput').val('');
    $('#resourse_msg').empty();
    $(".upload_msg").text('');
}
//上传资源文件点击事件
function uploadFile(){
    $(".upload_msg").text('');
    var files=$('#fileinput')[0].files;
    console.log("总共选择了"+files.length);
    for(var i=0;i<files.length;i++)
        uploadOneFile(i);
    alert("上传完成");
    $('#UploadComponentCancleButton').click();
    recourceList();
}


$(document).ready(function(){

    $(".edu-class-leftnav li").click(function () {
        $(".edu-class-leftnav li").removeClass("active");
        $(this).addClass("active");
        var n = $(this).attr("index");
        $(".edu-tab-con").addClass("hidden");
        $("#edu-tab-con-" + n).removeClass("hidden");
    })

    //上传资源
    var maxsize=1024*1024*100;//100MB
    var file=document.getElementById('fileinput');
    $('#fileinput').on('change',function() {
        $('#resourse_msg').empty();
        for(var i=0;i<file.files.length;i++){
            if(file.files[i].size>maxsize){
                $('#upload_msg').html("文件大小不能大于"+(maxsize/1024/1024)+"MB!请重选");
                //重置选择文件框
                $('#fileinput').val('');
                return true;
            }else{
                var name = "文件名:"+file.files[i].name;
                var size = "大小:"+(file.files[i].size/1024/1024).toFixed(2)+"MB";
                $('#resourse_msg').append("<li><span>"+name+"&nbsp;&nbsp;<span>"+size+"</span></span></li>");
            }
        }
    });

    //课程章节名称验证
    $('#chapter_name').blur(function(){
        var chapter_name = $('#chapter_name').val();
        if(chapter_name == null || chapter_name == ""||chapter_name == undefined ){
            $("#chapter_name_notice").html("章节名称不能为空！");
            return chapter_nameok = false;
        }else{
            $("#chapter_name_notice").html("");
            return chapter_nameok = true;
        }
    });
    //课程章节简介验证
    $('#chapter_description').blur(function(){
        var chapter_description = $('#chapter_description').val();
        if(chapter_description == null || chapter_description == ""||chapter_description == undefined ){
            $("#chapter_description_notice").html("章节简介不能为空！");
            return chapter_descriptionok = false;
        }else{
            $("#chapter_description_notice").html("");
            return chapter_descriptionok = true;
        }
    });
    //节名称验证
    $('#section_name').blur(function(){
        var section_name = $('#section_name').val();
        if(section_name == null || section_name == ""||section_name == undefined ){
            $("#section_name_notice").html("节名称不能为空！");
            return false;
        }else{
            $("#section_name_notice").html("");
            return true;
        }
    });
    //节简介验证
    $('#section_content').blur(function(){
        var section_content = $('#section_content').val();
        if(section_content == null || section_content == ""||section_content == undefined ){
            $("#section_content_notice").html("节简介不能为空！");
            return false;
        }else{
            $("#section_content_notice").html("");
            return true;
        }
    });
    //节题目验证
    $('#section_description').blur(function(){
        var section_description = $('#section_description').val();
        if(section_description == null || section_description == ""||section_description == undefined ){
            $("#section_description_notice").html("章节简介不能为空！");
            return false;
        }else{
            $("#section_description_notice").html("");
            return true;
        }
    });

    //节名称验证
    $('#section_name').blur(function(){
        var section_name = $('#section_name').val();
        if(section_name == null || section_name == ""||section_name == undefined ){
            $("#section_name_notice").html("节名称不能为空！");
            return false;
        }else{
            $("#section_name_notice").html("");
            return true;
        }
    });

    //实训题目验证
    $('#section_description').blur(function(){
        var section_description = $('#section_description').val();
        if(section_description == null || section_description == ""||section_description == undefined ){
            $("#section_description_notice").html("实训题目不能为空！");
            return false;
        }else{
            $("#section_description_notice").html("");
            return true;
        }
    });


    var chapter_name = $("#chapter_name").val();
    var chapter_description = $('#chapter_description').val();
    var chapterList = $('#chapterList option:selected').val();
    var section_name = $('#section_name').val();
    var section_description = $('#section_description').val();

    var jsonStr="{chapter_name:"+chapter_name+"," +
            "chapter_description:'"+chapter_description+"'}";
    var jsonSection="{chapterList:"+chapterList+","+
            "section_name:'"+section_name+","+
            "section_description:'"+section_description+"'}";

    //添加章信息
    $("#chapterSubmit").on('click',function() {
        //var new_course = $("#new_course").serialize();
        $.ajax({
            type:"post",
            dataType:"Json",
            url:"http://localhost:8080/CourseManage/new_course",
            //data:JSON.stringify(new_course),
            data: {RequestStr:jsonStr},
            /*error: function(data) {
                alert("请检查网络！");
            },*/
            success:function(data){
                setTimeout("location='http://localhost:8080/CourseManage/course.html'",2000);  
            }
        });
    });
    //添加章信息
    $("#sectionSubmit").on('click',function() {
        //var new_course = $("#new_course").serialize();
        //alert("asbajsfjka");
        $.ajax({
            type:"post",
            dataType:"Json",
            url:"http://localhost:8080/CourseManage/new_course",
            //data:JSON.stringify(new_course),
            data: {RequestStr:jsonSection},
            /*error: function(data) {
                alert("请检查网络！");
            },*/
            success:function(data){
                setTimeout("location='http://localhost:8080/CourseManage/course.html'",2000);  
            }
        });
    });

    //新建班级验证
    $('#class_name').blur(function() {
        var class_name = $('#class_name').val();
        var reg = /^[\u4e00-\u9fa5]+$/;
        if(class_name == null || class_name == "" || class_name == undefined){
            $('#class_name_msg').html("班级不能为空!");
            $('#class_name_msg').css("color","red");
            return classnameok = false;
        }else{
            $("#class_name_msg").html("");
            return classnameok = true;
        }
    }); 
});