#!/bin/bash

echo "Applying migration PartnerStatutoryPayPerWeek"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerStatutoryPayPerWeek               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayPerWeekController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerStatutoryPayPerWeek               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayPerWeekController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerStatutoryPayPerWeek               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayPerWeekController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerStatutoryPayPerWeek               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayPerWeekController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerStatutoryPayPerWeek.title = partnerStatutoryPayPerWeek" >> ../conf/messages.en
echo "partnerStatutoryPayPerWeek.heading = partnerStatutoryPayPerWeek" >> ../conf/messages.en
echo "partnerStatutoryPayPerWeek.checkYourAnswersLabel = partnerStatutoryPayPerWeek" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerStatutoryPayPerWeek: Option[Int] = cacheMap.getEntry[Int](PartnerStatutoryPayPerWeekId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerStatutoryPayPerWeek: Option[AnswerRow] = userAnswers.partnerStatutoryPayPerWeek map {";\
     print "    x => AnswerRow(\"partnerStatutoryPayPerWeek.checkYourAnswersLabel\", s\"$x\", false, routes.PartnerStatutoryPayPerWeekController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerStatutoryPayPerWeek complete"
