#!/bin/bash

echo "Applying migration EitherOfYouMaximumEarnings"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /eitherOfYouMaximumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.EitherOfYouMaximumEarningsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /eitherOfYouMaximumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.EitherOfYouMaximumEarningsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeEitherOfYouMaximumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.EitherOfYouMaximumEarningsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeEitherOfYouMaximumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.EitherOfYouMaximumEarningsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "eitherOfYouMaximumEarnings.title = eitherOfYouMaximumEarnings" >> ../conf/messages.en
echo "eitherOfYouMaximumEarnings.heading = eitherOfYouMaximumEarnings" >> ../conf/messages.en
echo "eitherOfYouMaximumEarnings.checkYourAnswersLabel = eitherOfYouMaximumEarnings" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def eitherOfYouMaximumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](EitherOfYouMaximumEarningsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def eitherOfYouMaximumEarnings: Option[AnswerRow] = userAnswers.eitherOfYouMaximumEarnings map {";\
     print "    x => AnswerRow(\"eitherOfYouMaximumEarnings.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.EitherOfYouMaximumEarningsController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration EitherOfYouMaximumEarnings complete"
