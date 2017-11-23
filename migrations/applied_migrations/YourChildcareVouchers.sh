#!/bin/bash

echo "Applying migration YourChildcareVouchers"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourChildcareVouchersController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourChildcareVouchersController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourChildcareVouchersController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourChildcareVouchersController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourChildcareVouchers.title = yourChildcareVouchers" >> ../conf/messages.en
echo "yourChildcareVouchers.heading = yourChildcareVouchers" >> ../conf/messages.en
echo "yourChildcareVouchers.option1 = yourChildcareVouchers" Option 1 >> ../conf/messages.en
echo "yourChildcareVouchers.option2 = yourChildcareVouchers" Option 2 >> ../conf/messages.en
echo "yourChildcareVouchers.checkYourAnswersLabel = yourChildcareVouchers" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourChildcareVouchers: Option[String] = cacheMap.getEntry[String](YourChildcareVouchersId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourChildcareVouchers: Option[AnswerRow] = userAnswers.yourChildcareVouchers map {";\
     print "    x => AnswerRow(\"yourChildcareVouchers.checkYourAnswersLabel\", s\"yourChildcareVouchers.$x\", true, routes.YourChildcareVouchersController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourChildcareVouchers complete"
