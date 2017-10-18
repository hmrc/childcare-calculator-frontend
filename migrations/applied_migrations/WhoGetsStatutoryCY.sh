#!/bin/bash

echo "Applying migration WhoGetsStatutoryCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoGetsStatutoryCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsStatutoryCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoGetsStatutoryCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsStatutoryCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoGetsStatutoryCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsStatutoryCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoGetsStatutoryCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsStatutoryCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoGetsStatutoryCY.title = whoGetsStatutoryCY" >> ../conf/messages.en
echo "whoGetsStatutoryCY.heading = whoGetsStatutoryCY" >> ../conf/messages.en
echo "whoGetsStatutoryCY.option1 = whoGetsStatutoryCY" Option 1 >> ../conf/messages.en
echo "whoGetsStatutoryCY.option2 = whoGetsStatutoryCY" Option 2 >> ../conf/messages.en
echo "whoGetsStatutoryCY.checkYourAnswersLabel = whoGetsStatutoryCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whoGetsStatutoryCY: Option[String] = cacheMap.getEntry[String](WhoGetsStatutoryCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whoGetsStatutoryCY: Option[AnswerRow] = userAnswers.whoGetsStatutoryCY map {";\
     print "    x => AnswerRow(\"whoGetsStatutoryCY.checkYourAnswersLabel\", s\"whoGetsStatutoryCY.$x\", true, routes.WhoGetsStatutoryCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhoGetsStatutoryCY complete"
