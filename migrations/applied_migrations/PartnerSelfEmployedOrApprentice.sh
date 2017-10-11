#!/bin/bash

echo "Applying migration PartnerSelfEmployedOrApprentice"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerSelfEmployedOrApprentice               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerSelfEmployedOrApprenticeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerSelfEmployedOrApprentice               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerSelfEmployedOrApprenticeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerSelfEmployedOrApprentice               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerSelfEmployedOrApprenticeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerSelfEmployedOrApprentice               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerSelfEmployedOrApprenticeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerSelfEmployedOrApprentice.title = partnerSelfEmployedOrApprentice" >> ../conf/messages.en
echo "partnerSelfEmployedOrApprentice.heading = partnerSelfEmployedOrApprentice" >> ../conf/messages.en
echo "partnerSelfEmployedOrApprentice.option1 = partnerSelfEmployedOrApprentice" Option 1 >> ../conf/messages.en
echo "partnerSelfEmployedOrApprentice.option2 = partnerSelfEmployedOrApprentice" Option 2 >> ../conf/messages.en
echo "partnerSelfEmployedOrApprentice.checkYourAnswersLabel = partnerSelfEmployedOrApprentice" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerSelfEmployedOrApprentice: Option[String] = cacheMap.getEntry[String](PartnerSelfEmployedOrApprenticeId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerSelfEmployedOrApprentice: Option[AnswerRow] = userAnswers.partnerSelfEmployedOrApprentice map {";\
     print "    x => AnswerRow(\"partnerSelfEmployedOrApprentice.checkYourAnswersLabel\", s\"partnerSelfEmployedOrApprentice.$x\", true, routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerSelfEmployedOrApprentice complete"
