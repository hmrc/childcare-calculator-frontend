#!/bin/bash

echo "Applying migration WhatsYourAge"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatsYourAge               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhatsYourAgeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatsYourAge               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhatsYourAgeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatsYourAge               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhatsYourAgeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatsYourAge               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhatsYourAgeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatsYourAge.title = whatsYourAge" >> ../conf/messages.en
echo "whatsYourAge.heading = whatsYourAge" >> ../conf/messages.en
echo "whatsYourAge.option1 = whatsYourAge" Option 1 >> ../conf/messages.en
echo "whatsYourAge.option2 = whatsYourAge" Option 2 >> ../conf/messages.en
echo "whatsYourAge.checkYourAnswersLabel = whatsYourAge" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whatsYourAge: Option[String] = cacheMap.getEntry[String](WhatsYourAgeId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whatsYourAge: Option[AnswerRow] = userAnswers.whatsYourAge map {";\
     print "    x => AnswerRow(\"whatsYourAge.checkYourAnswersLabel\", s\"whatsYourAge.$x\", true, routes.WhatsYourAgeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhatsYourAge complete"
