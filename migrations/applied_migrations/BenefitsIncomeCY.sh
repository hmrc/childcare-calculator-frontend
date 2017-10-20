#!/bin/bash

echo "Applying migration BenefitsIncomeCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /benefitsIncomeCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BenefitsIncomeCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /benefitsIncomeCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BenefitsIncomeCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBenefitsIncomeCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BenefitsIncomeCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBenefitsIncomeCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BenefitsIncomeCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "benefitsIncomeCY.title = benefitsIncomeCY" >> ../conf/messages.en
echo "benefitsIncomeCY.heading = benefitsIncomeCY" >> ../conf/messages.en
echo "benefitsIncomeCY.field1 = Field 1" >> ../conf/messages.en
echo "benefitsIncomeCY.field2 = Field 2" >> ../conf/messages.en
echo "benefitsIncomeCY.checkYourAnswersLabel = benefitsIncomeCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def benefitsIncomeCY: Option[BenefitsIncomeCY] = cacheMap.getEntry[BenefitsIncomeCY](BenefitsIncomeCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def benefitsIncomeCY: Option[AnswerRow] = userAnswers.benefitsIncomeCY map {";\
     print "    x => AnswerRow(\"benefitsIncomeCY.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.BenefitsIncomeCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BenefitsIncomeCY complete"
