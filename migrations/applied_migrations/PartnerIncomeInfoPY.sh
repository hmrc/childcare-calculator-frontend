#!/bin/bash

echo "Applying migration PartnerIncomeInfoPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerIncomeInfoPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerIncomeInfoPYController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerIncomeInfoPY.title = partnerIncomeInfoPY" >> ../conf/messages.en
echo "partnerIncomeInfoPY.heading = partnerIncomeInfoPY" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerIncomeInfoPY complete"
