#!/bin/bash

echo "Applying migration ChildStartEducation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /childStartEducation               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildStartEducationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /childStartEducation               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildStartEducationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeChildStartEducation               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildStartEducationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeChildStartEducation               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildStartEducationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages
echo "childStartEducation.title = childStartEducation" >> ../conf/messages
echo "childStartEducation.heading = childStartEducation" >> ../conf/messages
echo "childStartEducation.checkYourAnswersLabel = childStartEducation" >> ../conf/messages

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def childStartEducation: Option[Int] = cacheMap.getEntry[Int](ChildStartEducationId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def childStartEducation: Option[AnswerRow] = userAnswers.childStartEducation map {";\
     print "    x => AnswerRow(\"childStartEducation.checkYourAnswersLabel\", s\"$x\", false, routes.ChildStartEducationController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ChildStartEducation complete"
