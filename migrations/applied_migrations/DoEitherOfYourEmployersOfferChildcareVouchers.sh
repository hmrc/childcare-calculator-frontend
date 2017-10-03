#!/bin/bash

echo "Applying migration DoEitherOfYourEmployersOfferChildcareVouchers"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doEitherOfYourEmployersOfferChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoEitherOfYourEmployersOfferChildcareVouchersController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /doEitherOfYourEmployersOfferChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoEitherOfYourEmployersOfferChildcareVouchersController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDoEitherOfYourEmployersOfferChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoEitherOfYourEmployersOfferChildcareVouchersController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDoEitherOfYourEmployersOfferChildcareVouchers               uk.gov.hmrc.childcarecalculatorfrontend.controllers.DoEitherOfYourEmployersOfferChildcareVouchersController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doEitherOfYourEmployersOfferChildcareVouchers.title = doEitherOfYourEmployersOfferChildcareVouchers" >> ../conf/messages.en
echo "doEitherOfYourEmployersOfferChildcareVouchers.heading = doEitherOfYourEmployersOfferChildcareVouchers" >> ../conf/messages.en
echo "doEitherOfYourEmployersOfferChildcareVouchers.option1 = doEitherOfYourEmployersOfferChildcareVouchers" Option 1 >> ../conf/messages.en
echo "doEitherOfYourEmployersOfferChildcareVouchers.option2 = doEitherOfYourEmployersOfferChildcareVouchers" Option 2 >> ../conf/messages.en
echo "doEitherOfYourEmployersOfferChildcareVouchers.checkYourAnswersLabel = doEitherOfYourEmployersOfferChildcareVouchers" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def doEitherOfYourEmployersOfferChildcareVouchers: Option[String] = cacheMap.getEntry[String](DoEitherOfYourEmployersOfferChildcareVouchersId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def doEitherOfYourEmployersOfferChildcareVouchers: Option[AnswerRow] = userAnswers.doEitherOfYourEmployersOfferChildcareVouchers map {";\
     print "    x => AnswerRow(\"doEitherOfYourEmployersOfferChildcareVouchers.checkYourAnswersLabel\", s\"doEitherOfYourEmployersOfferChildcareVouchers.$x\", true, routes.DoEitherOfYourEmployersOfferChildcareVouchersController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoEitherOfYourEmployersOfferChildcareVouchers complete"
