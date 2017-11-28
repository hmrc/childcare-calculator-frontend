#!/bin/bash

echo "Applying migration PartnerStatutoryPayType"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerStatutoryPayType               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayTypeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerStatutoryPayType               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayTypeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerStatutoryPayType               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayTypeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerStatutoryPayType               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayTypeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerStatutoryPayType.title = partnerStatutoryPayType" >> ../conf/messages.en
echo "partnerStatutoryPayType.heading = partnerStatutoryPayType" >> ../conf/messages.en
echo "partnerStatutoryPayType.option1 = partnerStatutoryPayType" Option 1 >> ../conf/messages.en
echo "partnerStatutoryPayType.option2 = partnerStatutoryPayType" Option 2 >> ../conf/messages.en
echo "partnerStatutoryPayType.checkYourAnswersLabel = partnerStatutoryPayType" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerStatutoryPayType: Option[String] = cacheMap.getEntry[String](PartnerStatutoryPayTypeId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerStatutoryPayType: Option[AnswerRow] = userAnswers.partnerStatutoryPayType map {";\
     print "    x => AnswerRow(\"partnerStatutoryPayType.checkYourAnswersLabel\", s\"partnerStatutoryPayType.$x\", true, routes.PartnerStatutoryPayTypeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerStatutoryPayType complete"
