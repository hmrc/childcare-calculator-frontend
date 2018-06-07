#!/bin/bash

echo "Applying migration PartnerBenefitsIncomeCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerBenefitsIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerBenefitsIncomeCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerBenefitsIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerBenefitsIncomeCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerBenefitsIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerBenefitsIncomeCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerBenefitsIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerBenefitsIncomeCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages
echo "partnerBenefitsIncomeCY.title = partnerBenefitsIncomeCY" >> ../conf/messages
echo "partnerBenefitsIncomeCY.heading = partnerBenefitsIncomeCY" >> ../conf/messages
echo "partnerBenefitsIncomeCY.checkYourAnswersLabel = partnerBenefitsIncomeCY" >> ../conf/messages

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerBenefitsIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerBenefitsIncomeCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerBenefitsIncomeCY: Option[AnswerRow] = userAnswers.partnerBenefitsIncomeCY map {";\
     print "    x => AnswerRow(\"partnerBenefitsIncomeCY.checkYourAnswersLabel\", s\"$x\", false, routes.PartnerBenefitsIncomeCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerBenefitsIncomeCY complete"
