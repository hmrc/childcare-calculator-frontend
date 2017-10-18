#!/bin/bash

echo "Applying migration PartnerStatutoryPayCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerStatutoryPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerStatutoryPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerStatutoryPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerStatutoryPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerStatutoryPayCY.title = partnerStatutoryPayCY" >> ../conf/messages.en
echo "partnerStatutoryPayCY.heading = partnerStatutoryPayCY" >> ../conf/messages.en
echo "partnerStatutoryPayCY.checkYourAnswersLabel = partnerStatutoryPayCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerStatutoryPayCY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerStatutoryPayCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerStatutoryPayCY: Option[AnswerRow] = userAnswers.partnerStatutoryPayCY map {";\
     print "    x => AnswerRow(\"partnerStatutoryPayCY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerStatutoryPayCYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerStatutoryPayCY complete"
