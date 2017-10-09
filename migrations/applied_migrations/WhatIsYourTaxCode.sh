#!/bin/bash

echo "Applying migration WhatIsYourTaxCode"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatIsYourTaxCode               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhatIsYourTaxCodeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatIsYourTaxCode               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhatIsYourTaxCodeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatIsYourTaxCode               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhatIsYourTaxCodeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatIsYourTaxCode               uk.gov.hmrc.childcarecalculatorfrontend.controllers.WhatIsYourTaxCodeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatIsYourTaxCode.title = whatIsYourTaxCode" >> ../conf/messages.en
echo "whatIsYourTaxCode.heading = whatIsYourTaxCode" >> ../conf/messages.en
echo "whatIsYourTaxCode.checkYourAnswersLabel = whatIsYourTaxCode" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def whatIsYourTaxCode: Option[Int] = cacheMap.getEntry[Int](WhatIsYourTaxCodeId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whatIsYourTaxCode: Option[AnswerRow] = userAnswers.whatIsYourTaxCode map {";\
     print "    x => AnswerRow(\"whatIsYourTaxCode.checkYourAnswersLabel\", s\"$x\", false, routes.WhatIsYourTaxCodeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhatIsYourTaxCode complete"
