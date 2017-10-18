#!/bin/bash

echo "Applying migration PartnerPaidPension"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerPaidPension                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidPensionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerPaidPension                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidPensionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerPaidPension                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidPensionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerPaidPension                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidPensionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerPaidPension.title = partnerPaidPension" >> ../conf/messages.en
echo "partnerPaidPension.heading = partnerPaidPension" >> ../conf/messages.en
echo "partnerPaidPension.checkYourAnswersLabel = partnerPaidPension" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerPaidPension: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidPensionId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerPaidPension: Option[AnswerRow] = userAnswers.partnerPaidPension map {";\
     print "    x => AnswerRow(\"partnerPaidPension.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerPaidPensionController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerPaidPension complete"
