#!/bin/bash

echo "Applying migration YourAge"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourAge               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourAgeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourAge               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourAgeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourAge               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourAgeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourAge               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourAgeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourAge.title = yourAge" >> ../conf/messages.en
echo "yourAge.heading = yourAge" >> ../conf/messages.en
echo "yourAge.option1 = yourAge" Option 1 >> ../conf/messages.en
echo "yourAge.option2 = yourAge" Option 2 >> ../conf/messages.en
echo "yourAge.checkYourAnswersLabel = yourAge" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourAge: Option[String] = cacheMap.getEntry[String](YourAgeId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourAge: Option[AnswerRow] = userAnswers.yourAge map {";\
     print "    x => AnswerRow(\"yourAge.checkYourAnswersLabel\", s\"yourAge.$x\", true, routes.YourAgeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourAge complete"
