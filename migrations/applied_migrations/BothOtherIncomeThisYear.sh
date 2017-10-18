#!/bin/bash

echo "Applying migration BothOtherIncomeThisYear"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothOtherIncomeThisYear                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothOtherIncomeThisYearController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothOtherIncomeThisYear                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothOtherIncomeThisYearController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothOtherIncomeThisYear                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothOtherIncomeThisYearController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothOtherIncomeThisYear                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothOtherIncomeThisYearController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothOtherIncomeThisYear.title = bothOtherIncomeThisYear" >> ../conf/messages.en
echo "bothOtherIncomeThisYear.heading = bothOtherIncomeThisYear" >> ../conf/messages.en
echo "bothOtherIncomeThisYear.checkYourAnswersLabel = bothOtherIncomeThisYear" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothOtherIncomeThisYear: Option[Boolean] = cacheMap.getEntry[Boolean](BothOtherIncomeThisYearId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothOtherIncomeThisYear: Option[AnswerRow] = userAnswers.bothOtherIncomeThisYear map {";\
     print "    x => AnswerRow(\"bothOtherIncomeThisYear.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.BothOtherIncomeThisYearController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothOtherIncomeThisYear complete"
