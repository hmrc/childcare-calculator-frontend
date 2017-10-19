#!/bin/bash

echo "Applying migration WhoPaidIntoPensionPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoPaidIntoPensionPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoPaidIntoPensionPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoPaidIntoPensionPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoPaidIntoPensionPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoPaidIntoPensionPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoPaidIntoPensionPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoPaidIntoPensionPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoPaidIntoPensionPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoPaidIntoPensionPY.title = whoPaidIntoPensionPY" >> ../conf/messages.en
echo "whoPaidIntoPensionPY.heading = whoPaidIntoPensionPY" >> ../conf/messages.en
echo "whoPaidIntoPensionPY.option1 = whoPaidIntoPensionPY" Option 1 >> ../conf/messages.en
echo "whoPaidIntoPensionPY.option2 = whoPaidIntoPensionPY" Option 2 >> ../conf/messages.en
echo "whoPaidIntoPensionPY.checkYourAnswersLabel = whoPaidIntoPensionPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whoPaidIntoPensionPY: Option[String] = cacheMap.getEntry[String](WhoPaidIntoPensionPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whoPaidIntoPensionPY: Option[AnswerRow] = userAnswers.whoPaidIntoPensionPY map {";\
     print "    x => AnswerRow(\"whoPaidIntoPensionPY.checkYourAnswersLabel\", s\"whoPaidIntoPensionPY.$x\", true, routes.WhoPaidIntoPensionPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhoPaidIntoPensionPY complete"
