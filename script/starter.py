# -*- coding:utf-8 -*-
"""
Author: liye@qiyi.com
Creation Date: 2017/3/25
Description:
"""

import script.component as component
import traceback

parser = component.SkipperParser()
port = component.SkipperPort(port="COM4", baudrate=115200, timeout=2)
db = component.MySQL()

skipper_info_tbl = "skipper"


def starter():
    ser = port.start_port()
    while True:
        if ser.inWaiting() > 0:
            try:
                # parser info
                info = parser.parser_info(ser.readline())
                print(info)
                db_data = parser.transfer_to_db_format(info)

                # insert to mysql
                sql = "INSERT INTO " + skipper_info_tbl + \
                      " (address, send_port, receive_port, value) values (%s, %s, %s, %s)"
                db.executemany(sql, db_data)
            except Exception as e:
                traceback.print_exc(e)

if __name__ == '__main__':
    starter()
