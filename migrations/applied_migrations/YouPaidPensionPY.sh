#!/bin/bash

echo "Applying migration YouPaidPensionPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /youPaidPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouPaidPensionPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /youPaidPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouPaidPensionPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYouPaidPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouPaidPensionPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYouPaidPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouPaidPensionPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "youPaidPensionPY.title = youPaidPensionPY" >> ../conf/messages.en
echo "youPaidPensionPY.heading = youPaidPensionPY" >> ../conf/messages.en
echo "youPaidPensionPY.checkYourAnswersLabel = youPaidPensionPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def youPaidPensionPY: Option[Boolean] = cacheMap.getEntry[Boolean](YouPaidPensionPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def youPaidPensionPY: Option[AnswerRow] = userAnswers.youPaidPensionPY map {";\
     print "    x => AnswerRow(\"youPaidPensionPY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.YouPaidPensionPYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YouPaidPensionPY complete"
