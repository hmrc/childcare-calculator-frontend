#!/bin/bash

echo "Applying migration YourPartnersAge"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourPartnersAge               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourPartnersAgeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourPartnersAge               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourPartnersAgeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourPartnersAge               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourPartnersAgeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourPartnersAge               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourPartnersAgeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourPartnersAge.title = yourPartnersAge" >> ../conf/messages.en
echo "yourPartnersAge.heading = yourPartnersAge" >> ../conf/messages.en
echo "yourPartnersAge.option1 = yourPartnersAge" Option 1 >> ../conf/messages.en
echo "yourPartnersAge.option2 = yourPartnersAge" Option 2 >> ../conf/messages.en
echo "yourPartnersAge.checkYourAnswersLabel = yourPartnersAge" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourPartnersAge: Option[String] = cacheMap.getEntry[String](YourPartnersAgeId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourPartnersAge: Option[AnswerRow] = userAnswers.yourPartnersAge map {";\
     print "    x => AnswerRow(\"yourPartnersAge.checkYourAnswersLabel\", s\"yourPartnersAge.$x\", true, routes.YourPartnersAgeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourPartnersAge complete"
