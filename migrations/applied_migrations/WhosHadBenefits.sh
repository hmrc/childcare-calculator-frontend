#!/bin/bash

echo "Applying migration WhosHadBenefits"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whosHadBenefits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhosHadBenefitsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whosHadBenefits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhosHadBenefitsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhosHadBenefits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhosHadBenefitsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhosHadBenefits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhosHadBenefitsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whosHadBenefits.title = whosHadBenefits" >> ../conf/messages.en
echo "whosHadBenefits.heading = whosHadBenefits" >> ../conf/messages.en
echo "whosHadBenefits.option1 = whosHadBenefits" Option 1 >> ../conf/messages.en
echo "whosHadBenefits.option2 = whosHadBenefits" Option 2 >> ../conf/messages.en
echo "whosHadBenefits.checkYourAnswersLabel = whosHadBenefits" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whosHadBenefits: Option[String] = cacheMap.getEntry[String](WhosHadBenefitsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whosHadBenefits: Option[AnswerRow] = userAnswers.whosHadBenefits map {";\
     print "    x => AnswerRow(\"whosHadBenefits.checkYourAnswersLabel\", s\"whosHadBenefits.$x\", true, routes.WhosHadBenefitsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhosHadBenefits complete"
