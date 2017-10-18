#!/bin/bash

echo "Applying migration YourIncomeInfo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourIncomeInfo               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourIncomeInfoController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourIncomeInfo.title = yourIncomeInfo" >> ../conf/messages.en
echo "yourIncomeInfo.heading = yourIncomeInfo" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourIncomeInfo complete"
