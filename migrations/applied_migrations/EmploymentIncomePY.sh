#!/bin/bash

echo "Applying migration EmploymentIncomePY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employmentIncomePY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.EmploymentIncomePYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /employmentIncomePY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.EmploymentIncomePYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeEmploymentIncomePY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.EmploymentIncomePYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeEmploymentIncomePY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.EmploymentIncomePYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "employmentIncomePY.title = employmentIncomePY" >> ../conf/messages.en
echo "employmentIncomePY.heading = employmentIncomePY" >> ../conf/messages.en
echo "employmentIncomePY.field1 = Field 1" >> ../conf/messages.en
echo "employmentIncomePY.field2 = Field 2" >> ../conf/messages.en
echo "employmentIncomePY.checkYourAnswersLabel = employmentIncomePY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def employmentIncomePY: Option[EmploymentIncomePY] = cacheMap.getEntry[EmploymentIncomePY](EmploymentIncomePYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def employmentIncomePY: Option[AnswerRow] = userAnswers.employmentIncomePY map {";\
     print "    x => AnswerRow(\"employmentIncomePY.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.EmploymentIncomePYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration EmploymentIncomePY complete"
