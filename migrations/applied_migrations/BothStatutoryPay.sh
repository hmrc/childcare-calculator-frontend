#!/bin/bash

echo "Applying migration BothStatutoryPay"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothStatutoryPay                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothStatutoryPayController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothStatutoryPay                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothStatutoryPayController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothStatutoryPay                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothStatutoryPayController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothStatutoryPay                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothStatutoryPayController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothStatutoryPay.title = bothStatutoryPay" >> ../conf/messages.en
echo "bothStatutoryPay.heading = bothStatutoryPay" >> ../conf/messages.en
echo "bothStatutoryPay.checkYourAnswersLabel = bothStatutoryPay" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothStatutoryPay: Option[Boolean] = cacheMap.getEntry[Boolean](BothStatutoryPayId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothStatutoryPay: Option[AnswerRow] = userAnswers.bothStatutoryPay map {";\
     print "    x => AnswerRow(\"bothStatutoryPay.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.BothStatutoryPayController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothStatutoryPay complete"
