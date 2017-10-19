#!/bin/bash

echo "Applying migration WhoOtherIncomePY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoOtherIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoOtherIncomePYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoOtherIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoOtherIncomePYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoOtherIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoOtherIncomePYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoOtherIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoOtherIncomePYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoOtherIncomePY.title = whoOtherIncomePY" >> ../conf/messages.en
echo "whoOtherIncomePY.heading = whoOtherIncomePY" >> ../conf/messages.en
echo "whoOtherIncomePY.option1 = whoOtherIncomePY" Option 1 >> ../conf/messages.en
echo "whoOtherIncomePY.option2 = whoOtherIncomePY" Option 2 >> ../conf/messages.en
echo "whoOtherIncomePY.checkYourAnswersLabel = whoOtherIncomePY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whoOtherIncomePY: Option[String] = cacheMap.getEntry[String](WhoOtherIncomePYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whoOtherIncomePY: Option[AnswerRow] = userAnswers.whoOtherIncomePY map {";\
     print "    x => AnswerRow(\"whoOtherIncomePY.checkYourAnswersLabel\", s\"whoOtherIncomePY.$x\", true, routes.WhoOtherIncomePYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhoOtherIncomePY complete"
