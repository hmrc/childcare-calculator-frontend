#!/bin/bash

echo "Applying migration WhoIsInPaidEmployment"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoIsInPaidEmployment               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoIsInPaidEmploymentController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoIsInPaidEmployment               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoIsInPaidEmploymentController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoIsInPaidEmployment               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoIsInPaidEmploymentController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoIsInPaidEmployment               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoIsInPaidEmploymentController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoIsInPaidEmployment.title = whoIsInPaidEmployment" >> ../conf/messages.en
echo "whoIsInPaidEmployment.heading = whoIsInPaidEmployment" >> ../conf/messages.en
echo "whoIsInPaidEmployment.option1 = whoIsInPaidEmployment" Option 1 >> ../conf/messages.en
echo "whoIsInPaidEmployment.option2 = whoIsInPaidEmployment" Option 2 >> ../conf/messages.en
echo "whoIsInPaidEmployment.checkYourAnswersLabel = whoIsInPaidEmployment" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whoIsInPaidEmployment: Option[String] = cacheMap.getEntry[String](WhoIsInPaidEmploymentId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whoIsInPaidEmployment: Option[AnswerRow] = userAnswers.whoIsInPaidEmployment map {";\
     print "    x => AnswerRow(\"whoIsInPaidEmployment.checkYourAnswersLabel\", s\"whoIsInPaidEmployment.$x\", true, routes.WhoIsInPaidEmploymentController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhoIsInPaidEmployment complete"
