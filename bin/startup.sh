#/bin/sh
classpath='.'

#1��classpath������ftp-parser_framework���������jar��
for file in `ls ../../ftp-parser-framework/lib/*.jar`
do
  classpath=$classpath:${file}
done

#2��classpath�����Ӿ���Ӧ�ó��������jar��
for file in `ls ../lib/*.jar`
do
  classpath=$classpath:${file}
done

#3��classpath�����Ӿ���Ӧ�ó�������������ļ�Ŀ¼
classpath=../conf:../classes:$classpath

CURRENTPATH=`pwd`/.. ;export CURRENTPATH

java -Dprogram=FPF_$CURRENTPATH -Xmx3G -XX:-UseGCOverheadLimit -classpath $classpath com.inspur.ftpparserframework.Main "$@"