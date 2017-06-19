# -*- coding:utf-8 -*-
"""
Author: liye@qiyi.com
Creation Date: 2017/3/25
Description: Some basic operation of parser port's information
    data format: FE 06 90 A0 A0 0F 6C 8B FF
        head: FE
        data length: 06
        send port: 90
        receive port: A0(temperature)/A1(humidity)
        address: A0 0F (0x0FA0)
        data: 6C 8B (FF -> FE FD, FE -> FE FC)
        tail: FF
"""
import binascii
import logging
import sys
from time import strftime, localtime

logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                    datefmt='%Y-%m-%d %H:%M:%S',
                    filename=sys.path[0] + '/port.log.' + strftime("%Y%m%d%H", localtime()),
                    filemode='w')


class SkipperParser:
    # SHT30 threshold
    max_tmp_threshold = 125
    min_tmp_threshold = -40
    max_hum_threshold = 257
    min_hum_threshold = -40

    data_byte_length = 4
    data_package_length = data_byte_length + 14

    @staticmethod
    def humidity_parser(param):
        data = float(int(param, 16))
        return 100 * data / (2 ** 16 - 1)

    @staticmethod
    def temperature_parser(param):
        data = float(int(param, 16))
        return 175 * data / (2 ** 16 - 1) - 45

    # data: 6C 8B (FF -> FE FD, FE -> FE FC)
    # handle data
    @staticmethod
    def transfer_data_to_base_format(param):
        if len(param) == 4:
            return param
        else:
            param = param.replace("FEFD", "FF")
            param = param.replace("FEFC", "FE")
            if len(param) == 4:
                return param

        return None

    @staticmethod
    def parser_info(port_line_list, max_tmp_threshold=max_tmp_threshold, min_tmp_threshold=min_tmp_threshold,
                    max_hum_threshold=max_hum_threshold, min_hum_threshold=min_hum_threshold):
        info = {}
        for port_line in port_line_list:
            # transfer port_line to str
            recorder = binascii.b2a_hex(port_line).decode()
            # recorder = port_line
            recorder = recorder.upper()
            # print(recorder)

            # check recorder
            if len(recorder) == 0 or len(recorder) < SkipperParser.data_package_length:
                logging.warning('Error Recorder \'%s\' From Port.' % recorder)
                continue

            if not recorder.endswith("ff") and not recorder.endswith("FF"):
                logging.warning('Error Recorder \'%s\' From Port.' % recorder)

            send_port = recorder[4:6]
            receive_port = recorder[6:8]
            ln_address = recorder[10:12] + recorder[8:10]
            data = SkipperParser.transfer_data_to_base_format(recorder[12:(len(recorder)-2)])
            if data is None:
                logging.warning('Error format data \'%s\'' % data)
                continue

            if receive_port == "A0":
                data = SkipperParser.temperature_parser(data)
                if data > max_tmp_threshold or data < min_tmp_threshold:
                    logging.warning('Error temperature recorder data \'%s\'' % recorder)
                    continue
            elif receive_port == "A1":
                data = SkipperParser.humidity_parser(data)
                if data > max_hum_threshold or data < min_hum_threshold:
                    logging.warning('Error humidity recorder data \'%s\'' % recorder)
                    continue

            data = '%.2f' % data

            info[ln_address + "#" + receive_port] = {
                "send_port": send_port,
                "receive_port": receive_port,
                "ln_address": ln_address,
                "value": data
            }
        return info

    @staticmethod
    def transfer_to_db_format(info):
        db_data = []
        for recorder in info:
            send_port = info[recorder]['send_port']
            receive_port = info[recorder]['receive_port']
            ln_address = info[recorder]['ln_address']
            data = info[recorder]['value']
            db_data.append((ln_address, send_port, receive_port, data))
        return db_data


if __name__ == "__main__":
    logging.info(SkipperParser.parser_info(["fe0690a0a00f6cfefdff"]))
    try:
        raise Exception('test')
    except Exception as e:
        logging.error(e, stack_info=True)
