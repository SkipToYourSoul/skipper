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
var $download_table = $('#download-info-table');
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
            if (sensor_data.length == 0){
                message_info("没有符合条件的读数", "info", 5);
                $("#temp-grad").html("无数值");
                $('#humi-grad').html("无数值");
            } else {
                $("#temp-grad").html((maxTemp - minTemp).toFixed(2) + " °C");
                $('#humi-grad').html((maxHumi - minHumi).toFixed(1) + " %RH");
            }

            $table.bootstrapTable('hideLoading');
            initTable(sensor_data);
        }
    });
}

function initTable(table_data){
    $table.bootstrapTable({
        columns: [{
            field: 'address',
            sortable: 'true',
            align: 'center',
            title: '传感器编号'
        },{
            field: 'temperature',
            sortable: 'true',
            align: 'center',
            title: '最新温度(°C)',
            cellStyle: 'temperature_style',
            formatter: 'temperature_formatter'
        },{
            field: 'humidity',
            sortable: 'true',
            align: 'center',
            title: '最新湿度(%RH)',
            cellStyle: 'humidity_style',
            formatter: 'humidity_formatter'
        },{
            field: 'averageTemperature',
            sortable: 'true',
            align: 'center',
            title: '平均温度(°C)',
            formatter: 'temperature_formatter'
        },{
            field: 'averageHumidity',
            sortable: 'true',
            align: 'center',
            title: '平均湿度(%RH)',
            formatter: 'humidity_formatter'
        },{
            field: 'shiftTemperature',
            sortable: 'true',
            align: 'center',
            title: '温度波动(°C)',
            formatter: 'temperature_formatter'
        },{
            field: 'shiftHumidity',
            sortable: 'true',
            align: 'center',
            title: '湿度波动(%RH)',
            formatter: 'humidity_formatter'
        }],
        data: table_data
    });
}

function temperature_formatter(value){
    return value.toFixed(2);
}

function humidity_formatter(value){
    return value.toFixed(1);
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

$('#tableModel').on('shown.bs.modal', function (e) {
    var time = $('#data-time-select').val();
    var interval = $('#data-interval-select').val();
    if (time == "" || interval == ""){
        message_info("请先选择正确的参数", 'info', 3);
        $('#tableModel').modal('hide');
        return;
    }
    $.ajax({
        type: "get",
        url: "/sensor/overview/download?dataTime=" + time + "&dataInterval=" + interval,
        dataType: "json",
        error: function (error_message) {
            message_info("获取传感器数据错误", "error", 3);
        },
        success: function (sensor_data) {
            if (sensor_data.length == 0){
                message_info("没有符合条件的数据", "info", 3);
                return;
            }
            $download_table.bootstrapTable('destroy');
            // add columns
            var columns = [{
                field: 'timestamp',
                sortable: 'true',
                align: 'center',
                width: 300,
                title: '时间戳(yyyy-mm-dd HH:mm:ss)',
                escape: true,
                formatter: 'timestamp_formatter'
            }];
            var address_list = find_online_sensor_address();
            for (var row in address_list){
                var address = address_list[row];
                columns.push({
                    field: address + "temp",
                    sortable: 'true',
                    align: 'center',
                    title: address + "温度"
                });
                columns.push({
                    field: address + "humi",
                    sortable: 'true',
                    align: 'center',
                    title: address + "湿度"
                });
            }

            // add data
            var table_data = [];
            for (var row in sensor_data){
                var timestamp = sensor_data[row]['timestamp'];
                var list = sensor_data[row]['list'];
                var data = {};
                data['timestamp'] = timestamp;

                for (var i in list){
                    var address = list[i].split('#')[0];
                    var temp = parseFloat(list[i].split('#')[1]).toFixed(2);
                    var humi = parseFloat(list[i].split('#')[2]).toFixed(1);
                    data[address + "temp"] = temp;
                    data[address + "humi"] = humi;
                }

                table_data.push(data);
            }

            // build table
            $download_table.bootstrapTable({
                columns: columns,
                data: table_data
            });
        }
    });
});

function timestamp_formatter(value){
    var date = value.split(' ')[0];
    var time = value.split(' ')[1].split('.')[0];
    return date + " " + time;
}

function find_online_sensor_address(){
    var address = [];
    $.ajax({
        type: "get",
        url: "/sensor/status",
        dataType: "json",
        async: false,
        error: function (error_message) {
            message_info("获取传感器地址数据错误", "error", 3);
        },
        success: function (sensor_data) {
            for(var row in sensor_data){
                address.push(sensor_data[row]['address']);
            }
        }
    });
    return address;
}