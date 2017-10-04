#!/bin/bash

echo "Applying migration WhichBenefitsYourPartnerGet"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichBenefitsYourPartnerGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsYourPartnerGetController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichBenefitsYourPartnerGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsYourPartnerGetController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichBenefitsYourPartnerGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsYourPartnerGetController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichBenefitsYourPartnerGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsYourPartnerGetController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichBenefitsYourPartnerGet.title = whichBenefitsYourPartnerGet" >> ../conf/messages.en
echo "whichBenefitsYourPartnerGet.heading = whichBenefitsYourPartnerGet" >> ../conf/messages.en
echo "whichBenefitsYourPartnerGet.option1 = whichBenefitsYourPartnerGet" Option 1 >> ../conf/messages.en
echo "whichBenefitsYourPartnerGet.option2 = whichBenefitsYourPartnerGet" Option 2 >> ../conf/messages.en
echo "whichBenefitsYourPartnerGet.checkYourAnswersLabel = whichBenefitsYourPartnerGet" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whichBenefitsYourPartnerGet: Option[String] = cacheMap.getEntry[String](WhichBenefitsYourPartnerGetId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whichBenefitsYourPartnerGet: Option[AnswerRow] = userAnswers.whichBenefitsYourPartnerGet map {";\
     print "    x => AnswerRow(\"whichBenefitsYourPartnerGet.checkYourAnswersLabel\", s\"whichBenefitsYourPartnerGet.$x\", true, routes.WhichBenefitsYourPartnerGetController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhichBenefitsYourPartnerGet complete"
