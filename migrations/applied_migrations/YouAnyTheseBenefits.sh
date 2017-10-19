#!/bin/bash

echo "Applying migration YouAnyTheseBenefits"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /youAnyTheseBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouAnyTheseBenefitsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /youAnyTheseBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouAnyTheseBenefitsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYouAnyTheseBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouAnyTheseBenefitsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYouAnyTheseBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouAnyTheseBenefitsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "youAnyTheseBenefits.title = youAnyTheseBenefits" >> ../conf/messages.en
echo "youAnyTheseBenefits.heading = youAnyTheseBenefits" >> ../conf/messages.en
echo "youAnyTheseBenefits.checkYourAnswersLabel = youAnyTheseBenefits" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def youAnyTheseBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](YouAnyTheseBenefitsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def youAnyTheseBenefits: Option[AnswerRow] = userAnswers.youAnyTheseBenefits map {";\
     print "    x => AnswerRow(\"youAnyTheseBenefits.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.YouAnyTheseBenefitsController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YouAnyTheseBenefits complete"
