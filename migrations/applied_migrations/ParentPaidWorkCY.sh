#!/bin/bash

echo "Applying migration ParentPaidWorkCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /parentPaidWorkCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentPaidWorkCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /parentPaidWorkCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentPaidWorkCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeParentPaidWorkCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentPaidWorkCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeParentPaidWorkCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentPaidWorkCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "parentPaidWorkCY.title = parentPaidWorkCY" >> ../conf/messages.en
echo "parentPaidWorkCY.heading = parentPaidWorkCY" >> ../conf/messages.en
echo "parentPaidWorkCY.checkYourAnswersLabel = parentPaidWorkCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def parentPaidWorkCY: Option[Boolean] = cacheMap.getEntry[Boolean](ParentPaidWorkCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def parentPaidWorkCY: Option[AnswerRow] = userAnswers.parentPaidWorkCY map {";\
     print "    x => AnswerRow(\"parentPaidWorkCY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ParentPaidWorkCYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ParentPaidWorkCY complete"
