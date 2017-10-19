#!/bin/bash

echo "Applying migration EmploymentIncomeCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employmentIncomeCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.EmploymentIncomeCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /employmentIncomeCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.EmploymentIncomeCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeEmploymentIncomeCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.EmploymentIncomeCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeEmploymentIncomeCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.EmploymentIncomeCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "employmentIncomeCY.title = employmentIncomeCY" >> ../conf/messages.en
echo "employmentIncomeCY.heading = employmentIncomeCY" >> ../conf/messages.en
echo "employmentIncomeCY.field1 = Field 1" >> ../conf/messages.en
echo "employmentIncomeCY.field2 = Field 2" >> ../conf/messages.en
echo "employmentIncomeCY.checkYourAnswersLabel = employmentIncomeCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def employmentIncomeCY: Option[EmploymentIncomeCY] = cacheMap.getEntry[EmploymentIncomeCY](EmploymentIncomeCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def employmentIncomeCY: Option[AnswerRow] = userAnswers.employmentIncomeCY map {";\
     print "    x => AnswerRow(\"employmentIncomeCY.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.EmploymentIncomeCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration EmploymentIncomeCY complete"
