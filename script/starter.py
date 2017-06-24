# -*- coding:utf-8 -*-
"""
Author: liye@qiyi.com
Creation Date: 2017/3/25
Description: Run to monitor serial and insert data into mysql
"""

import os
import sys

sys.path.append(os.path.join(os.path.dirname(__file__), "../"))

import script.component as component
from time import strftime, localtime
import logging

# ---- logging settings
logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                    datefmt='%Y-%m-%d %H:%M:%S',
                    filename=sys.path[0] + '/port.log.' + strftime("%Y%m%d%H", localtime()),
                    filemode='w')

# print error log to console
console = logging.StreamHandler()
console.setLevel(logging.ERROR)
console.setFormatter(logging.Formatter('%(asctime)s %(filename)s[line:%(lineno)d] %(message)s'))
logging.getLogger('').addHandler(console)

# ---- db settings
skipper_data_tbl = "skipper"
skipper_info_tbl = "skipper_info"
skipper_status_tbl = "skipper_status"

# ---- print information
os.chdir(sys.path[0])
print("Server start, current path is " + sys.path[0])
print("Monitoring, more information will show in " + sys.path[0] + '\port.log.YYYYMMDDHH')

# --- basic tools
try:
    parser = component.SkipperParser()
    port = component.SkipperPort()
    db = component.MySQL("mysql-jy")
except Exception as e:
    logging.error(e, stack_info=True)

# ---- insert settings
sensor_count = db.query("SELECT count(*) FROM " + skipper_status_tbl + " WHERE status = 1")[0][0]
db_circle = 4
INSERT_INTERVAL = sensor_count*2*db_circle


def starter():
    ser = port.start_port()
    merge_info = {}
    insert_interval = INSERT_INTERVAL

    # --- recorder start time
    sql = "INSERT INTO " + skipper_info_tbl + " (time) VALUES ('" + strftime("%Y-%m-%d %H:%M:%S", localtime()) + "')"
    db.execute(sql)

    while True:
        if ser.inWaiting() > 0:
            try:
                # parser info
                info = parser.parser_info(ser.readlines())

                # merge info
                for key, value in info.items():
                    merge_info[key] = value

                if insert_interval > 0:
                    insert_interval -= 1
                else:
                    if len(merge_info) == 0:
                        break

                    # parser info to db format
                    db_data = parser.transfer_to_db_format(merge_info)

                    # insert to mysql
                    sql = "INSERT INTO {0} (address, send_port, receive_port, value) values (%s, %s, %s, %s)"\
                        .format(skipper_data_tbl)
                    db.executemany(sql, db_data)

                    # update variables
                    insert_interval = INSERT_INTERVAL
                    merge_info.clear()

                    time = strftime("%Y-%m-%d %H:%M:%S", localtime())
                    logging.info("Insert data into db at %s\n %s" % (time, db_data))
            except Exception as e:
                logging.error(e)

if __name__ == '__main__':
    starter()
