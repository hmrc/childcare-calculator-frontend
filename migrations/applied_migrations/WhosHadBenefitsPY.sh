#!/bin/bash

echo "Applying migration WhosHadBenefitsPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whosHadBenefitsPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhosHadBenefitsPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whosHadBenefitsPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhosHadBenefitsPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhosHadBenefitsPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhosHadBenefitsPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhosHadBenefitsPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhosHadBenefitsPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whosHadBenefitsPY.title = whosHadBenefitsPY" >> ../conf/messages.en
echo "whosHadBenefitsPY.heading = whosHadBenefitsPY" >> ../conf/messages.en
echo "whosHadBenefitsPY.option1 = whosHadBenefitsPY" Option 1 >> ../conf/messages.en
echo "whosHadBenefitsPY.option2 = whosHadBenefitsPY" Option 2 >> ../conf/messages.en
echo "whosHadBenefitsPY.checkYourAnswersLabel = whosHadBenefitsPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whosHadBenefitsPY: Option[String] = cacheMap.getEntry[String](WhosHadBenefitsPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whosHadBenefitsPY: Option[AnswerRow] = userAnswers.whosHadBenefitsPY map {";\
     print "    x => AnswerRow(\"whosHadBenefitsPY.checkYourAnswersLabel\", s\"whosHadBenefitsPY.$x\", true, routes.WhosHadBenefitsPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhosHadBenefitsPY complete"
