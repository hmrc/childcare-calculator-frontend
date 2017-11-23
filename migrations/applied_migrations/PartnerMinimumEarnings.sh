#!/bin/bash

echo "Applying migration PartnerMinimumEarnings"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerMinimumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerMinimumEarningsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerMinimumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerMinimumEarningsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerMinimumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerMinimumEarningsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerMinimumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerMinimumEarningsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerMinimumEarnings.title = partnerMinimumEarnings" >> ../conf/messages.en
echo "partnerMinimumEarnings.heading = partnerMinimumEarnings" >> ../conf/messages.en
echo "partnerMinimumEarnings.checkYourAnswersLabel = partnerMinimumEarnings" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerMinimumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerMinimumEarningsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerMinimumEarnings: Option[AnswerRow] = userAnswers.partnerMinimumEarnings map {";\
     print "    x => AnswerRow(\"partnerMinimumEarnings.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerMinimumEarningsController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerMinimumEarnings complete"
