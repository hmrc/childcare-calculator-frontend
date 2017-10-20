#!/bin/bash

echo "Applying migration PartnerOtherIncomeAmountCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerOtherIncomeAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerOtherIncomeAmountCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerOtherIncomeAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerOtherIncomeAmountCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerOtherIncomeAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerOtherIncomeAmountCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerOtherIncomeAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerOtherIncomeAmountCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerOtherIncomeAmountCY.title = partnerOtherIncomeAmountCY" >> ../conf/messages.en
echo "partnerOtherIncomeAmountCY.heading = partnerOtherIncomeAmountCY" >> ../conf/messages.en
echo "partnerOtherIncomeAmountCY.checkYourAnswersLabel = partnerOtherIncomeAmountCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerOtherIncomeAmountCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerOtherIncomeAmountCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerOtherIncomeAmountCY: Option[AnswerRow] = userAnswers.partnerOtherIncomeAmountCY map {";\
     print "    x => AnswerRow(\"partnerOtherIncomeAmountCY.checkYourAnswersLabel\", s\"$x\", false, routes.PartnerOtherIncomeAmountCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerOtherIncomeAmountCY complete"
