#!/bin/bash

echo "Applying migration PartnerAnyTheseBenefitsPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerAnyTheseBenefitsPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerAnyTheseBenefitsPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerAnyTheseBenefitsPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerAnyTheseBenefitsPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerAnyTheseBenefitsPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerAnyTheseBenefitsPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerAnyTheseBenefitsPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerAnyTheseBenefitsPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages
echo "partnerAnyTheseBenefitsPY.title = partnerAnyTheseBenefitsPY" >> ../conf/messages
echo "partnerAnyTheseBenefitsPY.heading = partnerAnyTheseBenefitsPY" >> ../conf/messages
echo "partnerAnyTheseBenefitsPY.checkYourAnswersLabel = partnerAnyTheseBenefitsPY" >> ../conf/messages

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerAnyTheseBenefitsPY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerAnyTheseBenefitsPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerAnyTheseBenefitsPY: Option[AnswerRow] = userAnswers.partnerAnyTheseBenefitsPY map {";\
     print "    x => AnswerRow(\"partnerAnyTheseBenefitsPY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerAnyTheseBenefitsPYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerAnyTheseBenefitsPY complete"
