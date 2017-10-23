#!/bin/bash

echo "Applying migration HowMuchYouPayPensionPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /howMuchYouPayPensionPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchYouPayPensionPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /howMuchYouPayPensionPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchYouPayPensionPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHowMuchYouPayPensionPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchYouPayPensionPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHowMuchYouPayPensionPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchYouPayPensionPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "howMuchYouPayPensionPY.title = howMuchYouPayPensionPY" >> ../conf/messages.en
echo "howMuchYouPayPensionPY.heading = howMuchYouPayPensionPY" >> ../conf/messages.en
echo "howMuchYouPayPensionPY.checkYourAnswersLabel = howMuchYouPayPensionPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def howMuchYouPayPensionPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](HowMuchYouPayPensionPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def howMuchYouPayPensionPY: Option[AnswerRow] = userAnswers.howMuchYouPayPensionPY map {";\
     print "    x => AnswerRow(\"howMuchYouPayPensionPY.checkYourAnswersLabel\", s\"$x\", false, routes.HowMuchYouPayPensionPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HowMuchYouPayPensionPY complete"
