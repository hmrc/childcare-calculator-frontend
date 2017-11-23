#!/bin/bash

echo "Applying migration WhoGetsOtherIncomeCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoGetsOtherIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsOtherIncomeCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoGetsOtherIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsOtherIncomeCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoGetsOtherIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsOtherIncomeCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoGetsOtherIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsOtherIncomeCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoGetsOtherIncomeCY.title = whoGetsOtherIncomeCY" >> ../conf/messages.en
echo "whoGetsOtherIncomeCY.heading = whoGetsOtherIncomeCY" >> ../conf/messages.en
echo "whoGetsOtherIncomeCY.option1 = whoGetsOtherIncomeCY" Option 1 >> ../conf/messages.en
echo "whoGetsOtherIncomeCY.option2 = whoGetsOtherIncomeCY" Option 2 >> ../conf/messages.en
echo "whoGetsOtherIncomeCY.checkYourAnswersLabel = whoGetsOtherIncomeCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whoGetsOtherIncomeCY: Option[String] = cacheMap.getEntry[String](WhoGetsOtherIncomeCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whoGetsOtherIncomeCY: Option[AnswerRow] = userAnswers.whoGetsOtherIncomeCY map {";\
     print "    x => AnswerRow(\"whoGetsOtherIncomeCY.checkYourAnswersLabel\", s\"whoGetsOtherIncomeCY.$x\", true, routes.WhoGetsOtherIncomeCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhoGetsOtherIncomeCY complete"
