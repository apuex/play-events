#! /bin/bash
LAUNCHER_DIR=$(dirname $0) 
java -DHOSTNAME=113.108.158.19 \
	-DSEED_NODE=113.108.158.19 \
	-Dplay.server.http.port=8000 \
	-jar $LAUNCHER_DIR/target/scala-2.12/leveldb-assembly-1.0-SNAPSHOT.jar &

