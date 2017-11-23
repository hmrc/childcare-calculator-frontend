#!/bin/bash

echo "Applying migration AboutYourChild"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /aboutYourChild                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.AboutYourChildController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /aboutYourChild                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.AboutYourChildController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAboutYourChild                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.AboutYourChildController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAboutYourChild                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.AboutYourChildController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "aboutYourChild.title = aboutYourChild" >> ../conf/messages.en
echo "aboutYourChild.heading = aboutYourChild" >> ../conf/messages.en
echo "aboutYourChild.field1 = Field 1" >> ../conf/messages.en
echo "aboutYourChild.field2 = Field 2" >> ../conf/messages.en
echo "aboutYourChild.checkYourAnswersLabel = aboutYourChild" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def aboutYourChild: Option[AboutYourChild] = cacheMap.getEntry[AboutYourChild](AboutYourChildId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def aboutYourChild: Option[AnswerRow] = userAnswers.aboutYourChild map {";\
     print "    x => AnswerRow(\"aboutYourChild.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.AboutYourChildController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AboutYourChild complete"
