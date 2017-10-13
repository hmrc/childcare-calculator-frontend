#!/bin/bash

echo "Applying migration MaxFreeHoursResult"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /maxFreeHoursResult               uk.gov.hmrc.childcarecalculatorfrontend.controllers.MaxFreeHoursResultController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "maxFreeHoursResult.title = maxFreeHoursResult" >> ../conf/messages.en
echo "maxFreeHoursResult.heading = maxFreeHoursResult" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration MaxFreeHoursResult complete"
