#!/bin/bash

echo "Applying migration ParentEmploymentIncomePY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /parentEmploymentIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentEmploymentIncomePYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /parentEmploymentIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentEmploymentIncomePYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeParentEmploymentIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentEmploymentIncomePYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeParentEmploymentIncomePY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentEmploymentIncomePYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "parentEmploymentIncomePY.title = parentEmploymentIncomePY" >> ../conf/messages.en
echo "parentEmploymentIncomePY.heading = parentEmploymentIncomePY" >> ../conf/messages.en
echo "parentEmploymentIncomePY.checkYourAnswersLabel = parentEmploymentIncomePY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def parentEmploymentIncomePY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](ParentEmploymentIncomePYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def parentEmploymentIncomePY: Option[AnswerRow] = userAnswers.parentEmploymentIncomePY map {";\
     print "    x => AnswerRow(\"parentEmploymentIncomePY.checkYourAnswersLabel\", s\"$x\", false, routes.ParentEmploymentIncomePYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ParentEmploymentIncomePY complete"
