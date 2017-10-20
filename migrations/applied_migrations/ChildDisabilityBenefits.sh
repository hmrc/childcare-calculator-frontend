#!/bin/bash

echo "Applying migration ChildDisabilityBenefits"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /childDisabilityBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildDisabilityBenefitsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /childDisabilityBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildDisabilityBenefitsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeChildDisabilityBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildDisabilityBenefitsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeChildDisabilityBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildDisabilityBenefitsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "childDisabilityBenefits.title = childDisabilityBenefits" >> ../conf/messages.en
echo "childDisabilityBenefits.heading = childDisabilityBenefits" >> ../conf/messages.en
echo "childDisabilityBenefits.checkYourAnswersLabel = childDisabilityBenefits" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def childDisabilityBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](ChildDisabilityBenefitsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def childDisabilityBenefits: Option[AnswerRow] = userAnswers.childDisabilityBenefits map {";\
     print "    x => AnswerRow(\"childDisabilityBenefits.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ChildDisabilityBenefitsController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ChildDisabilityBenefits complete"
