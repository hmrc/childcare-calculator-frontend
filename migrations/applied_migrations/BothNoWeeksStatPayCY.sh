#!/bin/bash

echo "Applying migration BothNoWeeksStatPayCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothNoWeeksStatPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothNoWeeksStatPayCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothNoWeeksStatPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothNoWeeksStatPayCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothNoWeeksStatPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothNoWeeksStatPayCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothNoWeeksStatPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothNoWeeksStatPayCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothNoWeeksStatPayCY.title = bothNoWeeksStatPayCY" >> ../conf/messages.en
echo "bothNoWeeksStatPayCY.heading = bothNoWeeksStatPayCY" >> ../conf/messages.en
echo "bothNoWeeksStatPayCY.field1 = Field 1" >> ../conf/messages.en
echo "bothNoWeeksStatPayCY.field2 = Field 2" >> ../conf/messages.en
echo "bothNoWeeksStatPayCY.checkYourAnswersLabel = bothNoWeeksStatPayCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothNoWeeksStatPayCY: Option[BothNoWeeksStatPayCY] = cacheMap.getEntry[BothNoWeeksStatPayCY](BothNoWeeksStatPayCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothNoWeeksStatPayCY: Option[AnswerRow] = userAnswers.bothNoWeeksStatPayCY map {";\
     print "    x => AnswerRow(\"bothNoWeeksStatPayCY.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.BothNoWeeksStatPayCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothNoWeeksStatPayCY complete"
