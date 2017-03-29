# -*- coding:utf-8 -*-
"""
Author: liye@qiyi.com
Creation Date: 2017/3/25
Description: some basic operation of port
"""
try:
    import serial
except ImportError:
    import sys
    print(sys.stderr, """ !!!
    There was a problem importing one of the Python modules required.
    The error leading to this problem was:
    %s

    Please install a package which provides this module, or verify that the module is installed correctly.
    It's possible that the above module doesn't match the current version of Python, which is:
    %s

    """) % (sys.exc_info(), sys.version)
    sys.exit(1)


class SkipperPort:
    def __init__(self, port="COM4", baudrate=115200, timeout=2):
        self.port = port
        self.baudrate = baudrate
        self.timeout = timeout
        self.ser = None

    def start_port(self):
        if self.ser is not None:
            print("Serial port is already open!")
            return self.ser
        self.ser = serial.Serial(port=self.port, baudrate=self.baudrate, timeout=self.timeout)
        if not self.ser.isOpen():
            print("Serial port open error!")
            return None
        print("Open serial port at port %s and baudrate %s and timeout %s, listening..."
              % (self.port, self.baudrate, self.timeout))
        return self.ser

    def close_port(self):
        if self.ser.isOpen():
            self.ser.close()

    def __del__(self):
        self.ser.close()