#!/bin/bash

echo "Applying migration PartnerIncomeInfo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerIncomeInfo               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerIncomeInfoController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerIncomeInfo.title = partnerIncomeInfo" >> ../conf/messages.en
echo "partnerIncomeInfo.heading = partnerIncomeInfo" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerIncomeInfo complete"
