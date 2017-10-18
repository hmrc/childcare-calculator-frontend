#!/bin/bash

echo "Applying migration YourOtherIncomeAmountCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourOtherIncomeAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourOtherIncomeAmountCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourOtherIncomeAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourOtherIncomeAmountCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourOtherIncomeAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourOtherIncomeAmountCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourOtherIncomeAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourOtherIncomeAmountCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourOtherIncomeAmountCY.title = yourOtherIncomeAmountCY" >> ../conf/messages.en
echo "yourOtherIncomeAmountCY.heading = yourOtherIncomeAmountCY" >> ../conf/messages.en
echo "yourOtherIncomeAmountCY.checkYourAnswersLabel = yourOtherIncomeAmountCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourOtherIncomeAmountCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YourOtherIncomeAmountCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourOtherIncomeAmountCY: Option[AnswerRow] = userAnswers.yourOtherIncomeAmountCY map {";\
     print "    x => AnswerRow(\"yourOtherIncomeAmountCY.checkYourAnswersLabel\", s\"$x\", false, routes.YourOtherIncomeAmountCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourOtherIncomeAmountCY complete"
