#!/bin/bash

echo "Applying migration PartnerAnyOtherIncomeLY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /partnerAnyOtherIncomeLY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerAnyOtherIncomeLYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /partnerAnyOtherIncomeLY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerAnyOtherIncomeLYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePartnerAnyOtherIncomeLY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerAnyOtherIncomeLYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePartnerAnyOtherIncomeLY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.PartnerAnyOtherIncomeLYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnerAnyOtherIncomeLY.title = partnerAnyOtherIncomeLY" >> ../conf/messages.en
echo "partnerAnyOtherIncomeLY.heading = partnerAnyOtherIncomeLY" >> ../conf/messages.en
echo "partnerAnyOtherIncomeLY.checkYourAnswersLabel = partnerAnyOtherIncomeLY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def partnerAnyOtherIncomeLY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerAnyOtherIncomeLYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def partnerAnyOtherIncomeLY: Option[AnswerRow] = userAnswers.partnerAnyOtherIncomeLY map {";\
     print "    x => AnswerRow(\"partnerAnyOtherIncomeLY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.PartnerAnyOtherIncomeLYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PartnerAnyOtherIncomeLY complete"
