#!/bin/bash

echo "Applying migration YouStatPay"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /youStatPay                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouStatPayController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /youStatPay                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouStatPayController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYouStatPay                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouStatPayController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYouStatPay                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouStatPayController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "youStatPay.title = youStatPay" >> ../conf/messages.en
echo "youStatPay.heading = youStatPay" >> ../conf/messages.en
echo "youStatPay.checkYourAnswersLabel = youStatPay" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def youStatPay: Option[Boolean] = cacheMap.getEntry[Boolean](YouStatPayId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def youStatPay: Option[AnswerRow] = userAnswers.youStatPay map {";\
     print "    x => AnswerRow(\"youStatPay.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.YouStatPayController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YouStatPay complete"
