/**
 * Created by liye on 2017/3/28.
 */

// 指定图表的配置项和数据
var option = {
    tooltip: {
        axisPointer: {
            type: 'cross',
            label: {
                backgroundColor: '#283b56'
            }
        }
    },
    legend: {
        data:['当前温度', '当前湿度']
    },
    toolbox: {
        show: true,
        feature: {
            dataView: {readOnly: false},
            restore: {},
            saveAsImage: {}
        },
        right: 20
    },
    dataZoom: {
        show: true,
        start: 0,
        end: 100
    },
    xAxis: {
        type: 'category',
        axisLine: {
          show: false
        },
        data: []
    },
    yAxis: [
        {
            type: 'value',
            scale: true,
            name: '温度°C',
            boundaryGap: [0.2, 0.2]
        },
        {
            type: 'value',
            scale: true,
            name: '湿度%',
            boundaryGap: [0.2, 0.2]
        }
    ],
    series: [{
        name: '当前温度',
        type: 'line',
        yAxisIndex: 0,
        data: []
    },
    {
        name: '当前湿度',
        type: 'bar',
        yAxisIndex: 1,
        data: []
    }]
};


chart_init_arr = [];
for (var i = 0; i < $(".sensor-chart").length; i++){
    chart_init_arr.push(echarts.init(document.getElementById($(".sensor-chart")[i].id), 'shine'));
}


// 使用刚指定的配置项和数据显示图表。
chart_init_arr[0].setOption(option);
chart_init_arr[1].setOption(option);

setInterval(function (){
    $.ajax({
        type: "get",
        url: window.location.href + "/sensor/data",
        dataType: "json",
        success: function(sensor_data){
            var data0 = option.series[0].data;
            var data1 = option.series[1].data;
            data0.push((Math.random() * 10 + 5).toFixed(1) - 0);
            data1.push((Math.random() * 10 + 15).toFixed(1) - 5);
            option.xAxis.data.push(new Date().toLocaleTimeString());

            if (option.xAxis.data.length > 10){
                data0.shift();
                data1.shift();
                option.xAxis.data.shift();
            }

            chart_init_arr[0].setOption(option);

            $("#sensor-temp-1a96").text((Math.random() * 10 + 5).toFixed(1) + '°C');
            $("#sensor-humi-1a96").text((Math.random() * 10 + 15).toFixed(1) + '%');
        },
        error: function () {
            alert("LOAD DATA ERROR!");
        }
    });
}, 2000);