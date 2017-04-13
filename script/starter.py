# -*- coding:utf-8 -*-
"""
Author: liye@qiyi.com
Creation Date: 2017/3/25
Description: Run to monitor serial and insert data into mysql
"""

import script.component as component
import traceback
from time import strftime, gmtime

# ---- db settings
skipper_info_tbl = "skipper"

# ---- insert settings
INSERT_INTERVAL = 3


def starter():
    parser = component.SkipperParser()
    port = component.SkipperPort()
    db = component.MySQL()
    ser = port.start_port()

    merge_info = {}
    insert_interval = INSERT_INTERVAL

    while True:
        if ser.inWaiting() > 0:
            try:
                # parser info
                info = parser.parser_info(ser.readline())

                # merge info
                for key, value in info:
                    merge_info[key] = value

                if insert_interval > 0:
                    insert_interval -= 1
                else:
                    if len(merge_info) == 0:
                        break

                    # parser info to db format
                    db_data = parser.transfer_to_db_format(merge_info)

                    # insert to mysql
                    sql = "INSERT INTO " + skipper_info_tbl + \
                          " (address, send_port, receive_port, value) values (%s, %s, %s, %s)"
                    db.executemany(sql, db_data)

                    # update variables
                    insert_interval = INSERT_INTERVAL
                    merge_info.clear()

                    print("Insert data into db at %s." % strftime("%Y-%m-%d %H:%M:%S", gmtime()))
            except Exception as e:
                traceback.print_exc(e)

if __name__ == '__main__':
    import os, sys
    os.chdir(sys.path[0])

    starter()
