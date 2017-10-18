#!/bin/bash

echo "Applying migration YouPaidPensionCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /YouPaidPensionCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouPaidPensionCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /YouPaidPensionCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouPaidPensionCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYouPaidPensionCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouPaidPensionCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYouPaidPensionCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouPaidPensionCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "YouPaidPensionCY.title = YouPaidPensionCY" >> ../conf/messages.en
echo "YouPaidPensionCY.heading = YouPaidPensionCY" >> ../conf/messages.en
echo "YouPaidPensionCY.checkYourAnswersLabel = YouPaidPensionCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def YouPaidPensionCY: Option[Boolean] = cacheMap.getEntry[Boolean](YouPaidPensionCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def YouPaidPensionCY: Option[AnswerRow] = userAnswers.YouPaidPensionCY map {";\
     print "    x => AnswerRow(\"YouPaidPensionCY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.YouPaidPensionCYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YouPaidPensionCY complete"
