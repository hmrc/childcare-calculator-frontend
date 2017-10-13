#!/bin/bash

echo "Applying migration MaxFreeHoursInfo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /maxFreeHoursInfo               uk.gov.hmrc.childcarecalculatorfrontend.controllers.MaxFreeHoursInfoController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "maxFreeHoursInfo.title = maxFreeHoursInfo" >> ../conf/messages.en
echo "maxFreeHoursInfo.heading = maxFreeHoursInfo" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration MaxFreeHoursInfo complete"
