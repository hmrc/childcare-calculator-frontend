#!/bin/bash

echo "Applying migration WhichChildrenDisability"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichChildrenDisability               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichChildrenDisabilityController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichChildrenDisability               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichChildrenDisabilityController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichChildrenDisability               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichChildrenDisabilityController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichChildrenDisability               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichChildrenDisabilityController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichChildrenDisability.title = whichChildrenDisability" >> ../conf/messages.en
echo "whichChildrenDisability.heading = whichChildrenDisability" >> ../conf/messages.en
echo "whichChildrenDisability.option1 = whichChildrenDisability" Option 1 >> ../conf/messages.en
echo "whichChildrenDisability.option2 = whichChildrenDisability" Option 2 >> ../conf/messages.en
echo "whichChildrenDisability.checkYourAnswersLabel = whichChildrenDisability" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whichChildrenDisability: Option[Set[String]] = cacheMap.getEntry[Set[String]](WhichChildrenDisabilityId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whichChildrenDisability: Option[AnswerRow] = userAnswers.whichChildrenDisability map {";\
     print "    x => AnswerRow(\"whichChildrenDisability.checkYourAnswersLabel\", s\"whichChildrenDisability.$x\", true, routes.WhichChildrenDisabilityController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhichChildrenDisability complete"
