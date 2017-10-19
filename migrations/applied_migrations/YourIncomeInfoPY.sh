#!/bin/bash

echo "Applying migration YourIncomeInfoPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourIncomeInfoPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourIncomeInfoPYController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourIncomeInfoPY.title = yourIncomeInfoPY" >> ../conf/messages.en
echo "yourIncomeInfoPY.heading = yourIncomeInfoPY" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourIncomeInfoPY complete"
