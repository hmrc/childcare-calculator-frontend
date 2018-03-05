#!/bin/bash

echo "Applying migration YouGetSameIncomePreviousYear"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /youGetSameIncomePreviousYear                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouGetSameIncomePreviousYearController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /youGetSameIncomePreviousYear                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouGetSameIncomePreviousYearController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYouGetSameIncomePreviousYear                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouGetSameIncomePreviousYearController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYouGetSameIncomePreviousYear                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YouGetSameIncomePreviousYearController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "youGetSameIncomePreviousYear.title = youGetSameIncomePreviousYear" >> ../conf/messages.en
echo "youGetSameIncomePreviousYear.heading = youGetSameIncomePreviousYear" >> ../conf/messages.en
echo "youGetSameIncomePreviousYear.checkYourAnswersLabel = youGetSameIncomePreviousYear" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def youGetSameIncomePreviousYear: Option[Boolean] = cacheMap.getEntry[Boolean](YouGetSameIncomePreviousYearId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def youGetSameIncomePreviousYear: Option[AnswerRow] = userAnswers.youGetSameIncomePreviousYear map {";\
     print "    x => AnswerRow(\"youGetSameIncomePreviousYear.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.YouGetSameIncomePreviousYearController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YouGetSameIncomePreviousYear complete"
