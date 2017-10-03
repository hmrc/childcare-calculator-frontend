#!/bin/bash

echo "Applying migration DoYouKnowYourAdjustedTaxCode"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doYouKnowYourAdjustedTaxCode                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /doYouKnowYourAdjustedTaxCode                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouKnowYourAdjustedTaxCodeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDoYouKnowYourAdjustedTaxCode                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDoYouKnowYourAdjustedTaxCode                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoYouKnowYourAdjustedTaxCodeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doYouKnowYourAdjustedTaxCode.title = doYouKnowYourAdjustedTaxCode" >> ../conf/messages.en
echo "doYouKnowYourAdjustedTaxCode.heading = doYouKnowYourAdjustedTaxCode" >> ../conf/messages.en
echo "doYouKnowYourAdjustedTaxCode.checkYourAnswersLabel = doYouKnowYourAdjustedTaxCode" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def doYouKnowYourAdjustedTaxCode: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouKnowYourAdjustedTaxCodeId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def doYouKnowYourAdjustedTaxCode: Option[AnswerRow] = userAnswers.doYouKnowYourAdjustedTaxCode map {";\
     print "    x => AnswerRow(\"doYouKnowYourAdjustedTaxCode.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouKnowYourAdjustedTaxCode complete"
