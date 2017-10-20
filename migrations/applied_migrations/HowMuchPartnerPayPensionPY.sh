#!/bin/bash

echo "Applying migration HowMuchPartnerPayPensionPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /howMuchPartnerPayPensionPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchPartnerPayPensionPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /howMuchPartnerPayPensionPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchPartnerPayPensionPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHowMuchPartnerPayPensionPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchPartnerPayPensionPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHowMuchPartnerPayPensionPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchPartnerPayPensionPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "howMuchPartnerPayPensionPY.title = howMuchPartnerPayPensionPY" >> ../conf/messages.en
echo "howMuchPartnerPayPensionPY.heading = howMuchPartnerPayPensionPY" >> ../conf/messages.en
echo "howMuchPartnerPayPensionPY.checkYourAnswersLabel = howMuchPartnerPayPensionPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def howMuchPartnerPayPensionPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](HowMuchPartnerPayPensionPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def howMuchPartnerPayPensionPY: Option[AnswerRow] = userAnswers.howMuchPartnerPayPensionPY map {";\
     print "    x => AnswerRow(\"howMuchPartnerPayPensionPY.checkYourAnswersLabel\", s\"$x\", false, routes.HowMuchPartnerPayPensionPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HowMuchPartnerPayPensionPY complete"
