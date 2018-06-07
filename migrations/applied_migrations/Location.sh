#!/bin/bash

if grep -Fxp Location applied
then
    echo "Migration Location has already been applied, exiting"
    exit 1
fi

echo "Applying migration Location"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /location               uk.gov.hmrc.childcarecalculatorfrontend.controllers.LocationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /location               uk.gov.hmrc.childcarecalculatorfrontend.controllers.LocationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeLocation               uk.gov.hmrc.childcarecalculatorfrontend.controllers.LocationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeLocation               uk.gov.hmrc.childcarecalculatorfrontend.controllers.LocationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages
echo "location.title = location" >> ../conf/messages
echo "location.heading = location" >> ../conf/messages
echo "location.option1 = location" Option 1 >> ../conf/messages
echo "location.option2 = location" Option 2 >> ../conf/messages
echo "location.checkYourAnswersLabel = location" >> ../conf/messages

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def location: Option[String] = cacheMap.getEntry[String](LocationId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def location: Option[AnswerRow] = userAnswers.location map {";\
     print "    x => AnswerRow(\"location.checkYourAnswersLabel\", s\"location.$x\", true, routes.LocationController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Registering this migration as completed"
echo "Location" >> applied
