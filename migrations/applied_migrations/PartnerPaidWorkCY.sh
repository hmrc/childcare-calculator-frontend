#!/bin/bash

echo "Applying migration PartnerPaidWorkCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerPaidWorkCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidWorkCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerPaidWorkCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidWorkCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerPaidWorkCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidWorkCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerPaidWorkCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidWorkCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerPaidWorkCY.title = partnerPaidWorkCY" >> ../conf/messages.en
echo "partnerPaidWorkCY.heading = partnerPaidWorkCY" >> ../conf/messages.en
echo "partnerPaidWorkCY.checkYourAnswersLabel = partnerPaidWorkCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerPaidWorkCY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidWorkCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerPaidWorkCY: Option[AnswerRow] = userAnswers.partnerPaidWorkCY map {";\
     print "    x => AnswerRow(\"partnerPaidWorkCY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerPaidWorkCYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerPaidWorkCY complete"
