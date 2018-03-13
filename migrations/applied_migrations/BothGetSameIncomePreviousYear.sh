#!/bin/bash

echo "Applying migration BothGetSameIncomePreviousYear"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothGetSameIncomePreviousYear                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothGetSameIncomePreviousYearController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothGetSameIncomePreviousYear                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothGetSameIncomePreviousYearController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothGetSameIncomePreviousYear                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothGetSameIncomePreviousYearController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothGetSameIncomePreviousYear                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothGetSameIncomePreviousYearController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothGetSameIncomePreviousYear.title = bothGetSameIncomePreviousYear" >> ../conf/messages.en
echo "bothGetSameIncomePreviousYear.heading = bothGetSameIncomePreviousYear" >> ../conf/messages.en
echo "bothGetSameIncomePreviousYear.checkYourAnswersLabel = bothGetSameIncomePreviousYear" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothGetSameIncomePreviousYear: Option[Boolean] = cacheMap.getEntry[Boolean](BothGetSameIncomePreviousYearId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothGetSameIncomePreviousYear: Option[AnswerRow] = userAnswers.bothGetSameIncomePreviousYear map {";\
     print "    x => AnswerRow(\"bothGetSameIncomePreviousYear.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.BothGetSameIncomePreviousYearController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothGetSameIncomePreviousYear complete"
