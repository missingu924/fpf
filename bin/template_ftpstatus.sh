#/bin/sh
CURRENTPATH=`pwd`/.. ;
 export CURRENTPATH;

if [ 0 -lt $# ]
then
	ps -ef | grep -i java |grep FTP | grep  $CURRENTPATH | grep  $1
else
	ps -ef | grep -i java |grep FTP | grep  $CURRENTPATH
fi

