/**
 * Created by liye on 2017/4/13.
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

var maxTemp, maxHumi, minTemp, minHumi;
var $table = $('#all-info-table');
initial(30, 30);

$("#refreshBtn").click(function (evt) {
   var time = $('#data-time-select').val();
   var interval = $('#data-interval-select').val();
   if (time == "" || interval == ""){
       message_info("请先选择正确的参数", 'info', 3);
       return;
   }
   initial(time, interval);
});

/**
 * data of overview content
 */
function initial(data_time, data_interval){
    $table.bootstrapTable('destroy');
    $table.bootstrapTable('showLoading');

    $.ajax({
        type: "get",
        url: "/sensor/overview/data?dataTime=" + data_time + "&dataInterval=" + data_interval,
        dataType: "json",
        error: function (error_message) {
            message_info("初始化数据错误", "error", 3);
        },
        success: function (sensor_data) {
            var tempGrads = 0; var humiGrads = 0;
            maxTemp = 0.0;
            maxHumi = 0.0;
            minTemp = 100.0;
            minHumi = 100.0;
            for (var i in sensor_data){
                var row = sensor_data[i];
                var address = row['address'];
                if (row['temperature'] > maxTemp)
                    maxTemp = row['temperature'];
                if (row['temperature'] < minTemp)
                    minTemp = row['temperature'];
                if (row['humidity'] > maxHumi)
                    maxHumi = row['humidity'];
                if (row['humidity'] < minHumi)
                    minHumi = row['humidity'];
            }

            $("#data-time").html(data_time + " 分钟");
            $("#data-interval").html(data_interval + " 秒");
            $("#temp-grad").html(maxTemp - minTemp + " °C");
            $('#humi-grad').html(maxHumi - minHumi + " °RH");

            $table.bootstrapTable('hideLoading');
            initTable(sensor_data);
        }
    });
}

function initTable(table_data){
    $table.bootstrapTable({
        columns: [{
            field: 'radio',
            radio: 'true'
        },{
            field: 'address',
            sortable: 'true',
            align: 'center',
            title: '传感器编号'
        },{
            field: 'temperature',
            sortable: 'true',
            align: 'center',
            title: '最新温度',
            cellStyle: 'temperature_style'
        },{
            field: 'humidity',
            sortable: 'true',
            align: 'center',
            title: '最新湿度',
            cellStyle: 'humidity_style'
        },{
            field: 'averageTemperature',
            sortable: 'true',
            align: 'center',
            title: '平均温度',
            formatter: 'data_formatter'
        },{
            field: 'averageHumidity',
            sortable: 'true',
            align: 'center',
            title: '平均湿度',
            formatter: 'data_formatter'
        },{
            field: 'shiftTemperature',
            sortable: 'true',
            align: 'center',
            title: '温度波动'
        },{
            field: 'shiftHumidity',
            sortable: 'true',
            align: 'center',
            title: '湿度波动'
        }],
        data: table_data
    });
}

function data_formatter(value){
    return value.toFixed(2);
}

function temperature_style(value, row, index, field) {
    var maxStyle = {
        classes: 'text-nowrap danger',
        css: {"color": "white"}
    };
    var minStyle = {
        classes: 'text-nowrap info',
        css: {"color": "white"}
    };
    if (value >= maxTemp)
        return maxStyle;
    else if (value <= minTemp)
        return minStyle;
    else
        return {};
}

function humidity_style(value, row, index, field) {
    var maxStyle = {
        classes: 'text-nowrap danger',
        css: {"color": "white"}
    };
    var minStyle = {
        classes: 'text-nowrap info',
        css: {"color": "white"}
    };
    if (value >= maxHumi)
        return maxStyle;
    else if (value <= minHumi)
        return minStyle;
    else
        return {};
}