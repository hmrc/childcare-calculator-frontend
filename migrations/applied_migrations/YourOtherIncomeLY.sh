#!/bin/bash

echo "Applying migration YourOtherIncomeLY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourOtherIncomeLY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourOtherIncomeLYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourOtherIncomeLY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourOtherIncomeLYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourOtherIncomeLY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourOtherIncomeLYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourOtherIncomeLY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourOtherIncomeLYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourOtherIncomeLY.title = yourOtherIncomeLY" >> ../conf/messages.en
echo "yourOtherIncomeLY.heading = yourOtherIncomeLY" >> ../conf/messages.en
echo "yourOtherIncomeLY.checkYourAnswersLabel = yourOtherIncomeLY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourOtherIncomeLY: Option[Boolean] = cacheMap.getEntry[Boolean](YourOtherIncomeLYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourOtherIncomeLY: Option[AnswerRow] = userAnswers.yourOtherIncomeLY map {";\
     print "    x => AnswerRow(\"yourOtherIncomeLY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.YourOtherIncomeLYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourOtherIncomeLY complete"
