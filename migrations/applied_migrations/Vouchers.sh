#!/bin/bash

echo "Applying migration Vouchers"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.VouchersController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /vouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.VouchersController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.VouchersController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.VouchersController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "vouchers.title = vouchers" >> ../conf/messages.en
echo "vouchers.heading = vouchers" >> ../conf/messages.en
echo "vouchers.option1 = vouchers" Option 1 >> ../conf/messages.en
echo "vouchers.option2 = vouchers" Option 2 >> ../conf/messages.en
echo "vouchers.checkYourAnswersLabel = vouchers" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def vouchers: Option[String] = cacheMap.getEntry[String](VouchersId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def vouchers: Option[AnswerRow] = userAnswers.vouchers map {";\
     print "    x => AnswerRow(\"vouchers.checkYourAnswersLabel\", s\"vouchers.$x\", true, routes.VouchersController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration Vouchers complete"
