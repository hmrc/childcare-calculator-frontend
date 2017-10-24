#!/bin/bash

echo "Applying migration YouNoWeeksStatPayCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /youNoWeeksStatPayCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouNoWeeksStatPayCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /youNoWeeksStatPayCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouNoWeeksStatPayCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYouNoWeeksStatPayCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouNoWeeksStatPayCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYouNoWeeksStatPayCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouNoWeeksStatPayCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "youNoWeeksStatPayCY.title = youNoWeeksStatPayCY" >> ../conf/messages.en
echo "youNoWeeksStatPayCY.heading = youNoWeeksStatPayCY" >> ../conf/messages.en
echo "youNoWeeksStatPayCY.checkYourAnswersLabel = youNoWeeksStatPayCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def youNoWeeksStatPayCY: Option[Int] = cacheMap.getEntry[Int](YouNoWeeksStatPayCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def youNoWeeksStatPayCY: Option[AnswerRow] = userAnswers.youNoWeeksStatPayCY map {";\
     print "    x => AnswerRow(\"youNoWeeksStatPayCY.checkYourAnswersLabel\", s\"$x\", false, routes.YouNoWeeksStatPayCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YouNoWeeksStatPayCY complete"
