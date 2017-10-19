#!/bin/bash

echo "Applying migration PartnerPaidPensionCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /PartnerPaidPensionCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidPensionCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /PartnerPaidPensionCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidPensionCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerPaidPensionCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidPensionCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerPaidPensionCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidPensionCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "PartnerPaidPensionCY.title = PartnerPaidPensionCY" >> ../conf/messages.en
echo "PartnerPaidPensionCY.heading = PartnerPaidPensionCY" >> ../conf/messages.en
echo "PartnerPaidPensionCY.checkYourAnswersLabel = PartnerPaidPensionCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def PartnerPaidPensionCY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidPensionCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def PartnerPaidPensionCY: Option[AnswerRow] = userAnswers.PartnerPaidPensionCY map {";\
     print "    x => AnswerRow(\"PartnerPaidPensionCY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerPaidPensionCYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerPaidPensionCY complete"
