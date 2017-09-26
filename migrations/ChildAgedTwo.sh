#!/bin/bash

if grep -Fxp ChildAgedTwo applied
then
    echo "Migration ChildAgedTwo has already been applied, exiting"
    exit 1
fi

echo "Applying migration ChildAgedTwo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.Routes
echo "GET        /childAgedTwo                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildAgedTwoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.Routes
echo "POST       /childAgedTwo                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildAgedTwoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.Routes

echo "GET        /changeChildAgedTwo                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildAgedTwoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.Routes
echo "POST       /changeChildAgedTwo                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildAgedTwoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.Routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "childAgedTwo.title = childAgedTwo" >> ../conf/messages.en
echo "childAgedTwo.heading = childAgedTwo" >> ../conf/messages.en
echo "childAgedTwo.checkYourAnswersLabel = childAgedTwo" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def childAgedTwo: Option[Boolean] = cacheMap.getEntry[Boolean](ChildAgedTwoId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def childAgedTwo: Option[AnswerRow] = userAnswers.childAgedTwo map {";\
     print "    x => AnswerRow(\"childAgedTwo.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ChildAgedTwoController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Registering this migration as applied"
echo "ChildAgedTwo" >> applied
