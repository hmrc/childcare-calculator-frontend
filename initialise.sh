#!/bin/bash

echo "Initialising..."

if grep -Fxp 'initialised' migrations/applied
then
    echo "This project has already been initialised, exiting"
    exit 1
fi

echo ""
echo "Setting up package names in scaffolds"

find ./ -name '*.scala*'  -type f -exec sed -i '' 's/\$package\$/uk.gov.hmrc.childcarecalculatorfrontend/g' {} \;
find ./ -name '*.sh'  -type f -exec sed -i '' 's/\$package\$/uk.gov.hmrc.childcarecalculatorfrontend/g' {} \;
find ./ -name '*.md'  -type f -exec sed -i '' 's/\$package\$/uk.gov.hmrc.childcarecalculatorfrontend/g' {} \;

echo ""
echo "Setting directory names in scaffolds"
find ./ -type d -iname '*appName*' -depth -exec bash -c 'mv "$1" "${1//\$appName__word\$/childcarecalculatorfrontend}"' -- {} \;

find ./ -name '*.sh'  -type f -exec sed -i '' 's/\$package;format=\"packaged\"\$/uk\/gov\/hmrc\/childcarecalculatorfrontend/g' {} \;

echo ""
echo "Initialising git repository"
git init
git add .
git commit -m 'initial commit'

echo "initialised" >> migrations/applied
