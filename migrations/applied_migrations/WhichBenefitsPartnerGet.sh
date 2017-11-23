#!/bin/bash

echo "Applying migration WhichBenefitsPartnerGet"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichBenefitsPartnerGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsPartnerGetController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichBenefitsPartnerGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsPartnerGetController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichBenefitsPartnerGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsPartnerGetController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichBenefitsPartnerGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsPartnerGetController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichBenefitsPartnerGet.title = whichBenefitsPartnerGet" >> ../conf/messages.en
echo "whichBenefitsPartnerGet.heading = whichBenefitsPartnerGet" >> ../conf/messages.en
echo "whichBenefitsPartnerGet.option1 = whichBenefitsPartnerGet" Option 1 >> ../conf/messages.en
echo "whichBenefitsPartnerGet.option2 = whichBenefitsPartnerGet" Option 2 >> ../conf/messages.en
echo "whichBenefitsPartnerGet.checkYourAnswersLabel = whichBenefitsPartnerGet" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whichBenefitsPartnerGet: Option[Set[String]] = cacheMap.getEntry[Set[String]](WhichBenefitsPartnerGetId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whichBenefitsPartnerGet: Option[AnswerRow] = userAnswers.whichBenefitsPartnerGet map {";\
     print "    x => AnswerRow(\"whichBenefitsPartnerGet.checkYourAnswersLabel\", s\"whichBenefitsPartnerGet.$x\", true, routes.WhichBenefitsPartnerGetController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhichBenefitsPartnerGet complete"
