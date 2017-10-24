#!/bin/bash

echo "Applying migration BothNoWeeksStatPayPy"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothNoWeeksStatPayPy                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothNoWeeksStatPayPyController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothNoWeeksStatPayPy                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothNoWeeksStatPayPyController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothNoWeeksStatPayPy                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothNoWeeksStatPayPyController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothNoWeeksStatPayPy                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothNoWeeksStatPayPyController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothNoWeeksStatPayPy.title = bothNoWeeksStatPayPy" >> ../conf/messages.en
echo "bothNoWeeksStatPayPy.heading = bothNoWeeksStatPayPy" >> ../conf/messages.en
echo "bothNoWeeksStatPayPy.field1 = Field 1" >> ../conf/messages.en
echo "bothNoWeeksStatPayPy.field2 = Field 2" >> ../conf/messages.en
echo "bothNoWeeksStatPayPy.checkYourAnswersLabel = bothNoWeeksStatPayPy" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothNoWeeksStatPayPy: Option[BothNoWeeksStatPayPy] = cacheMap.getEntry[BothNoWeeksStatPayPy](BothNoWeeksStatPayPyId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothNoWeeksStatPayPy: Option[AnswerRow] = userAnswers.bothNoWeeksStatPayPy map {";\
     print "    x => AnswerRow(\"bothNoWeeksStatPayPy.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.BothNoWeeksStatPayPyController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothNoWeeksStatPayPy complete"
