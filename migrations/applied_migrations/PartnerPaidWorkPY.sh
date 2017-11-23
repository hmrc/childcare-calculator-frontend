#!/bin/bash

echo "Applying migration PartnerPaidWorkPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerPaidWorkPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidWorkPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerPaidWorkPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidWorkPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerPaidWorkPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidWorkPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerPaidWorkPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerPaidWorkPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerPaidWorkPY.title = partnerPaidWorkPY" >> ../conf/messages.en
echo "partnerPaidWorkPY.heading = partnerPaidWorkPY" >> ../conf/messages.en
echo "partnerPaidWorkPY.checkYourAnswersLabel = partnerPaidWorkPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerPaidWorkPY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidWorkPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerPaidWorkPY: Option[AnswerRow] = userAnswers.partnerPaidWorkPY map {";\
     print "    x => AnswerRow(\"partnerPaidWorkPY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerPaidWorkPYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerPaidWorkPY complete"
