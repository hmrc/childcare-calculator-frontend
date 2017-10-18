#!/bin/bash

echo "Applying migration PartnerAnyTheseBenefitsCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerAnyTheseBenefitsCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerAnyTheseBenefitsCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerAnyTheseBenefitsCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerAnyTheseBenefitsCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerAnyTheseBenefitsCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerAnyTheseBenefitsCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerAnyTheseBenefitsCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerAnyTheseBenefitsCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerAnyTheseBenefitsCY.title = partnerAnyTheseBenefitsCY" >> ../conf/messages.en
echo "partnerAnyTheseBenefitsCY.heading = partnerAnyTheseBenefitsCY" >> ../conf/messages.en
echo "partnerAnyTheseBenefitsCY.checkYourAnswersLabel = partnerAnyTheseBenefitsCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerAnyTheseBenefitsCY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerAnyTheseBenefitsCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerAnyTheseBenefitsCY: Option[AnswerRow] = userAnswers.partnerAnyTheseBenefitsCY map {";\
     print "    x => AnswerRow(\"partnerAnyTheseBenefitsCY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerAnyTheseBenefitsCYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerAnyTheseBenefitsCY complete"
