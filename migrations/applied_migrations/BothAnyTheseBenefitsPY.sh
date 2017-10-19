#!/bin/bash

echo "Applying migration BothAnyTheseBenefitsPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothAnyTheseBenefitsPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothAnyTheseBenefitsPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothAnyTheseBenefitsPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothAnyTheseBenefitsPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothAnyTheseBenefitsPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothAnyTheseBenefitsPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothAnyTheseBenefitsPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothAnyTheseBenefitsPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothAnyTheseBenefitsPY.title = bothAnyTheseBenefitsPY" >> ../conf/messages.en
echo "bothAnyTheseBenefitsPY.heading = bothAnyTheseBenefitsPY" >> ../conf/messages.en
echo "bothAnyTheseBenefitsPY.checkYourAnswersLabel = bothAnyTheseBenefitsPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothAnyTheseBenefitsPY: Option[Boolean] = cacheMap.getEntry[Boolean](BothAnyTheseBenefitsPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothAnyTheseBenefitsPY: Option[AnswerRow] = userAnswers.bothAnyTheseBenefitsPY map {";\
     print "    x => AnswerRow(\"bothAnyTheseBenefitsPY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.BothAnyTheseBenefitsPYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothAnyTheseBenefitsPY complete"
