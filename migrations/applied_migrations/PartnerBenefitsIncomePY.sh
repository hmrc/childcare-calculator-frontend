#!/bin/bash

echo "Applying migration PartnerBenefitsIncomePY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerBenefitsIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerBenefitsIncomePYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerBenefitsIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerBenefitsIncomePYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerBenefitsIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerBenefitsIncomePYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerBenefitsIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerBenefitsIncomePYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerBenefitsIncomePY.title = partnerBenefitsIncomePY" >> ../conf/messages.en
echo "partnerBenefitsIncomePY.heading = partnerBenefitsIncomePY" >> ../conf/messages.en
echo "partnerBenefitsIncomePY.checkYourAnswersLabel = partnerBenefitsIncomePY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerBenefitsIncomePY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerBenefitsIncomePYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerBenefitsIncomePY: Option[AnswerRow] = userAnswers.partnerBenefitsIncomePY map {";\
     print "    x => AnswerRow(\"partnerBenefitsIncomePY.checkYourAnswersLabel\", s\"$x\", false, routes.PartnerBenefitsIncomePYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerBenefitsIncomePY complete"
