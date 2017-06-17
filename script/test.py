# -*- coding:utf-8 -*-
"""
Author: liye@qiyi.com
Creation Date: 2017/6/17
Description: port test
"""

import os
import sys

sys.path.append(os.path.join(os.path.dirname(__file__), "../"))

import script.component as component

# --- basic tools
parser = component.SkipperParser()
port = component.SkipperPort()

ser = port.start_port()
while True:
    if ser.inWaiting() > 0:
        info = parser.parser_info(ser.readlines())
        print(info)