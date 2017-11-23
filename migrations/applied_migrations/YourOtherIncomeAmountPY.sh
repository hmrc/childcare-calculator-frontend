#!/bin/bash

echo "Applying migration YourOtherIncomeAmountPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourOtherIncomeAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourOtherIncomeAmountPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourOtherIncomeAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourOtherIncomeAmountPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourOtherIncomeAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourOtherIncomeAmountPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourOtherIncomeAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourOtherIncomeAmountPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourOtherIncomeAmountPY.title = yourOtherIncomeAmountPY" >> ../conf/messages.en
echo "yourOtherIncomeAmountPY.heading = yourOtherIncomeAmountPY" >> ../conf/messages.en
echo "yourOtherIncomeAmountPY.checkYourAnswersLabel = yourOtherIncomeAmountPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourOtherIncomeAmountPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YourOtherIncomeAmountPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourOtherIncomeAmountPY: Option[AnswerRow] = userAnswers.yourOtherIncomeAmountPY map {";\
     print "    x => AnswerRow(\"yourOtherIncomeAmountPY.checkYourAnswersLabel\", s\"$x\", false, routes.YourOtherIncomeAmountPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourOtherIncomeAmountPY complete"
