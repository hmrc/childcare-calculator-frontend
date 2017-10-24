#!/bin/bash

echo "Applying migration YouNoWeeksStatPayPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /youNoWeeksStatPayPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouNoWeeksStatPayPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /youNoWeeksStatPayPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouNoWeeksStatPayPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYouNoWeeksStatPayPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouNoWeeksStatPayPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYouNoWeeksStatPayPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouNoWeeksStatPayPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "youNoWeeksStatPayPY.title = youNoWeeksStatPayPY" >> ../conf/messages.en
echo "youNoWeeksStatPayPY.heading = youNoWeeksStatPayPY" >> ../conf/messages.en
echo "youNoWeeksStatPayPY.checkYourAnswersLabel = youNoWeeksStatPayPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def youNoWeeksStatPayPY: Option[Int] = cacheMap.getEntry[Int](YouNoWeeksStatPayPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def youNoWeeksStatPayPY: Option[AnswerRow] = userAnswers.youNoWeeksStatPayPY map {";\
     print "    x => AnswerRow(\"youNoWeeksStatPayPY.checkYourAnswersLabel\", s\"$x\", false, routes.YouNoWeeksStatPayPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YouNoWeeksStatPayPY complete"
