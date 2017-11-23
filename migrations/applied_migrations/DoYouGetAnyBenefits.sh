#!/bin/bash

echo "Applying migration DoYouGetAnyBenefits"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doYouGetAnyBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouGetAnyBenefitsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /doYouGetAnyBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouGetAnyBenefitsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDoYouGetAnyBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouGetAnyBenefitsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDoYouGetAnyBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouGetAnyBenefitsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doYouGetAnyBenefits.title = doYouGetAnyBenefits" >> ../conf/messages.en
echo "doYouGetAnyBenefits.heading = doYouGetAnyBenefits" >> ../conf/messages.en
echo "doYouGetAnyBenefits.checkYourAnswersLabel = doYouGetAnyBenefits" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def doYouGetAnyBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouGetAnyBenefitsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def doYouGetAnyBenefits: Option[AnswerRow] = userAnswers.doYouGetAnyBenefits map {";\
     print "    x => AnswerRow(\"doYouGetAnyBenefits.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.DoYouGetAnyBenefitsController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouGetAnyBenefits complete"
