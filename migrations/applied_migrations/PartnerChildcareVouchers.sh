#!/bin/bash

echo "Applying migration PartnerChildcareVouchers"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerChildcareVouchersController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerChildcareVouchersController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerChildcareVouchersController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerChildcareVouchersController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerChildcareVouchers.title = partnerChildcareVouchers" >> ../conf/messages.en
echo "partnerChildcareVouchers.heading = partnerChildcareVouchers" >> ../conf/messages.en
echo "partnerChildcareVouchers.option1 = partnerChildcareVouchers" Option 1 >> ../conf/messages.en
echo "partnerChildcareVouchers.option2 = partnerChildcareVouchers" Option 2 >> ../conf/messages.en
echo "partnerChildcareVouchers.checkYourAnswersLabel = partnerChildcareVouchers" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerChildcareVouchers: Option[String] = cacheMap.getEntry[String](PartnerChildcareVouchersId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerChildcareVouchers: Option[AnswerRow] = userAnswers.partnerChildcareVouchers map {";\
     print "    x => AnswerRow(\"partnerChildcareVouchers.checkYourAnswersLabel\", s\"partnerChildcareVouchers.$x\", true, routes.PartnerChildcareVouchersController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerChildcareVouchers complete"
