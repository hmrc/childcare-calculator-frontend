#!/bin/bash

echo "Applying migrations..."
cd migrations
for file in *.sh
do
    if grep -Fxp $file applied
    then
        echo "Skipping $file as it has already been applied"
    else
        echo "Applying migration $file"
        chmod u+x $file
        /bin/bash $file
    fi
done

