#!/bin/bash

echo "Applying migration PartnerStatutoryPayBeforeTax"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerStatutoryPayBeforeTax               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayBeforeTaxController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerStatutoryPayBeforeTax               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayBeforeTaxController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerStatutoryPayBeforeTax               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayBeforeTaxController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerStatutoryPayBeforeTax               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerStatutoryPayBeforeTaxController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerStatutoryPayBeforeTax.title = partnerStatutoryPayBeforeTax" >> ../conf/messages.en
echo "partnerStatutoryPayBeforeTax.heading = partnerStatutoryPayBeforeTax" >> ../conf/messages.en
echo "partnerStatutoryPayBeforeTax.option1 = partnerStatutoryPayBeforeTax" Option 1 >> ../conf/messages.en
echo "partnerStatutoryPayBeforeTax.option2 = partnerStatutoryPayBeforeTax" Option 2 >> ../conf/messages.en
echo "partnerStatutoryPayBeforeTax.checkYourAnswersLabel = partnerStatutoryPayBeforeTax" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerStatutoryPayBeforeTax: Option[String] = cacheMap.getEntry[String](PartnerStatutoryPayBeforeTaxId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerStatutoryPayBeforeTax: Option[AnswerRow] = userAnswers.partnerStatutoryPayBeforeTax map {";\
     print "    x => AnswerRow(\"partnerStatutoryPayBeforeTax.checkYourAnswersLabel\", s\"partnerStatutoryPayBeforeTax.$x\", true, routes.PartnerStatutoryPayBeforeTaxController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerStatutoryPayBeforeTax complete"
