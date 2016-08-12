# -*- coding:utf-8 -*-

import os
import string
import sys

from avro import schema
from avro import io
from avro import datafile

if __name__ == '__main__':
  if len(sys.argv) !=2:
    sys.exit("使用方法：%s <data_file>" % sys.argv[0])
  avro_file = sys.argv[1]
  writer = open(avro_file,'wb')
  datum_writer = io.DatumWriter()
  schema_object = schema.parse(' \
    { "type":"record", \
    "name":"Pair", \
    "doc":"StringPair", \
    "fields":[ \
      {"name":"left","type":"string"}, \
      {"name":"right","type":"string"} \
    ] \
  }')
  dfw = datafile.DataFileWriter(writer,datum_writer,schema_object)

  print("请输入以逗号分隔的数据，Ctrl-d结束")
  for line in sys.stdin.readlines():
    (left,right) = string.split(line.strip(),',')
    dfw.append({'left':left,'right':right});
  dfw.close()
