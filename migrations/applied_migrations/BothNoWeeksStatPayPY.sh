#!/bin/bash

echo "Applying migration BothNoWeeksStatPayPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothNoWeeksStatPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothNoWeeksStatPayPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothNoWeeksStatPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothNoWeeksStatPayPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothNoWeeksStatPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothNoWeeksStatPayPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothNoWeeksStatPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothNoWeeksStatPayPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothNoWeeksStatPayPY.title = bothNoWeeksStatPayPY" >> ../conf/messages.en
echo "bothNoWeeksStatPayPY.heading = bothNoWeeksStatPayPY" >> ../conf/messages.en
echo "bothNoWeeksStatPayPY.field1 = Field 1" >> ../conf/messages.en
echo "bothNoWeeksStatPayPY.field2 = Field 2" >> ../conf/messages.en
echo "bothNoWeeksStatPayPY.checkYourAnswersLabel = bothNoWeeksStatPayPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothNoWeeksStatPayPY: Option[BothNoWeeksStatPayPY] = cacheMap.getEntry[BothNoWeeksStatPayPY](BothNoWeeksStatPayPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothNoWeeksStatPayPY: Option[AnswerRow] = userAnswers.bothNoWeeksStatPayPY map {";\
     print "    x => AnswerRow(\"bothNoWeeksStatPayPY.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.BothNoWeeksStatPayPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothNoWeeksStatPayPY complete"
