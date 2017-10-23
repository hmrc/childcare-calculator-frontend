#!/bin/bash

echo "Applying migration PartnerStatutoryPayAmountPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerStatutoryPayAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayAmountPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerStatutoryPayAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayAmountPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerStatutoryPayAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayAmountPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerStatutoryPayAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayAmountPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerStatutoryPayAmountPY.title = partnerStatutoryPayAmountPY" >> ../conf/messages.en
echo "partnerStatutoryPayAmountPY.heading = partnerStatutoryPayAmountPY" >> ../conf/messages.en
echo "partnerStatutoryPayAmountPY.checkYourAnswersLabel = partnerStatutoryPayAmountPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerStatutoryPayAmountPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerStatutoryPayAmountPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerStatutoryPayAmountPY: Option[AnswerRow] = userAnswers.partnerStatutoryPayAmountPY map {";\
     print "    x => AnswerRow(\"partnerStatutoryPayAmountPY.checkYourAnswersLabel\", s\"$x\", false, routes.PartnerStatutoryPayAmountPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerStatutoryPayAmountPY complete"
