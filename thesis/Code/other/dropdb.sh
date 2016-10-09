VERSIONS="8.0 8.1 8.2 8.3 8.4 9.0 9.1 9.2 9.3 9.4"

OLDPATH=$PATH
PGHOME=/home/tvondra/postgres
DATA=/home/tvondra/tmp

for v in $VERSIONS; do

    echo "PostgreSQL $v"

    # set the path
    export PATH=$PGHOME/$v/bin:$OLDPATH

    # start the cluster (and sleep for 15 seconds, until it fully starts)
    pg_ctl -D $DATA/data-$v -l pg-$v.log start >> bench-$v.log 2>&1
    sleep 5

    dropdb tpcds

    pg_ctl -D $DATA/data-$v stop > /dev/null 2>&1
    sleep 5

done
