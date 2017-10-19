#!/bin/bash

echo "Applying migration BothBenefitsIncomeCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothBenefitsIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothBenefitsIncomeCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothBenefitsIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothBenefitsIncomeCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothBenefitsIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothBenefitsIncomeCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothBenefitsIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothBenefitsIncomeCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothBenefitsIncomeCY.title = bothBenefitsIncomeCY" >> ../conf/messages.en
echo "bothBenefitsIncomeCY.heading = bothBenefitsIncomeCY" >> ../conf/messages.en
echo "bothBenefitsIncomeCY.checkYourAnswersLabel = bothBenefitsIncomeCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothBenefitsIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](BothBenefitsIncomeCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothBenefitsIncomeCY: Option[AnswerRow] = userAnswers.bothBenefitsIncomeCY map {";\
     print "    x => AnswerRow(\"bothBenefitsIncomeCY.checkYourAnswersLabel\", s\"$x\", false, routes.BothBenefitsIncomeCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothBenefitsIncomeCY complete"
