/**
 * Created by liye on 2017/4/10.
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

/**
 * form
 * @type {*}
 */
$form = $('#sensor-form');
$form.formValidation({
    framework: 'bootstrap',
    icon: {
        valid: 'glyphicon glyphicon-ok',
        invalid: 'glyphicon glyphicon-remove'
    },
    fields: {
        'id': {validators: {notEmpty: {message: '传感器编号不能为空'}}},
        'lower-temp': {validators: {notEmpty: {message: '温度阈值不能为空'}}},
        'upper-temp': {validators: {notEmpty: {message: '温度阈值不能为空'}}},
        'lower-humi': {validators: {notEmpty: {message: '湿度阈值不能为空'}}},
        'upper-humi': {validators: {notEmpty: {message: '湿度阈值不能为空'}}}
    }
}).on('success.form.fv', function (evt) {
    evt.preventDefault();
    $.ajax({
        type: 'POST',
        url: '/sensor/status/save/modify',
        data: $(this).serialize(),
        success: function (return_status) {
            if (return_status != "error") {
                message_info(return_status, "success", 3);
                $table.bootstrapTable('refresh');
                $form[0].reset();
                $('#formModel').modal('hide');
            } else {
                message_info("修改出错", "error", 3);
            }
        },
        error: function (return_status) {
            message_info("修改出错", "error", 3);
        }
    });
}).on('err.form.fv', function (evt) {
    message_info("提交失败", 'error', 3);
});


/**
 * sensor table
 */
var $table = $('#sensor-table');
$table.bootstrapTable({
    url: "/sensor/status",
    columns: [{
        field: 'radio',
        radio: 'true'
    },{
        field: 'address',
        sortable: 'true',
        align: 'center',
        title: '传感器编号'
    },{
        field: 'status',
        sortable: 'true',
        align: 'center',
        title: '当前状态',
        cellStyle: 'status_style',
        formatter: 'status_format'
    },{
        field: 'uppertemp',
        sortable: 'true',
        align: 'center',
        title: '高温阈值(°C)'
    },{
        field: 'lowertemp',
        sortable: 'true',
        align: 'center',
        title: '低温阈值(°C)'
    },{
        field: 'upperhumi',
        sortable: 'true',
        align: 'center',
        title: '高湿度阈值(%RH)'
    },{
        field: 'lowerhumi',
        sortable: 'true',
        align: 'center',
        title: '低湿度阈值(%RH)'
    }],
    onCheck: function (row) {
        $('#address').val(row.address);
        $('#lower-temp').val(row.lowertemp);
        $('#upper-temp').val(row.uppertemp);
        $('#lower-humi').val(row.lowerhumi);
        $('#upper-humi').val(row.upperhumi);
    }
});

function status_style(value, row, index) {
    if (value == '监控中'){
        return {
            classes: 'success'
        }
    } else {
        return {
            classes: 'warning'
        }
    }
}

function status_format(value) {
    if (value == '1')
        return "监控中";
    else
        return "已关闭";
}

function status_change() {
    var $select = $table.bootstrapTable('getSelections');
    if ($select.length == 0){
        message_info("请选择一条记录", "info", "3");
        return;
    }
    $.ajax({
        type: 'POST',
        url: '/sensor/status/save/switch',
        data: $select[0],
        success: function (return_status) {
            if (return_status == "success") {
                message_info("修改成功", "success", 3);
                $table.bootstrapTable('refresh');
            } else {
                message_info("修改出错", "error", 3);
                $table.bootstrapTable('refresh');
            }
        },
        error: function (data) {
            message_info("修改出错", "error", 3);
        }
    });
}
