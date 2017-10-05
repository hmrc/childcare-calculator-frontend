#!/bin/bash

echo "Applying migration DoYouKnowYourPartnersAdjustedTaxCode"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doYouKnowYourPartnersAdjustedTaxCode                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /doYouKnowYourPartnersAdjustedTaxCode                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouKnowYourPartnersAdjustedTaxCodeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDoYouKnowYourPartnersAdjustedTaxCode                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDoYouKnowYourPartnersAdjustedTaxCode                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouKnowYourPartnersAdjustedTaxCodeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doYouKnowYourPartnersAdjustedTaxCode.title = doYouKnowYourPartnersAdjustedTaxCode" >> ../conf/messages.en
echo "doYouKnowYourPartnersAdjustedTaxCode.heading = doYouKnowYourPartnersAdjustedTaxCode" >> ../conf/messages.en
echo "doYouKnowYourPartnersAdjustedTaxCode.checkYourAnswersLabel = doYouKnowYourPartnersAdjustedTaxCode" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def doYouKnowYourPartnersAdjustedTaxCode: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouKnowYourPartnersAdjustedTaxCodeId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def doYouKnowYourPartnersAdjustedTaxCode: Option[AnswerRow] = userAnswers.doYouKnowYourPartnersAdjustedTaxCode map {";\
     print "    x => AnswerRow(\"doYouKnowYourPartnersAdjustedTaxCode.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouKnowYourPartnersAdjustedTaxCode complete"
