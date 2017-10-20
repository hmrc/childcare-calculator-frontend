#!/bin/bash

echo "Applying migration PartnerStatutoryPayPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerStatutoryPayPY.title = partnerStatutoryPayPY" >> ../conf/messages.en
echo "partnerStatutoryPayPY.heading = partnerStatutoryPayPY" >> ../conf/messages.en
echo "partnerStatutoryPayPY.checkYourAnswersLabel = partnerStatutoryPayPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerStatutoryPayPY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerStatutoryPayPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerStatutoryPayPY: Option[AnswerRow] = userAnswers.partnerStatutoryPayPY map {";\
     print "    x => AnswerRow(\"partnerStatutoryPayPY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerStatutoryPayPYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerStatutoryPayPY complete"
