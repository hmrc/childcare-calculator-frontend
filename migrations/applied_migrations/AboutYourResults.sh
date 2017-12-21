#!/bin/bash

echo "Applying migration AboutYourResults"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /aboutYourResults               uk.gov.hmrc.childcarecalculatorfrontend.controllers.AboutYourResultsController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "aboutYourResults.title = aboutYourResults" >> ../conf/messages.en
echo "aboutYourResults.heading = aboutYourResults" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AboutYourResults complete"
