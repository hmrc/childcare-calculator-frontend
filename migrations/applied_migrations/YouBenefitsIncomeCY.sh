#!/bin/bash

echo "Applying migration YouBenefitsIncomeCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /youBenefitsIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouBenefitsIncomeCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /youBenefitsIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouBenefitsIncomeCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYouBenefitsIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouBenefitsIncomeCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYouBenefitsIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouBenefitsIncomeCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "youBenefitsIncomeCY.title = youBenefitsIncomeCY" >> ../conf/messages.en
echo "youBenefitsIncomeCY.heading = youBenefitsIncomeCY" >> ../conf/messages.en
echo "youBenefitsIncomeCY.checkYourAnswersLabel = youBenefitsIncomeCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def youBenefitsIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YouBenefitsIncomeCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def youBenefitsIncomeCY: Option[AnswerRow] = userAnswers.youBenefitsIncomeCY map {";\
     print "    x => AnswerRow(\"youBenefitsIncomeCY.checkYourAnswersLabel\", s\"$x\", false, routes.YouBenefitsIncomeCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YouBenefitsIncomeCY complete"
