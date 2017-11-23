#!/bin/bash

echo "Applying migration FreeHoursInfo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /freeHoursInfo                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.FreeHoursInfoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /freeHoursInfo                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.FreeHoursInfoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeFreeHoursInfo                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.FreeHoursInfoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeFreeHoursInfo                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.FreeHoursInfoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "freeHoursInfo.title = freeHoursInfo" >> ../conf/messages.en
echo "freeHoursInfo.heading = freeHoursInfo" >> ../conf/messages.en
echo "freeHoursInfo.checkYourAnswersLabel = freeHoursInfo" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def freeHoursInfo: Option[Boolean] = cacheMap.getEntry[Boolean](FreeHoursInfoId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def freeHoursInfo: Option[AnswerRow] = userAnswers.freeHoursInfo map {";\
     print "    x => AnswerRow(\"freeHoursInfo.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.FreeHoursInfoController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration FreeHoursInfo complete"
