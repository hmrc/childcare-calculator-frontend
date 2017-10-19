#!/bin/bash

echo "Applying migration ParentPaidWorkPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /parentPaidWorkPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentPaidWorkPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /parentPaidWorkPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentPaidWorkPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeParentPaidWorkPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentPaidWorkPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeParentPaidWorkPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentPaidWorkPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "parentPaidWorkPY.title = parentPaidWorkPY" >> ../conf/messages.en
echo "parentPaidWorkPY.heading = parentPaidWorkPY" >> ../conf/messages.en
echo "parentPaidWorkPY.checkYourAnswersLabel = parentPaidWorkPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def parentPaidWorkPY: Option[Boolean] = cacheMap.getEntry[Boolean](ParentPaidWorkPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def parentPaidWorkPY: Option[AnswerRow] = userAnswers.parentPaidWorkPY map {";\
     print "    x => AnswerRow(\"parentPaidWorkPY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ParentPaidWorkPYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ParentPaidWorkPY complete"
