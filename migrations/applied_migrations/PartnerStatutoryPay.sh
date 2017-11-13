#!/bin/bash

echo "Applying migration PartnerStatutoryPay"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerStatutoryPay                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerStatutoryPay                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerStatutoryPay                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerStatutoryPay                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerStatutoryPay.title = partnerStatutoryPay" >> ../conf/messages.en
echo "partnerStatutoryPay.heading = partnerStatutoryPay" >> ../conf/messages.en
echo "partnerStatutoryPay.checkYourAnswersLabel = partnerStatutoryPay" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerStatutoryPay: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerStatutoryPayId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerStatutoryPay: Option[AnswerRow] = userAnswers.partnerStatutoryPay map {";\
     print "    x => AnswerRow(\"partnerStatutoryPay.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerStatutoryPayController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerStatutoryPay complete"
