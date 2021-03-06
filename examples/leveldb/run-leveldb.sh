#! /bin/bash
LAUNCHER_DIR=$(dirname $0)
HOSTNAME=concerto
AKKAPORT=2558
HTTPPORT=8000
SEED_NODE="$HOSTNAME:$AKKAPORT"
APPLICATION_SECRET="cfd16c3a-f0f2-4fa9-8e58-ff9a2ad2a422"

java -DHOSTNAME=$HOSTNAME \
	-DPORT=$AKKAPORT \
	-DSEED_NODE=$SEED_NODE \
	-DAPPLICATION_SECRET=$APPLICATION_SECRET \
	-Dplay.server.http.port=$HTTPPORT \
	-jar $LAUNCHER_DIR/target/scala-2.12/leveldb-assembly-1.0.1.jar \
	&>> leveldb-assembly-1.0-SNAPSHOT.log &

