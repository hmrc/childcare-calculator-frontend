#!/bin/bash

echo "Applying migration WhoWasInPaidWorkPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoWasInPaidWorkPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoWasInPaidWorkPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoWasInPaidWorkPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoWasInPaidWorkPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoWasInPaidWorkPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoWasInPaidWorkPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoWasInPaidWorkPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoWasInPaidWorkPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoWasInPaidWorkPY.title = whoWasInPaidWorkPY" >> ../conf/messages.en
echo "whoWasInPaidWorkPY.heading = whoWasInPaidWorkPY" >> ../conf/messages.en
echo "whoWasInPaidWorkPY.option1 = whoWasInPaidWorkPY" Option 1 >> ../conf/messages.en
echo "whoWasInPaidWorkPY.option2 = whoWasInPaidWorkPY" Option 2 >> ../conf/messages.en
echo "whoWasInPaidWorkPY.checkYourAnswersLabel = whoWasInPaidWorkPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whoWasInPaidWorkPY: Option[String] = cacheMap.getEntry[String](WhoWasInPaidWorkPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whoWasInPaidWorkPY: Option[AnswerRow] = userAnswers.whoWasInPaidWorkPY map {";\
     print "    x => AnswerRow(\"whoWasInPaidWorkPY.checkYourAnswersLabel\", s\"whoWasInPaidWorkPY.$x\", true, routes.WhoWasInPaidWorkPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhoWasInPaidWorkPY complete"
