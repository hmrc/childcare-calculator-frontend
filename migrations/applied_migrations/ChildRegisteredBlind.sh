#!/bin/bash

echo "Applying migration ChildRegisteredBlind"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /childRegisteredBlind                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildRegisteredBlindController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /childRegisteredBlind                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildRegisteredBlindController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeChildRegisteredBlind                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildRegisteredBlindController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeChildRegisteredBlind                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildRegisteredBlindController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "childRegisteredBlind.title = childRegisteredBlind" >> ../conf/messages.en
echo "childRegisteredBlind.heading = childRegisteredBlind" >> ../conf/messages.en
echo "childRegisteredBlind.checkYourAnswersLabel = childRegisteredBlind" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def childRegisteredBlind: Option[Boolean] = cacheMap.getEntry[Boolean](ChildRegisteredBlindId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def childRegisteredBlind: Option[AnswerRow] = userAnswers.childRegisteredBlind map {";\
     print "    x => AnswerRow(\"childRegisteredBlind.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ChildRegisteredBlindController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ChildRegisteredBlind complete"
