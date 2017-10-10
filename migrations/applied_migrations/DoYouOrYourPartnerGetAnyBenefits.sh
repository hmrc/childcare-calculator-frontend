#!/bin/bash

echo "Applying migration DoYouOrYourPartnerGetAnyBenefits"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doYouOrYourPartnerGetAnyBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /doYouOrYourPartnerGetAnyBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouOrYourPartnerGetAnyBenefitsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDoYouOrYourPartnerGetAnyBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDoYouOrYourPartnerGetAnyBenefits                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouOrYourPartnerGetAnyBenefitsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doYouOrYourPartnerGetAnyBenefits.title = doYouOrYourPartnerGetAnyBenefits" >> ../conf/messages.en
echo "doYouOrYourPartnerGetAnyBenefits.heading = doYouOrYourPartnerGetAnyBenefits" >> ../conf/messages.en
echo "doYouOrYourPartnerGetAnyBenefits.checkYourAnswersLabel = doYouOrYourPartnerGetAnyBenefits" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def doYouOrYourPartnerGetAnyBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouOrYourPartnerGetAnyBenefitsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def doYouOrYourPartnerGetAnyBenefits: Option[AnswerRow] = userAnswers.doYouOrYourPartnerGetAnyBenefits map {";\
     print "    x => AnswerRow(\"doYouOrYourPartnerGetAnyBenefits.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouOrYourPartnerGetAnyBenefits complete"
