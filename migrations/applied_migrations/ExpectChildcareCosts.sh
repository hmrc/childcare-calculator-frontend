#!/bin/bash

if grep -Fxp ExpectChildcareCosts applied
then
    echo "Migration ExpectChildcareCosts has already been applied, exiting"
    exit 1
fi

echo "Applying migration ExpectChildcareCosts"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /expectChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ExpectChildcareCostsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /expectChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ExpectChildcareCostsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeExpectChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ExpectChildcareCostsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeExpectChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ExpectChildcareCostsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "expectChildcareCosts.title = expectChildcareCosts" >> ../conf/messages.en
echo "expectChildcareCosts.heading = expectChildcareCosts" >> ../conf/messages.en
echo "expectChildcareCosts.option1 = expectChildcareCosts" Option 1 >> ../conf/messages.en
echo "expectChildcareCosts.option2 = expectChildcareCosts" Option 2 >> ../conf/messages.en
echo "expectChildcareCosts.checkYourAnswersLabel = expectChildcareCosts" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def expectChildcareCosts: Option[String] = cacheMap.getEntry[String](ExpectChildcareCostsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def expectChildcareCosts: Option[AnswerRow] = userAnswers.expectChildcareCosts map {";\
     print "    x => AnswerRow(\"expectChildcareCosts.checkYourAnswersLabel\", s\"expectChildcareCosts.$x\", true, routes.ExpectChildcareCostsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Registering this migration as completed"
echo "ExpectChildcareCosts" >> applied
