#!/bin/bash

echo "Applying migration PartnerWorkHours"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerWorkHours               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerWorkHoursController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerWorkHours               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerWorkHoursController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerWorkHours               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerWorkHoursController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerWorkHours               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerWorkHoursController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerWorkHours.title = partnerWorkHours" >> ../conf/messages.en
echo "partnerWorkHours.heading = partnerWorkHours" >> ../conf/messages.en
echo "partnerWorkHours.checkYourAnswersLabel = partnerWorkHours" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerWorkHours: Option[Int] = cacheMap.getEntry[Int](PartnerWorkHoursId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerWorkHours: Option[AnswerRow] = userAnswers.partnerWorkHours map {";\
     print "    x => AnswerRow(\"partnerWorkHours.checkYourAnswersLabel\", s\"$x\", false, routes.PartnerWorkHoursController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerWorkHours complete"
