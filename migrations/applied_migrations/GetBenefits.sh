#!/bin/bash

echo "Applying migration GetBenefits"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /getBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.GetBenefitsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /getBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.GetBenefitsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeGetBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.GetBenefitsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeGetBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.GetBenefitsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "getBenefits.title = getBenefits" >> ../conf/messages.en
echo "getBenefits.heading = getBenefits" >> ../conf/messages.en
echo "getBenefits.checkYourAnswersLabel = getBenefits" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def getBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](GetBenefitsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def getBenefits: Option[AnswerRow] = userAnswers.getBenefits map {";\
     print "    x => AnswerRow(\"getBenefits.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.GetBenefitsController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration GetBenefits complete"
