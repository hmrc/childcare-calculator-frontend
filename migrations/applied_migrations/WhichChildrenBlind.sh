#!/bin/bash

echo "Applying migration WhichChildrenBlind"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichChildrenBlind               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichChildrenBlindController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichChildrenBlind               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichChildrenBlindController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichChildrenBlind               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichChildrenBlindController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichChildrenBlind               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichChildrenBlindController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichChildrenBlind.title = whichChildrenBlind" >> ../conf/messages.en
echo "whichChildrenBlind.heading = whichChildrenBlind" >> ../conf/messages.en
echo "whichChildrenBlind.option1 = whichChildrenBlind" Option 1 >> ../conf/messages.en
echo "whichChildrenBlind.option2 = whichChildrenBlind" Option 2 >> ../conf/messages.en
echo "whichChildrenBlind.checkYourAnswersLabel = whichChildrenBlind" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whichChildrenBlind: Option[Set[String]] = cacheMap.getEntry[Set[String]](WhichChildrenBlindId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whichChildrenBlind: Option[AnswerRow] = userAnswers.whichChildrenBlind map {";\
     print "    x => AnswerRow(\"whichChildrenBlind.checkYourAnswersLabel\", s\"whichChildrenBlind.$x\", true, routes.WhichChildrenBlindController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhichChildrenBlind complete"
