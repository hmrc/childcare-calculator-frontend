#!/bin/bash

echo "Applying migration BothOtherIncomeLY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothOtherIncomeLY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothOtherIncomeLYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothOtherIncomeLY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothOtherIncomeLYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothOtherIncomeLY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothOtherIncomeLYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothOtherIncomeLY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothOtherIncomeLYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothOtherIncomeLY.title = bothOtherIncomeLY" >> ../conf/messages.en
echo "bothOtherIncomeLY.heading = bothOtherIncomeLY" >> ../conf/messages.en
echo "bothOtherIncomeLY.checkYourAnswersLabel = bothOtherIncomeLY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothOtherIncomeLY: Option[Boolean] = cacheMap.getEntry[Boolean](BothOtherIncomeLYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothOtherIncomeLY: Option[AnswerRow] = userAnswers.bothOtherIncomeLY map {";\
     print "    x => AnswerRow(\"bothOtherIncomeLY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.BothOtherIncomeLYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothOtherIncomeLY complete"
