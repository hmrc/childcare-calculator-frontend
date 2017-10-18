#!/bin/bash

echo "Applying migration YouPaidPension"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /youPaidPension                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouPaidPensionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /youPaidPension                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouPaidPensionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYouPaidPension                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouPaidPensionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYouPaidPension                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouPaidPensionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "youPaidPension.title = youPaidPension" >> ../conf/messages.en
echo "youPaidPension.heading = youPaidPension" >> ../conf/messages.en
echo "youPaidPension.checkYourAnswersLabel = youPaidPension" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def youPaidPension: Option[Boolean] = cacheMap.getEntry[Boolean](YouPaidPensionId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def youPaidPension: Option[AnswerRow] = userAnswers.youPaidPension map {";\
     print "    x => AnswerRow(\"youPaidPension.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.YouPaidPensionController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YouPaidPension complete"
