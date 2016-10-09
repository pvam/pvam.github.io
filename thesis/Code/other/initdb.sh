VERSIONS="7.4 8.0 8.1 8.2 8.3 8.4 9.0 9.1 9.2 9.3 9.4beta1 head"
DATA=/mnt/data/tpcds/pgdata
OLDPATH=$PATH
PGHOME=/home/postgres/builds

for v in $VERSIONS; do

    export PATH=$PGHOME/$v/bin:$OLDPATH

    initdb $DATA/data-$v

done
