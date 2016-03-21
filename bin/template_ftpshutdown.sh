#/bin/sh
CURRENTPATH=`pwd`/.. ;
 export CURRENTPATH;

if [ 0 -lt $# ]
then
        proccessIds=`ps -ef | grep -i java |grep FTP | grep  $CURRENTPATH | grep  $1|awk '{print $2}'`
else
        proccessIds=`ps -ef | grep -i java |grep FTP | grep  $CURRENTPATH|awk '{print $2}'`
fi


for  proccessId in $proccessIds
 do
        echo `ps f$proccessId`
        echo "shutdown $proccessId y/n(n)"
        read isShutdown

	if [  -z $isShutdown ]
        then
	continue
	fi

 	if [ $isShutdown = "y" ]
        then
        echo "kill $proccessId"
        kill $proccessId
 	fi
done

