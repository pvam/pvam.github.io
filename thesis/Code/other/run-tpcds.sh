VERSIONS="8.0 8.1 8.2 8.3 8.4 9.0 9.1 9.2 9.3 9.4beta1 head"

OLDPATH=$PATH
PGHOME=/home/postgres/builds
DATA=/mnt/data/tpcds/pgdata
FILE=messages.data

# number of pre-generated SQL scripts
QUERYDIR=queries-compat
QUERIES=3
RUNS=1

# timeout (1h by default)
TIMEOUT=1800000

for v in $VERSIONS; do

    echo "PostgreSQL $v"

    # set the path
    export PATH=$PGHOME/$v/bin:$OLDPATH

    # if testing this version completed, skip to the next one
    if [ -f "results/$v/complete" ]; then
        echo "  completed"
        continue;
    fi

    # remove the current results directory, create new one
    rm -Rf results/$v
    mkdir -p results/$v

    # start the cluster (and sleep for 15 seconds, until it fully starts)
    pg_ctl -D $DATA/data-$v -l pg-$v.log start >> bench-$v.log 2>&1
    sleep 15

    dropdb tpcds >> bench-$v.log 2>&1
    createdb tpcds >> bench-$v.log 2>&1

    psql tpcds < ddl/tables.sql >> bench-$v.log 2>&1

    # load the data
    echo "  loading data"

    /usr/bin/time -f '%e' -o time.tmp psql tpcds -c "\i ddl/load.sql" >> bench-$v.log 2>&1

    time_a=`cat time.tmp`
    size_a=`du -s $DATA/data-$v/base | cut -f 1`

    # creating indexes
    echo "  creating indexes"

    /usr/bin/time -f '%e' -o time.tmp psql tpcds -c "\i ddl/indexes.sql" >> bench-$v.log 2>&1

    time_b=`cat time.tmp`
    size_b=`du -s $DATA/data-$v/base | cut -f 1`

    echo "  vacuum full"
    /usr/bin/time -f '%e' -o time.tmp psql tpcds -c "vacuum full" > /dev/null 2>&1

    time_c=`cat time.tmp`
    size_c=`du -s $DATA/data-$v/base | cut -f 1`

    echo "  vacuum freeze"
    /usr/bin/time -f '%e' -o time.tmp psql tpcds -c "vacuum freeze" > /dev/null 2>&1

    time_d=`cat time.tmp`
    size_d=`du -s $DATA/data-$v/base | cut -f 1`

    echo "  analyze"
    /usr/bin/time -f '%e' -o time.tmp psql tpcds -c "analyze" > /dev/null 2>&1

    time_e=`cat time.tmp`

    echo "LOAD : $time_a"          >> results/$v/times.log
    echo "INDEXES : $time_b"       >> results/$v/times.log
    echo "VACUUM FULL : $time_c"   >> results/$v/times.log
    echo "VACUUM FREEZE : $time_d" >> results/$v/times.log
    echo "ANALYZE : $time_e"       >> results/$v/times.log

    echo "LOAD : $size_a"          >> results/$v/sizes.log
    echo "INDEXES : $size_b"       >> results/$v/sizes.log
    echo "VACUUM FULL : $size_c"   >> results/$v/sizes.log
    echo "VACUUM FEEZE : $size_d"  >> results/$v/sizes.log

    for r in `seq 1 $RUNS`; do

        echo "  run $i"

        for q in `seq 1 $QUERIES`; do

            echo "\o /dev/null
                  \timing
                  set statement_timeout=$TIMEOUT;
                  \i $QUERYDIR/$q.sql" | psql tpcds > results/$v/queries-$q-run-$r.log 2>&1;

        done

    done

    echo "\i $QUERYDIR/explain.sql" | psql tpcds > results/$v/explain.log 2>&1;

    echo "set statement_timeout=$TIMEOUT;
          \i $QUERYDIR/explain-analyze.sql" | psql tpcds > results/$v/explain-analyze.log 2>&1;

    touch "results/$v/complete";

    dropdb tpcds > /dev/null 2>&1

    pg_ctl -D $DATA/data-$v stop > /dev/null 2>&1
    sleep 15

    # remove the WAL segments to save space
    pg_resetxlog $DATA/data-$v

done
