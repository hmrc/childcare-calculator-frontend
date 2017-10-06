#!/bin/bash

echo "Applying migration WhatIsYourPartnersTaxCode"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatIsYourPartnersTaxCode               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhatIsYourPartnersTaxCodeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatIsYourPartnersTaxCode               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhatIsYourPartnersTaxCodeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatIsYourPartnersTaxCode               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhatIsYourPartnersTaxCodeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatIsYourPartnersTaxCode               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhatIsYourPartnersTaxCodeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatIsYourPartnersTaxCode.title = whatIsYourPartnersTaxCode" >> ../conf/messages.en
echo "whatIsYourPartnersTaxCode.heading = whatIsYourPartnersTaxCode" >> ../conf/messages.en
echo "whatIsYourPartnersTaxCode.checkYourAnswersLabel = whatIsYourPartnersTaxCode" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whatIsYourPartnersTaxCode: Option[Int] = cacheMap.getEntry[Int](WhatIsYourPartnersTaxCodeId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whatIsYourPartnersTaxCode: Option[AnswerRow] = userAnswers.whatIsYourPartnersTaxCode map {";\
     print "    x => AnswerRow(\"whatIsYourPartnersTaxCode.checkYourAnswersLabel\", s\"$x\", false, routes.WhatIsYourPartnersTaxCodeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhatIsYourPartnersTaxCode complete"
