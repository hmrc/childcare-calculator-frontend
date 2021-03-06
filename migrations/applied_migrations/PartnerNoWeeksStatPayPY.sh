#!/bin/bash

echo "Applying migration PartnerNoWeeksStatPayPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerNoWeeksStatPayPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerNoWeeksStatPayPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerNoWeeksStatPayPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerNoWeeksStatPayPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerNoWeeksStatPayPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerNoWeeksStatPayPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerNoWeeksStatPayPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerNoWeeksStatPayPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerNoWeeksStatPayPY.title = partnerNoWeeksStatPayPY" >> ../conf/messages.en
echo "partnerNoWeeksStatPayPY.heading = partnerNoWeeksStatPayPY" >> ../conf/messages.en
echo "partnerNoWeeksStatPayPY.checkYourAnswersLabel = partnerNoWeeksStatPayPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerNoWeeksStatPayPY: Option[Int] = cacheMap.getEntry[Int](PartnerNoWeeksStatPayPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerNoWeeksStatPayPY: Option[AnswerRow] = userAnswers.partnerNoWeeksStatPayPY map {";\
     print "    x => AnswerRow(\"partnerNoWeeksStatPayPY.checkYourAnswersLabel\", s\"$x\", false, routes.PartnerNoWeeksStatPayPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerNoWeeksStatPayPY complete"
