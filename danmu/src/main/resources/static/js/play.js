$(function(){
    var winWidth = 0;
    var winHeight = 0;
    function findDimensions() //函数：获取尺寸
    {
        var left = 0,
            top = 0,
            width = 0,
            height = 0;
        //获取窗口宽度
        if (window.innerWidth)
            winWidth = window.innerWidth;
        else if ((document.body) && (document.body.clientWidth))
            winWidth = document.body.clientWidth;
        //获取窗口高度
        if (window.innerHeight)
            winHeight = window.innerHeight;
        else if ((document.body) && (document.body.clientHeight))
            winHeight = document.body.clientHeight;
        //通过深入Document内部对body进行检测，获取窗口大小
        if (document.documentElement  && document.documentElement.clientHeight && document.documentElement.clientWidth)
        {
            winHeight = document.documentElement.clientHeight;
            winWidth = document.documentElement.clientWidth;
        }
        left = parseInt(623/(1920/winWidth));
        top = parseInt(348/(1080/winHeight));
        width = parseInt(693/(1920/winWidth));
        height = parseInt(391/(1080/winHeight));
        //consol
        $('body').css({width:winWidth+'px',height:winHeight+'px'});
        $('.player').css({top:top+"px",left:left+'px',width:width+'px',height:height+'px'});
    }
    findDimensions();
    //调用函数，获取数值
    window.onresize=findDimensions;

})