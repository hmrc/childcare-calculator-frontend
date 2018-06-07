#!/bin/bash

echo "Applying migration YouBenefitsIncomePY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /youBenefitsIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouBenefitsIncomePYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /youBenefitsIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouBenefitsIncomePYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYouBenefitsIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouBenefitsIncomePYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYouBenefitsIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouBenefitsIncomePYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages
echo "youBenefitsIncomePY.title = youBenefitsIncomePY" >> ../conf/messages
echo "youBenefitsIncomePY.heading = youBenefitsIncomePY" >> ../conf/messages
echo "youBenefitsIncomePY.checkYourAnswersLabel = youBenefitsIncomePY" >> ../conf/messages

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def youBenefitsIncomePY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YouBenefitsIncomePYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def youBenefitsIncomePY: Option[AnswerRow] = userAnswers.youBenefitsIncomePY map {";\
     print "    x => AnswerRow(\"youBenefitsIncomePY.checkYourAnswersLabel\", s\"$x\", false, routes.YouBenefitsIncomePYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YouBenefitsIncomePY complete"
