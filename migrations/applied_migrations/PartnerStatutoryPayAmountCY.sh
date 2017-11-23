#!/bin/bash

echo "Applying migration PartnerStatutoryPayAmountCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerStatutoryPayAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayAmountCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerStatutoryPayAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayAmountCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerStatutoryPayAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayAmountCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerStatutoryPayAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayAmountCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerStatutoryPayAmountCY.title = partnerStatutoryPayAmountCY" >> ../conf/messages.en
echo "partnerStatutoryPayAmountCY.heading = partnerStatutoryPayAmountCY" >> ../conf/messages.en
echo "partnerStatutoryPayAmountCY.checkYourAnswersLabel = partnerStatutoryPayAmountCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerStatutoryPayAmountCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerStatutoryPayAmountCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerStatutoryPayAmountCY: Option[AnswerRow] = userAnswers.partnerStatutoryPayAmountCY map {";\
     print "    x => AnswerRow(\"partnerStatutoryPayAmountCY.checkYourAnswersLabel\", s\"$x\", false, routes.PartnerStatutoryPayAmountCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerStatutoryPayAmountCY complete"
