#!/bin/bash

echo "Applying migration PartnerPaidPensionPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerPaidPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidPensionPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerPaidPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidPensionPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerPaidPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidPensionPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerPaidPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidPensionPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerPaidPensionPY.title = partnerPaidPensionPY" >> ../conf/messages.en
echo "partnerPaidPensionPY.heading = partnerPaidPensionPY" >> ../conf/messages.en
echo "partnerPaidPensionPY.checkYourAnswersLabel = partnerPaidPensionPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerPaidPensionPY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidPensionPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerPaidPensionPY: Option[AnswerRow] = userAnswers.partnerPaidPensionPY map {";\
     print "    x => AnswerRow(\"partnerPaidPensionPY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerPaidPensionPYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerPaidPensionPY complete"
