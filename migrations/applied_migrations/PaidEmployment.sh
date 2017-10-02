#!/bin/bash

echo "Applying migration PaidEmployment"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /paidEmployment                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PaidEmploymentController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /paidEmployment                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PaidEmploymentController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePaidEmployment                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PaidEmploymentController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePaidEmployment                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PaidEmploymentController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "paidEmployment.title = paidEmployment" >> ../conf/messages.en
echo "paidEmployment.heading = paidEmployment" >> ../conf/messages.en
echo "paidEmployment.checkYourAnswersLabel = paidEmployment" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def paidEmployment: Option[Boolean] = cacheMap.getEntry[Boolean](PaidEmploymentId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def paidEmployment: Option[AnswerRow] = userAnswers.paidEmployment map {";\
     print "    x => AnswerRow(\"paidEmployment.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PaidEmploymentController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PaidEmployment complete"
