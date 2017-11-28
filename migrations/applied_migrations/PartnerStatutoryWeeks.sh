#!/bin/bash

echo "Applying migration PartnerStatutoryWeeks"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerStatutoryWeeks               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryWeeksController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerStatutoryWeeks               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryWeeksController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerStatutoryWeeks               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryWeeksController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerStatutoryWeeks               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryWeeksController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerStatutoryWeeks.title = partnerStatutoryWeeks" >> ../conf/messages.en
echo "partnerStatutoryWeeks.heading = partnerStatutoryWeeks" >> ../conf/messages.en
echo "partnerStatutoryWeeks.checkYourAnswersLabel = partnerStatutoryWeeks" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerStatutoryWeeks: Option[Int] = cacheMap.getEntry[Int](PartnerStatutoryWeeksId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerStatutoryWeeks: Option[AnswerRow] = userAnswers.partnerStatutoryWeeks map {";\
     print "    x => AnswerRow(\"partnerStatutoryWeeks.checkYourAnswersLabel\", s\"$x\", false, routes.PartnerStatutoryWeeksController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerStatutoryWeeks complete"
