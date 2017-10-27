#!/bin/bash

echo "Applying migration WhichDisabilityBenefits"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichDisabilityBenefits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichDisabilityBenefitsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichDisabilityBenefits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichDisabilityBenefitsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichDisabilityBenefits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichDisabilityBenefitsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichDisabilityBenefits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichDisabilityBenefitsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichDisabilityBenefits.title = whichDisabilityBenefits" >> ../conf/messages.en
echo "whichDisabilityBenefits.heading = whichDisabilityBenefits" >> ../conf/messages.en
echo "whichDisabilityBenefits.option1 = whichDisabilityBenefits" Option 1 >> ../conf/messages.en
echo "whichDisabilityBenefits.option2 = whichDisabilityBenefits" Option 2 >> ../conf/messages.en
echo "whichDisabilityBenefits.checkYourAnswersLabel = whichDisabilityBenefits" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whichDisabilityBenefits: Option[Set[String]] = cacheMap.getEntry[Set[String]](WhichDisabilityBenefitsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whichDisabilityBenefits: Option[AnswerRow] = userAnswers.whichDisabilityBenefits map {";\
     print "    x => AnswerRow(\"whichDisabilityBenefits.checkYourAnswersLabel\", s\"whichDisabilityBenefits.$x\", true, routes.WhichDisabilityBenefitsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhichDisabilityBenefits complete"
