#/bin/sh
classpath='.'

#1、classpath中增加ftp-parser_framework本身所需的jar包
for file in `ls ../../ftp-parser-framework/lib/*.jar`
do
  classpath=$classpath:${file}
done

#2、classpath中增加具体应用程序所需的jar包
for file in `ls ../lib/*.jar`
do
  classpath=$classpath:${file}
done

#3、classpath中增加具体应用程序所需的配置文件目录
classpath=../conf:../classes:$classpath

CURRENTPATH=`pwd`/.. ;export CURRENTPATH

java -Dprogram=FTP_$CURRENTPATH -Xmx2048M -classpath $classpath com.inspur.ftpparserframework.ftp.Main "$@"