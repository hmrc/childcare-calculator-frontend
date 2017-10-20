#!/bin/bash

echo "Applying migration PartnerOtherIncomeAmountPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerOtherIncomeAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerOtherIncomeAmountPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerOtherIncomeAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerOtherIncomeAmountPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerOtherIncomeAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerOtherIncomeAmountPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerOtherIncomeAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerOtherIncomeAmountPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerOtherIncomeAmountPY.title = partnerOtherIncomeAmountPY" >> ../conf/messages.en
echo "partnerOtherIncomeAmountPY.heading = partnerOtherIncomeAmountPY" >> ../conf/messages.en
echo "partnerOtherIncomeAmountPY.checkYourAnswersLabel = partnerOtherIncomeAmountPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerOtherIncomeAmountPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerOtherIncomeAmountPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerOtherIncomeAmountPY: Option[AnswerRow] = userAnswers.partnerOtherIncomeAmountPY map {";\
     print "    x => AnswerRow(\"partnerOtherIncomeAmountPY.checkYourAnswersLabel\", s\"$x\", false, routes.PartnerOtherIncomeAmountPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerOtherIncomeAmountPY complete"
