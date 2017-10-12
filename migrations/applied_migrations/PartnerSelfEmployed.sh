#!/bin/bash

echo "Applying migration PartnerSelfEmployed"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerSelfEmployed                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerSelfEmployedController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerSelfEmployed                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerSelfEmployedController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerSelfEmployed                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerSelfEmployedController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerSelfEmployed                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerSelfEmployedController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerSelfEmployed.title = partnerSelfEmployed" >> ../conf/messages.en
echo "partnerSelfEmployed.heading = partnerSelfEmployed" >> ../conf/messages.en
echo "partnerSelfEmployed.checkYourAnswersLabel = partnerSelfEmployed" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerSelfEmployed: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerSelfEmployedId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerSelfEmployed: Option[AnswerRow] = userAnswers.partnerSelfEmployed map {";\
     print "    x => AnswerRow(\"partnerSelfEmployed.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerSelfEmployedController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerSelfEmployed complete"
