#!/bin/bash

echo "Applying migration WhoGetsStatutoryPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoGetsStatutoryPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsStatutoryPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoGetsStatutoryPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsStatutoryPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoGetsStatutoryPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsStatutoryPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoGetsStatutoryPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsStatutoryPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoGetsStatutoryPY.title = whoGetsStatutoryPY" >> ../conf/messages.en
echo "whoGetsStatutoryPY.heading = whoGetsStatutoryPY" >> ../conf/messages.en
echo "whoGetsStatutoryPY.option1 = whoGetsStatutoryPY" Option 1 >> ../conf/messages.en
echo "whoGetsStatutoryPY.option2 = whoGetsStatutoryPY" Option 2 >> ../conf/messages.en
echo "whoGetsStatutoryPY.checkYourAnswersLabel = whoGetsStatutoryPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whoGetsStatutoryPY: Option[String] = cacheMap.getEntry[String](WhoGetsStatutoryPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whoGetsStatutoryPY: Option[AnswerRow] = userAnswers.whoGetsStatutoryPY map {";\
     print "    x => AnswerRow(\"whoGetsStatutoryPY.checkYourAnswersLabel\", s\"whoGetsStatutoryPY.$x\", true, routes.WhoGetsStatutoryPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhoGetsStatutoryPY complete"
