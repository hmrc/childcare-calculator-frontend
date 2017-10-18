#!/bin/bash

echo "Applying migration WhoPaysIntoPension"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoPaysIntoPension               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoPaysIntoPensionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoPaysIntoPension               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoPaysIntoPensionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoPaysIntoPension               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoPaysIntoPensionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoPaysIntoPension               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoPaysIntoPensionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoPaysIntoPension.title = whoPaysIntoPension" >> ../conf/messages.en
echo "whoPaysIntoPension.heading = whoPaysIntoPension" >> ../conf/messages.en
echo "whoPaysIntoPension.option1 = whoPaysIntoPension" Option 1 >> ../conf/messages.en
echo "whoPaysIntoPension.option2 = whoPaysIntoPension" Option 2 >> ../conf/messages.en
echo "whoPaysIntoPension.checkYourAnswersLabel = whoPaysIntoPension" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whoPaysIntoPension: Option[String] = cacheMap.getEntry[String](WhoPaysIntoPensionId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whoPaysIntoPension: Option[AnswerRow] = userAnswers.whoPaysIntoPension map {";\
     print "    x => AnswerRow(\"whoPaysIntoPension.checkYourAnswersLabel\", s\"whoPaysIntoPension.$x\", true, routes.WhoPaysIntoPensionController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhoPaysIntoPension complete"
