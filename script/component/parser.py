# -*- coding:utf-8 -*-
"""
Author: liye@qiyi.com
Creation Date: 2017/3/25
Description: Some basic operation of parser port's information
"""
import binascii


class SkipperParser:
    @staticmethod
    def parser_info(port_line_list):
        data_length = 16
        for port_line in port_line_list:
            # transfer port_line to str
            hex_str = binascii.b2a_hex(port_line).decode()

            if not hex_str.endswith("ff"):
                print("Error information: %s" % hex_str)
                raise Exception('Error Information From Port.')

            info = {}
            for position in range(0, len(hex_str), data_length):
                recorder = hex_str[position: position + data_length]
                if recorder != "":
                    send_port = recorder[4:6]
                    receive_port = recorder[6:8]
                    ln_address = recorder[10:12] + recorder[8:10]
                    data = '%.2f' % float(int(recorder[12:14], 16))
                    info[ln_address + "#" + receive_port] = {
                        "send_port": send_port,
                        "receive_port": receive_port,
                        "ln_address": ln_address,
                        "data": data
                    }
        return info

    @staticmethod
    def transfer_to_db_format(info):
        db_data = []
        for recorder in info:
            send_port = info[recorder]['send_port']
            receive_port = info[recorder]['receive_port']
            ln_address = info[recorder]['ln_address']
            data = info[recorder]['data']
            db_data.append((ln_address, send_port, receive_port, data))
        return db_data
