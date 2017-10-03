#!/bin/bash

echo "Applying migration DoesYourEmployerOfferChildcareVouchers"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doesYourEmployerOfferChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoesYourEmployerOfferChildcareVouchersController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /doesYourEmployerOfferChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoesYourEmployerOfferChildcareVouchersController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDoesYourEmployerOfferChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoesYourEmployerOfferChildcareVouchersController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDoesYourEmployerOfferChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoesYourEmployerOfferChildcareVouchersController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doesYourEmployerOfferChildcareVouchers.title = doesYourEmployerOfferChildcareVouchers" >> ../conf/messages.en
echo "doesYourEmployerOfferChildcareVouchers.heading = doesYourEmployerOfferChildcareVouchers" >> ../conf/messages.en
echo "doesYourEmployerOfferChildcareVouchers.option1 = doesYourEmployerOfferChildcareVouchers" Option 1 >> ../conf/messages.en
echo "doesYourEmployerOfferChildcareVouchers.option2 = doesYourEmployerOfferChildcareVouchers" Option 2 >> ../conf/messages.en
echo "doesYourEmployerOfferChildcareVouchers.checkYourAnswersLabel = doesYourEmployerOfferChildcareVouchers" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def doesYourEmployerOfferChildcareVouchers: Option[String] = cacheMap.getEntry[String](DoesYourEmployerOfferChildcareVouchersId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def doesYourEmployerOfferChildcareVouchers: Option[AnswerRow] = userAnswers.doesYourEmployerOfferChildcareVouchers map {";\
     print "    x => AnswerRow(\"doesYourEmployerOfferChildcareVouchers.checkYourAnswersLabel\", s\"doesYourEmployerOfferChildcareVouchers.$x\", true, routes.DoesYourEmployerOfferChildcareVouchersController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoesYourEmployerOfferChildcareVouchers complete"
