FILES=""

for f in queries*.log; do
    sed 's/^Time: \(.*\) ms$/\1/g' $f > ${f/.log/.times};
done;

for f in queries*.times; do
    FILES="$FILES $f"
done

paste -d ',' $FILES > results.csv

