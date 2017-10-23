#!/bin/bash

echo "Applying migration ChildApprovedEducation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /childApprovedEducation                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildApprovedEducationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /childApprovedEducation                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildApprovedEducationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeChildApprovedEducation                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildApprovedEducationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeChildApprovedEducation                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildApprovedEducationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "childApprovedEducation.title = childApprovedEducation" >> ../conf/messages.en
echo "childApprovedEducation.heading = childApprovedEducation" >> ../conf/messages.en
echo "childApprovedEducation.checkYourAnswersLabel = childApprovedEducation" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def childApprovedEducation: Option[Boolean] = cacheMap.getEntry[Boolean](ChildApprovedEducationId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def childApprovedEducation: Option[AnswerRow] = userAnswers.childApprovedEducation map {";\
     print "    x => AnswerRow(\"childApprovedEducation.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ChildApprovedEducationController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ChildApprovedEducation complete"
