#!/bin/bash

echo "Applying migration ParentWorkHours"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /parentWorkHours               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentWorkHoursController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /parentWorkHours               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentWorkHoursController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeParentWorkHours               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentWorkHoursController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeParentWorkHours               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentWorkHoursController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "parentWorkHours.title = parentWorkHours" >> ../conf/messages.en
echo "parentWorkHours.heading = parentWorkHours" >> ../conf/messages.en
echo "parentWorkHours.checkYourAnswersLabel = parentWorkHours" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def parentWorkHours: Option[Int] = cacheMap.getEntry[Int](ParentWorkHoursId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def parentWorkHours: Option[AnswerRow] = userAnswers.parentWorkHours map {";\
     print "    x => AnswerRow(\"parentWorkHours.checkYourAnswersLabel\", s\"$x\", false, routes.ParentWorkHoursController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ParentWorkHours complete"
