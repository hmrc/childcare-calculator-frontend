#!/bin/bash

echo "Applying migration WhichBenefitsYouGet"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichBenefitsYouGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsYouGetController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichBenefitsYouGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsYouGetController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichBenefitsYouGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsYouGetController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichBenefitsYouGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsYouGetController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichBenefitsYouGet.title = whichBenefitsYouGet" >> ../conf/messages.en
echo "whichBenefitsYouGet.heading = whichBenefitsYouGet" >> ../conf/messages.en
echo "whichBenefitsYouGet.option1 = whichBenefitsYouGet" Option 1 >> ../conf/messages.en
echo "whichBenefitsYouGet.option2 = whichBenefitsYouGet" Option 2 >> ../conf/messages.en
echo "whichBenefitsYouGet.checkYourAnswersLabel = whichBenefitsYouGet" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whichBenefitsYouGet: Option[Set[String]] = cacheMap.getEntry[Set[String]](WhichBenefitsYouGetId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whichBenefitsYouGet: Option[AnswerRow] = userAnswers.whichBenefitsYouGet map {";\
     print "    x => AnswerRow(\"whichBenefitsYouGet.checkYourAnswersLabel\", s\"whichBenefitsYouGet.$x\", true, routes.WhichBenefitsYouGetController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhichBenefitsYouGet complete"
