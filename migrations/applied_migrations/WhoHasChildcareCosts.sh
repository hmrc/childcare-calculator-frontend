#!/bin/bash

echo "Applying migration WhoHasChildcareCosts"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoHasChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoHasChildcareCostsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoHasChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoHasChildcareCostsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoHasChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoHasChildcareCostsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoHasChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoHasChildcareCostsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoHasChildcareCosts.title = whoHasChildcareCosts" >> ../conf/messages.en
echo "whoHasChildcareCosts.heading = whoHasChildcareCosts" >> ../conf/messages.en
echo "whoHasChildcareCosts.option1 = whoHasChildcareCosts" Option 1 >> ../conf/messages.en
echo "whoHasChildcareCosts.option2 = whoHasChildcareCosts" Option 2 >> ../conf/messages.en
echo "whoHasChildcareCosts.checkYourAnswersLabel = whoHasChildcareCosts" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whoHasChildcareCosts: Option[Set[String]] = cacheMap.getEntry[Set[String]](WhoHasChildcareCostsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whoHasChildcareCosts: Option[AnswerRow] = userAnswers.whoHasChildcareCosts map {";\
     print "    x => AnswerRow(\"whoHasChildcareCosts.checkYourAnswersLabel\", s\"whoHasChildcareCosts.$x\", true, routes.WhoHasChildcareCostsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhoHasChildcareCosts complete"
