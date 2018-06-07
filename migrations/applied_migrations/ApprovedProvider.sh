#!/bin/bash

echo "Applying migration ApprovedProvider"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /approvedProvider               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ApprovedProviderController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /approvedProvider               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ApprovedProviderController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeApprovedProvider               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ApprovedProviderController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeApprovedProvider               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ApprovedProviderController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages
echo "approvedProvider.title = approvedProvider" >> ../conf/messages
echo "approvedProvider.heading = approvedProvider" >> ../conf/messages
echo "approvedProvider.option1 = approvedProvider" Option 1 >> ../conf/messages
echo "approvedProvider.option2 = approvedProvider" Option 2 >> ../conf/messages
echo "approvedProvider.checkYourAnswersLabel = approvedProvider" >> ../conf/messages

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def approvedProvider: Option[String] = cacheMap.getEntry[String](ApprovedProviderId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def approvedProvider: Option[AnswerRow] = userAnswers.approvedProvider map {";\
     print "    x => AnswerRow(\"approvedProvider.checkYourAnswersLabel\", s\"approvedProvider.$x\", true, routes.ApprovedProviderController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ApprovedProvider complete"
