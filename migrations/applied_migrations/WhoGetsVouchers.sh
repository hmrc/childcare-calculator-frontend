#!/bin/bash

echo "Applying migration WhoGetsVouchers"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoGetsVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsVouchersController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoGetsVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsVouchersController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoGetsVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsVouchersController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoGetsVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsVouchersController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoGetsVouchers.title = whoGetsVouchers" >> ../conf/messages.en
echo "whoGetsVouchers.heading = whoGetsVouchers" >> ../conf/messages.en
echo "whoGetsVouchers.option1 = whoGetsVouchers" Option 1 >> ../conf/messages.en
echo "whoGetsVouchers.option2 = whoGetsVouchers" Option 2 >> ../conf/messages.en
echo "whoGetsVouchers.checkYourAnswersLabel = whoGetsVouchers" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whoGetsVouchers: Option[String] = cacheMap.getEntry[String](WhoGetsVouchersId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whoGetsVouchers: Option[AnswerRow] = userAnswers.whoGetsVouchers map {";\
     print "    x => AnswerRow(\"whoGetsVouchers.checkYourAnswersLabel\", s\"whoGetsVouchers.$x\", true, routes.WhoGetsVouchersController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhoGetsVouchers complete"
