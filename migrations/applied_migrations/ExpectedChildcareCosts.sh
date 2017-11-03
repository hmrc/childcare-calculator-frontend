#!/bin/bash

echo "Applying migration ExpectedChildcareCosts"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /expectedChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ExpectedChildcareCostsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /expectedChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ExpectedChildcareCostsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeExpectedChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ExpectedChildcareCostsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeExpectedChildcareCosts               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ExpectedChildcareCostsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "expectedChildcareCosts.title = expectedChildcareCosts" >> ../conf/messages.en
echo "expectedChildcareCosts.heading = expectedChildcareCosts" >> ../conf/messages.en
echo "expectedChildcareCosts.checkYourAnswersLabel = expectedChildcareCosts" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def expectedChildcareCosts: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](ExpectedChildcareCostsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def expectedChildcareCosts: Option[AnswerRow] = userAnswers.expectedChildcareCosts map {";\
     print "    x => AnswerRow(\"expectedChildcareCosts.checkYourAnswersLabel\", s\"$x\", false, routes.ExpectedChildcareCostsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ExpectedChildcareCosts complete"
