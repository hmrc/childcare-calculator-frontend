#!/bin/bash

echo "Applying migration ChildcareCosts"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /childcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildcareCostsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /childcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildcareCostsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildcareCostsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildcareCostsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "childcareCosts.title = childcareCosts" >> ../conf/messages.en
echo "childcareCosts.heading = childcareCosts" >> ../conf/messages.en
echo "childcareCosts.option1 = childcareCosts" Option 1 >> ../conf/messages.en
echo "childcareCosts.option2 = childcareCosts" Option 2 >> ../conf/messages.en
echo "childcareCosts.checkYourAnswersLabel = childcareCosts" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def childcareCosts: Option[String] = cacheMap.getEntry[String](ChildcareCostsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def childcareCosts: Option[AnswerRow] = userAnswers.childcareCosts map {";\
     print "    x => AnswerRow(\"childcareCosts.checkYourAnswersLabel\", s\"childcareCosts.$x\", true, routes.ChildcareCostsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ChildcareCosts complete"
