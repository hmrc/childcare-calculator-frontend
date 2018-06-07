#!/bin/bash

echo "Applying migration PartnerMaximumEarnings"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerMaximumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerMaximumEarningsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerMaximumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerMaximumEarningsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerMaximumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerMaximumEarningsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerMaximumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerMaximumEarningsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages
echo "partnerMaximumEarnings.title = partnerMaximumEarnings" >> ../conf/messages
echo "partnerMaximumEarnings.heading = partnerMaximumEarnings" >> ../conf/messages
echo "partnerMaximumEarnings.checkYourAnswersLabel = partnerMaximumEarnings" >> ../conf/messages

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerMaximumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerMaximumEarningsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerMaximumEarnings: Option[AnswerRow] = userAnswers.partnerMaximumEarnings map {";\
     print "    x => AnswerRow(\"partnerMaximumEarnings.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerMaximumEarningsController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerMaximumEarnings complete"
