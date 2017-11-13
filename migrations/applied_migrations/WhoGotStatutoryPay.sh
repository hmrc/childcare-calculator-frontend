#!/bin/bash

echo "Applying migration WhoGotStatutoryPay"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoGotStatutoryPay               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGotStatutoryPayController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoGotStatutoryPay               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGotStatutoryPayController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoGotStatutoryPay               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGotStatutoryPayController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoGotStatutoryPay               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGotStatutoryPayController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoGotStatutoryPay.title = whoGotStatutoryPay" >> ../conf/messages.en
echo "whoGotStatutoryPay.heading = whoGotStatutoryPay" >> ../conf/messages.en
echo "whoGotStatutoryPay.option1 = whoGotStatutoryPay" Option 1 >> ../conf/messages.en
echo "whoGotStatutoryPay.option2 = whoGotStatutoryPay" Option 2 >> ../conf/messages.en
echo "whoGotStatutoryPay.checkYourAnswersLabel = whoGotStatutoryPay" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whoGotStatutoryPay: Option[String] = cacheMap.getEntry[String](WhoGotStatutoryPayId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whoGotStatutoryPay: Option[AnswerRow] = userAnswers.whoGotStatutoryPay map {";\
     print "    x => AnswerRow(\"whoGotStatutoryPay.checkYourAnswersLabel\", s\"whoGotStatutoryPay.$x\", true, routes.WhoGotStatutoryPayController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhoGotStatutoryPay complete"
