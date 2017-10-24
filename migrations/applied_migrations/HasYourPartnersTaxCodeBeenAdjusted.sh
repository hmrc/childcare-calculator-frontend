#!/bin/bash

echo "Applying migration HasYourPartnersTaxCodeBeenAdjusted"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /hasYourPartnersTaxCodeBeenAdjusted               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /hasYourPartnersTaxCodeBeenAdjusted               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HasYourPartnersTaxCodeBeenAdjustedController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHasYourPartnersTaxCodeBeenAdjusted               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHasYourPartnersTaxCodeBeenAdjusted               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HasYourPartnersTaxCodeBeenAdjustedController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "hasYourPartnersTaxCodeBeenAdjusted.title = hasYourPartnersTaxCodeBeenAdjusted" >> ../conf/messages.en
echo "hasYourPartnersTaxCodeBeenAdjusted.heading = hasYourPartnersTaxCodeBeenAdjusted" >> ../conf/messages.en
echo "hasYourPartnersTaxCodeBeenAdjusted.option1 = hasYourPartnersTaxCodeBeenAdjusted" Option 1 >> ../conf/messages.en
echo "hasYourPartnersTaxCodeBeenAdjusted.option2 = hasYourPartnersTaxCodeBeenAdjusted" Option 2 >> ../conf/messages.en
echo "hasYourPartnersTaxCodeBeenAdjusted.checkYourAnswersLabel = hasYourPartnersTaxCodeBeenAdjusted" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def hasYourPartnersTaxCodeBeenAdjusted: Option[String] = cacheMap.getEntry[String](HasYourPartnersTaxCodeBeenAdjustedId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def hasYourPartnersTaxCodeBeenAdjusted: Option[AnswerRow] = userAnswers.hasYourPartnersTaxCodeBeenAdjusted map {";\
     print "    x => AnswerRow(\"hasYourPartnersTaxCodeBeenAdjusted.checkYourAnswersLabel\", s\"hasYourPartnersTaxCodeBeenAdjusted.$x\", true, routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HasYourPartnersTaxCodeBeenAdjusted complete"
