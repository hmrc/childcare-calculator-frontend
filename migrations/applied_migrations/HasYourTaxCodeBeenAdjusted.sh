#!/bin/bash

echo "Applying migration HasYourTaxCodeBeenAdjusted"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /hasYourTaxCodeBeenAdjusted               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HasYourTaxCodeBeenAdjustedController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /hasYourTaxCodeBeenAdjusted               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HasYourTaxCodeBeenAdjustedController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHasYourTaxCodeBeenAdjusted               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HasYourTaxCodeBeenAdjustedController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHasYourTaxCodeBeenAdjusted               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HasYourTaxCodeBeenAdjustedController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "hasYourTaxCodeBeenAdjusted.title = hasYourTaxCodeBeenAdjusted" >> ../conf/messages.en
echo "hasYourTaxCodeBeenAdjusted.heading = hasYourTaxCodeBeenAdjusted" >> ../conf/messages.en
echo "hasYourTaxCodeBeenAdjusted.option1 = hasYourTaxCodeBeenAdjusted" Option 1 >> ../conf/messages.en
echo "hasYourTaxCodeBeenAdjusted.option2 = hasYourTaxCodeBeenAdjusted" Option 2 >> ../conf/messages.en
echo "hasYourTaxCodeBeenAdjusted.checkYourAnswersLabel = hasYourTaxCodeBeenAdjusted" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def hasYourTaxCodeBeenAdjusted: Option[String] = cacheMap.getEntry[String](HasYourTaxCodeBeenAdjustedId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def hasYourTaxCodeBeenAdjusted: Option[AnswerRow] = userAnswers.hasYourTaxCodeBeenAdjusted map {";\
     print "    x => AnswerRow(\"hasYourTaxCodeBeenAdjusted.checkYourAnswersLabel\", s\"hasYourTaxCodeBeenAdjusted.$x\", true, routes.HasYourTaxCodeBeenAdjustedController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HasYourTaxCodeBeenAdjusted complete"
