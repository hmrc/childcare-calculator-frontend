#!/bin/bash

echo "Applying migration HowMuchPartnerPayPension"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /howMuchPartnerPayPension               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchPartnerPayPensionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /howMuchPartnerPayPension               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchPartnerPayPensionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHowMuchPartnerPayPension               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchPartnerPayPensionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHowMuchPartnerPayPension               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchPartnerPayPensionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "howMuchPartnerPayPension.title = howMuchPartnerPayPension" >> ../conf/messages.en
echo "howMuchPartnerPayPension.heading = howMuchPartnerPayPension" >> ../conf/messages.en
echo "howMuchPartnerPayPension.checkYourAnswersLabel = howMuchPartnerPayPension" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def howMuchPartnerPayPension: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](HowMuchPartnerPayPensionId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def howMuchPartnerPayPension: Option[AnswerRow] = userAnswers.howMuchPartnerPayPension map {";\
     print "    x => AnswerRow(\"howMuchPartnerPayPension.checkYourAnswersLabel\", s\"$x\", false, routes.HowMuchPartnerPayPensionController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HowMuchPartnerPayPension complete"
