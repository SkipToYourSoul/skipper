# -*- coding:utf-8 -*-
"""
Author: liye@qiyi.com
Creation Date: 2017/3/25
Description: some basic operation with db
"""
try:
    import pymysql
    import configparser
    import os
except ImportError:
    import sys
    print(sys.stderr, """ !!!
    There was a problem importing one of the Python modules required.
    The error leading to this problem was:
    %s

    Please install a package which provides this module, or verify that the module is installed correctly.
    It's possible that the above module doesn't match the current version of Python, which is:
    %s

    """ % (sys.exc_info(), sys.version))
    sys.exit(1)


class MySQL(object):
    conf_path = "./common.conf"
    conf_section = "mysql-local"

    def __init__(self, conf_section):
        if not os.path.exists(self.conf_path):
            print("Error config file path.")
            return

        self.conf_section = conf_section
        config_file = configparser.ConfigParser()
        config_file.read(self.conf_path, "utf-8")

        self.MYSQL_HOST = config_file.get(self.conf_section, 'host')
        self.MYSQL_PORT = int(config_file.get(self.conf_section, 'port'))
        self.MYSQL_USER = config_file.get(self.conf_section, 'user')
        self.MYSQL_PASSWD = config_file.get(self.conf_section, 'password')
        self.MYSQL_DB_NAME = config_file.get(self.conf_section, 'db')

        self.conn = pymysql.connect(host=self.MYSQL_HOST, port=self.MYSQL_PORT,
                                    user=self.MYSQL_USER, passwd=self.MYSQL_PASSWD, db=self.MYSQL_DB_NAME)
        print("Open database {0} successful...".format(self.conf_section))

    def get_cursor(self):
        return self.conn.cursor()

    def query(self, sql):
        cursor = self.get_cursor()
        try:
            cursor.execute(sql, None)
            result = cursor.fetchall()
        except Exception as e:
            print("mysql query error: %s" % e)
            return None
        finally:
            cursor.close()
        return result

    def execute(self, sql, param=None):
        cursor = self.get_cursor()
        try:
            cursor.execute(sql, param)
            self.conn.commit()
            affected_row = cursor.rowcount
        except Exception as e:
            print("mysql execute error: %s" % e)
            return 0
        finally:
            cursor.close()
        return affected_row

    def executemany(self, sql, params=None):
        cursor = self.get_cursor()
        try:
            cursor.executemany(sql, params)
            self.conn.commit()
            affected_rows = cursor.rowcount
        except Exception as e:
            print("mysql executemany error: %s" % e)
            return 0
        finally:
            cursor.close()
        return affected_rows

    def close(self):
        try:
            self.conn.close()
        except Exception as e:
            pass

    def __del__(self):
        self.close()
