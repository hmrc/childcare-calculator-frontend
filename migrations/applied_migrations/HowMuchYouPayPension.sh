#!/bin/bash

echo "Applying migration HowMuchYouPayPension"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /howMuchYouPayPension               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchYouPayPensionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /howMuchYouPayPension               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchYouPayPensionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHowMuchYouPayPension               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchYouPayPensionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHowMuchYouPayPension               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchYouPayPensionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "howMuchYouPayPension.title = howMuchYouPayPension" >> ../conf/messages.en
echo "howMuchYouPayPension.heading = howMuchYouPayPension" >> ../conf/messages.en
echo "howMuchYouPayPension.checkYourAnswersLabel = howMuchYouPayPension" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def howMuchYouPayPension: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](HowMuchYouPayPensionId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def howMuchYouPayPension: Option[AnswerRow] = userAnswers.howMuchYouPayPension map {";\
     print "    x => AnswerRow(\"howMuchYouPayPension.checkYourAnswersLabel\", s\"$x\", false, routes.HowMuchYouPayPensionController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HowMuchYouPayPension complete"
