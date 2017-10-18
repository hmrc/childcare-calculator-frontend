#!/bin/bash

echo "Applying migration StatutoryPayAWeek"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /statutoryPayAWeek                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.StatutoryPayAWeekController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /statutoryPayAWeek                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.StatutoryPayAWeekController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeStatutoryPayAWeek                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.StatutoryPayAWeekController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeStatutoryPayAWeek                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.StatutoryPayAWeekController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "statutoryPayAWeek.title = statutoryPayAWeek" >> ../conf/messages.en
echo "statutoryPayAWeek.heading = statutoryPayAWeek" >> ../conf/messages.en
echo "statutoryPayAWeek.checkYourAnswersLabel = statutoryPayAWeek" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def statutoryPayAWeek: Option[Boolean] = cacheMap.getEntry[Boolean](StatutoryPayAWeekId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def statutoryPayAWeek: Option[AnswerRow] = userAnswers.statutoryPayAWeek map {";\
     print "    x => AnswerRow(\"statutoryPayAWeek.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.StatutoryPayAWeekController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration StatutoryPayAWeek complete"
