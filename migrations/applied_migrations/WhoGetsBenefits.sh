#!/bin/bash

echo "Applying migration WhoGetsBenefits"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoGetsBenefits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsBenefitsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoGetsBenefits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsBenefitsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoGetsBenefits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsBenefitsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoGetsBenefits               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhoGetsBenefitsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoGetsBenefits.title = whoGetsBenefits" >> ../conf/messages.en
echo "whoGetsBenefits.heading = whoGetsBenefits" >> ../conf/messages.en
echo "whoGetsBenefits.option1 = whoGetsBenefits" Option 1 >> ../conf/messages.en
echo "whoGetsBenefits.option2 = whoGetsBenefits" Option 2 >> ../conf/messages.en
echo "whoGetsBenefits.checkYourAnswersLabel = whoGetsBenefits" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whoGetsBenefits: Option[String] = cacheMap.getEntry[String](WhoGetsBenefitsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whoGetsBenefits: Option[AnswerRow] = userAnswers.whoGetsBenefits map {";\
     print "    x => AnswerRow(\"whoGetsBenefits.checkYourAnswersLabel\", s\"whoGetsBenefits.$x\", true, routes.WhoGetsBenefitsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhoGetsBenefits complete"
