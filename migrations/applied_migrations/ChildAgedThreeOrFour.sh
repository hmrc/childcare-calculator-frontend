#!/bin/bash

if grep -Fxp ChildAgedThreeOrFour applied
then
    echo "Migration ChildAgedThreeOrFour has already been applied, exiting"
    exit 1
fi

echo "Applying migration ChildAgedThreeOrFour"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /childAgedThreeOrFour                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildAgedThreeOrFourController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /childAgedThreeOrFour                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildAgedThreeOrFourController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeChildAgedThreeOrFour                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildAgedThreeOrFourController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeChildAgedThreeOrFour                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.ChildAgedThreeOrFourController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "childAgedThreeOrFour.title = childAgedThreeOrFour" >> ../conf/messages.en
echo "childAgedThreeOrFour.heading = childAgedThreeOrFour" >> ../conf/messages.en
echo "childAgedThreeOrFour.checkYourAnswersLabel = childAgedThreeOrFour" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def childAgedThreeOrFour: Option[Boolean] = cacheMap.getEntry[Boolean](ChildAgedThreeOrFourId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def childAgedThreeOrFour: Option[AnswerRow] = userAnswers.childAgedThreeOrFour map {";\
     print "    x => AnswerRow(\"childAgedThreeOrFour.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ChildAgedThreeOrFourController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Registering this migration as applied"
echo "ChildAgedThreeOrFour" >> applied
