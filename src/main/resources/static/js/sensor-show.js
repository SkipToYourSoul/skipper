/**
 * Created by liye on 2017/3/28.
 */

$._messengerDefaults = {
    extraClasses: 'messenger-fixed messenger-theme-future messenger-on-top'
};

//info success error
function message_info(text, type, hide) {
    Messenger().post({
        message: text,
        type: type,
        hideAfter: hide,
        hideOnNavigate: true,
        showCloseButton: true
    });
}

// initial charts
var theme = ['shine', 'roma', 'infographic', 'vintage'];
var sensor_option = {};
for (var i = 0; i < $(".sensor-chart").length; i++){
    var id = $(".sensor-chart")[i].id;
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
                magicType : {show: true, type: ['line', 'bar']},
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
            splitLine: {
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
                name: '湿度%RH',
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
            type: 'line',
            yAxisIndex: 1,
            data: []
        }]
    };
    var chart = echarts.init(document.getElementById($(".sensor-chart")[i].id), theme[i%4]);
    sensor_option[id] = {
        'chart': chart,
        'option': option
    };
    sensor_option[id]['chart'].setOption(option);
}

// interval function, get data from db
function interval_function(){
    $.ajax({
        type: "get",
        url: "/sensor/data",
        dataType: "json",
        success: function(sensor_data){
            var sensor_data_map = {};
            for (var i in sensor_data){
                var row = sensor_data[i];
                sensor_data_map['sensor-chart-' + row.address] = {
                    'temperature' : row.temperature,
                    'humidity' : row.humidity,
                    'timestamp' : row.timeStamp
                }
            }

            for (var sensor in sensor_option){
                var current_option = sensor_option[sensor]['option'];
                var current_chart = sensor_option[sensor]['chart'];

                var temp_data = sensor_data_map[sensor]['temperature'].toFixed(2);
                var humi_data = sensor_data_map[sensor]['humidity'].toFixed(1);

                current_option.series[0].data.push(temp_data);
                current_option.series[1].data.push(humi_data);

                var time_data = (new Date()).toLocaleTimeString().replace(/^\D*/,'');
                current_option.xAxis.data.push(time_data);

                if (current_option.xAxis.data.length > 10){
                    current_option.series[0].data.shift();
                    current_option.series[1].data.shift();
                    current_option.xAxis.data.shift();
                }

                current_chart.setOption(current_option);

                // set current temperature and humidity
                var $temp_sensor = $("#sensor-temp-" + sensor.split('-')[2]);
                var $humi_sensor = $("#sensor-humi-" + sensor.split('-')[2]);
                $temp_sensor.text(temp_data + '°C');
                $humi_sensor.text(humi_data + '%RH');

                var temp_upper = $("#sensor-temp-upper-" + sensor.split('-')[2]).text();
                var temp_lower = $("#sensor-temp-lower-" + sensor.split('-')[2]).text();
                var humi_upper = $("#sensor-humi-upper-" + sensor.split('-')[2]).text();
                var humi_lower = $("#sensor-humi-lower-" + sensor.split('-')[2]).text();

                if (temp_data > temp_upper || temp_data < temp_lower){
                    $temp_sensor.css({
                        "color": "#ff5a00"
                    });
                } else {
                    $temp_sensor.css({
                        "color": "#5c5b5b"
                    });
                }

                if (humi_data > humi_upper || humi_data < humi_lower){
                    $humi_sensor.css({
                        "color": "#ff5a00"
                    });
                } else {
                    $humi_sensor.css({
                        "color": "#5c5b5b"
                    });
                }
            }
        },
        error: function () {
            message_info("数据加载出错", "info", 3);
        }
    });
}

// controller
var interval = null;
$('#start-btn').click(function () {
    var $start_btn = $('#start-btn');
    if ($start_btn.text() == "开始监控"){
        $.ajax({
                type: "get",
                url: "/sensor/status/is/monitor",
                success: function(sensor_data){
                    if (sensor_data == "已关闭"){
                        message_info("当前传感器处于关闭状态", "info", 3);
                    } else {
                        $select = $('#interval-select');
                        message_info("监控开始，间隔时间为" + $select.val() + "秒", "info", 3);
                        $('#interval-time').text($select.val());
                        $select.attr("disabled", true);

                        interval_function();
                        interval = setInterval(interval_function, parseInt($select.val()) * 1000);
                        $start_btn.text("停止监控");
                        $start_btn.removeClass("btn-info");
                        $start_btn.addClass("btn-warning");
                    }
                },
                error: function (error_status) {
                    message_info("获取传感器状态出错", "error", 3);
                }
        });
    } else if ($start_btn.text() == "停止监控"){
        message_info("监控停止", "info", 3);
        $('#interval-select').attr("disabled", false);

        clearInterval(interval);
        $start_btn.text("开始监控");
        $start_btn.removeClass("btn-warning");
        $start_btn.addClass("btn-info");
    }
});
