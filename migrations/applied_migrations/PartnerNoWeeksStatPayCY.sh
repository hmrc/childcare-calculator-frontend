#!/bin/bash

echo "Applying migration PartnerNoWeeksStatPayCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerNoWeeksStatPayCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerNoWeeksStatPayCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerNoWeeksStatPayCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerNoWeeksStatPayCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerNoWeeksStatPayCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerNoWeeksStatPayCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerNoWeeksStatPayCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerNoWeeksStatPayCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerNoWeeksStatPayCY.title = partnerNoWeeksStatPayCY" >> ../conf/messages.en
echo "partnerNoWeeksStatPayCY.heading = partnerNoWeeksStatPayCY" >> ../conf/messages.en
echo "partnerNoWeeksStatPayCY.checkYourAnswersLabel = partnerNoWeeksStatPayCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerNoWeeksStatPayCY: Option[Int] = cacheMap.getEntry[Int](PartnerNoWeeksStatPayCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerNoWeeksStatPayCY: Option[AnswerRow] = userAnswers.partnerNoWeeksStatPayCY map {";\
     print "    x => AnswerRow(\"partnerNoWeeksStatPayCY.checkYourAnswersLabel\", s\"$x\", false, routes.PartnerNoWeeksStatPayCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerNoWeeksStatPayCY complete"
