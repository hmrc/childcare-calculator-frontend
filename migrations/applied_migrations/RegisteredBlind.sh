#!/bin/bash

echo "Applying migration RegisteredBlind"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /registeredBlind                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.RegisteredBlindController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /registeredBlind                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.RegisteredBlindController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeRegisteredBlind                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.RegisteredBlindController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeRegisteredBlind                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.RegisteredBlindController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "registeredBlind.title = registeredBlind" >> ../conf/messages.en
echo "registeredBlind.heading = registeredBlind" >> ../conf/messages.en
echo "registeredBlind.checkYourAnswersLabel = registeredBlind" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def registeredBlind: Option[Boolean] = cacheMap.getEntry[Boolean](RegisteredBlindId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def registeredBlind: Option[AnswerRow] = userAnswers.registeredBlind map {";\
     print "    x => AnswerRow(\"registeredBlind.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.RegisteredBlindController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisteredBlind complete"
