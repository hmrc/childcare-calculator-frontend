#!/bin/bash

echo "Applying migration PartnerEmploymentIncomeCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerEmploymentIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerEmploymentIncomeCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerEmploymentIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerEmploymentIncomeCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerEmploymentIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerEmploymentIncomeCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerEmploymentIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerEmploymentIncomeCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerEmploymentIncomeCY.title = partnerEmploymentIncomeCY" >> ../conf/messages.en
echo "partnerEmploymentIncomeCY.heading = partnerEmploymentIncomeCY" >> ../conf/messages.en
echo "partnerEmploymentIncomeCY.checkYourAnswersLabel = partnerEmploymentIncomeCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerEmploymentIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerEmploymentIncomeCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerEmploymentIncomeCY: Option[AnswerRow] = userAnswers.partnerEmploymentIncomeCY map {";\
     print "    x => AnswerRow(\"partnerEmploymentIncomeCY.checkYourAnswersLabel\", s\"$x\", false, routes.PartnerEmploymentIncomeCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerEmploymentIncomeCY complete"
