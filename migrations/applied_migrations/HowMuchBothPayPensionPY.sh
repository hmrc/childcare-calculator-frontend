#!/bin/bash

echo "Applying migration HowMuchBothPayPensionPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /howMuchBothPayPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchBothPayPensionPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /howMuchBothPayPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchBothPayPensionPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHowMuchBothPayPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchBothPayPensionPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHowMuchBothPayPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.HowMuchBothPayPensionPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "howMuchBothPayPensionPY.title = howMuchBothPayPensionPY" >> ../conf/messages.en
echo "howMuchBothPayPensionPY.heading = howMuchBothPayPensionPY" >> ../conf/messages.en
echo "howMuchBothPayPensionPY.field1 = Field 1" >> ../conf/messages.en
echo "howMuchBothPayPensionPY.field2 = Field 2" >> ../conf/messages.en
echo "howMuchBothPayPensionPY.checkYourAnswersLabel = howMuchBothPayPensionPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def howMuchBothPayPensionPY: Option[HowMuchBothPayPensionPY] = cacheMap.getEntry[HowMuchBothPayPensionPY](HowMuchBothPayPensionPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def howMuchBothPayPensionPY: Option[AnswerRow] = userAnswers.howMuchBothPayPensionPY map {";\
     print "    x => AnswerRow(\"howMuchBothPayPensionPY.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.HowMuchBothPayPensionPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HowMuchBothPayPensionPY complete"
