#!/bin/bash

if grep -Fxp DoYouLiveWithPartner applied
then
    echo "Migration DoYouLiveWithPartner has already been applied, exiting"
    exit 1
fi

echo "Applying migration DoYouLiveWithPartner"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doYouLiveWithPartner                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouLiveWithPartnerController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /doYouLiveWithPartner                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouLiveWithPartnerController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDoYouLiveWithPartner                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouLiveWithPartnerController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDoYouLiveWithPartner                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouLiveWithPartnerController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doYouLiveWithPartner.title = doYouLiveWithPartner" >> ../conf/messages.en
echo "doYouLiveWithPartner.heading = doYouLiveWithPartner" >> ../conf/messages.en
echo "doYouLiveWithPartner.checkYourAnswersLabel = doYouLiveWithPartner" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def doYouLiveWithPartner: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouLiveWithPartnerId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def doYouLiveWithPartner: Option[AnswerRow] = userAnswers.doYouLiveWithPartner map {";\
     print "    x => AnswerRow(\"doYouLiveWithPartner.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.DoYouLiveWithPartnerController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Registering this migration as applied"
echo "DoYouLiveWithPartner" >> applied
