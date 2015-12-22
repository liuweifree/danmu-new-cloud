$(function(){
    var ajaxLock = false,
        damuT = $('#danmuText'),
        timer = null,
        $color = '#ffffff',
        $expression = '';

    var str = '';
    var now = '';

    filter_time = function(){
        var time = setInterval(filter_staff_from_exist, 100);
        $(this).bind('blur',function(){
            clearInterval(time);
        });
    };
    $('.color-box p').on('click',function(){
        $color = $(this).attr('ac-color')
        $('.color-box').hide();
        $('.mask').hide();

        $(this).parent('li').siblings().find('div').hide();
        $(this).next('div').show();
        $('#colorBtn').css('color',$color)
    });
    filter_staff_from_exist = function(){
        now = $.trim(damuT.val());
        if (now != '' && now != str) {
            var len = 40 - now.length;
            if(len < 0){
                damuT.val(now.substr(0,40));
                ajaxLock = false
            }else{
                ajaxLock = true;
                //console.log(len)
                $('#textNum').text(len);
                $('.remind').hide();
            }
        }else if(now == ''){
            $('#textNum').text(40);
        }
        str = now;
    };

    damuT.bind('focus',filter_time);

    $('#danmuBtn').on('click',function(){
        if(damuT.val().length != 0){
            $('.remind').hide();
            $('.butt').attr('disabled',"disabled");
            if (!ajaxLock){
                $('.remind').show();
                clearTimeout(timer);
                timer = setTimeout(function(){
                    $('.remind').hide();
                },3000);
                damuT.val('');
                $('#danmuBtn').removeAttr('disabled');
            }else{
                $.ajax({
                    url:"/v1/api/danmu",
                    type:"post",
                    dataType:"json",
                    data:{
                        msg:damuT.val(),
                        color:$color
                    }
                }).done(function(data){

                    if(data.result == 200 || data.result == 403){
                        clearTimeout(timer);
                        $('.success,.mask1').show();
                        timer = setTimeout(function(){
                            $('.success,.mask1').hide();
                            $('#danmuBtn').removeAttr('disabled');
                        },2000);
                        damuT.val('');
                        $('.textNum').html('40');
                    }else if (data.result == 400){
                        clearTimeout(timer);
                        $('.networkWarning,.mask1').show();

                        damuT.val('');
                        $('.textNum').html('40');
                    }else{
                        $('.networkWarning,.mask1').show();
                        damuT.val('');
                        $('.textNum').html('40');
                    }
                }).fail(function(){
                    $('.networkWarning,.mask1').show();
                    damuT.val('');
                    $('.textNum').html('40');
                });
                $('#danmuBtn').removeAttr('disabled');
            };
        }else{
            $('.remind').show();
            clearTimeout(timer);
            timer = setTimeout(function(){
                $('.remind').hide();
            },3000)
        }

    });

    $('#reload,.cancel').on('click',function(){
        window.location.reload()
    })

    $('.expression-box img').on('click',function(){
        $('.confirm,.mask1').show();
        $(this).parent('li').siblings().find('div').hide();
        $(this).next('div').show();
        $expression = $(this).attr('ac-img');
        $('.mask').hide();
        $('.expression-box').hide();
        $('.determine').unbind('click').on('click',function() {
            $('.confirm,.mask1').hide();
            $.ajax({
                url: "/v1/api/expression",
                type: "post",
                dataType: "json",
                data: {
                    expression: $expression
                }
            }).done(function (data) {
                if (data.result == 200) {
                    $('.success,.mask1').show();
                    timer = setTimeout(function () {
                        $('.success,.mask1').hide();
                        $('#danmuBtn').removeAttr('disabled');
                    }, 2000);
                } else {
                    $('.networkWarning,.mask1').show();

                }
                ;

            });
        });
    });
    $('#colorBtn').on('click',function(){
        $('.mask').show();
        $('.color-box').show();
    });
    $('#expressionBtn').on('click',function(){
        $('.mask').show();
        $('.expression-box').show();
    });
    $('.mask').on('click',function(){
        $('.mask,.expression-box,.color-box').hide();
    });
    if (window.innerHeight)
        $('body,.bg,.cont').css('height',window.innerHeight);
    else if ((document.body) && (document.body.clientHeight))
        $('body,.bg,.cont').css('height',document.body.clientHeight);
});