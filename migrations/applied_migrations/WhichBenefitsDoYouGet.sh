#!/bin/bash

echo "Applying migration WhichBenefitsDoYouGet"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichBenefitsDoYouGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsDoYouGetController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichBenefitsDoYouGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsDoYouGetController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichBenefitsDoYouGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsDoYouGetController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichBenefitsDoYouGet               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhichBenefitsDoYouGetController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichBenefitsDoYouGet.title = whichBenefitsDoYouGet" >> ../conf/messages.en
echo "whichBenefitsDoYouGet.heading = whichBenefitsDoYouGet" >> ../conf/messages.en
echo "whichBenefitsDoYouGet.option1 = whichBenefitsDoYouGet" Option 1 >> ../conf/messages.en
echo "whichBenefitsDoYouGet.option2 = whichBenefitsDoYouGet" Option 2 >> ../conf/messages.en
echo "whichBenefitsDoYouGet.checkYourAnswersLabel = whichBenefitsDoYouGet" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whichBenefitsDoYouGet: Option[String] = cacheMap.getEntry[String](WhichBenefitsDoYouGetId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whichBenefitsDoYouGet: Option[AnswerRow] = userAnswers.whichBenefitsDoYouGet map {";\
     print "    x => AnswerRow(\"whichBenefitsDoYouGet.checkYourAnswersLabel\", s\"whichBenefitsDoYouGet.$x\", true, routes.WhichBenefitsDoYouGetController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhichBenefitsDoYouGet complete"
