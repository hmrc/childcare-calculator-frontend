#!/bin/bash

echo "Applying migration TaxOrUniversalCredits"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /taxOrUniversalCredits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.TaxOrUniversalCreditsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /taxOrUniversalCredits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.TaxOrUniversalCreditsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTaxOrUniversalCredits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.TaxOrUniversalCreditsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTaxOrUniversalCredits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.TaxOrUniversalCreditsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "taxOrUniversalCredits.title = taxOrUniversalCredits" >> ../conf/messages.en
echo "taxOrUniversalCredits.heading = taxOrUniversalCredits" >> ../conf/messages.en
echo "taxOrUniversalCredits.option1 = taxOrUniversalCredits" Option 1 >> ../conf/messages.en
echo "taxOrUniversalCredits.option2 = taxOrUniversalCredits" Option 2 >> ../conf/messages.en
echo "taxOrUniversalCredits.checkYourAnswersLabel = taxOrUniversalCredits" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def taxOrUniversalCredits: Option[String] = cacheMap.getEntry[String](TaxOrUniversalCreditsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def taxOrUniversalCredits: Option[AnswerRow] = userAnswers.taxOrUniversalCredits map {";\
     print "    x => AnswerRow(\"taxOrUniversalCredits.checkYourAnswersLabel\", s\"taxOrUniversalCredits.$x\", true, routes.TaxOrUniversalCreditsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration TaxOrUniversalCredits complete"
