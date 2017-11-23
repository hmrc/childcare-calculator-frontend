#!/bin/bash

echo "Applying migration ParentEmploymentIncomeCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /parentEmploymentIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentEmploymentIncomeCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /parentEmploymentIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentEmploymentIncomeCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeParentEmploymentIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentEmploymentIncomeCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeParentEmploymentIncomeCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.ParentEmploymentIncomeCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "parentEmploymentIncomeCY.title = parentEmploymentIncomeCY" >> ../conf/messages.en
echo "parentEmploymentIncomeCY.heading = parentEmploymentIncomeCY" >> ../conf/messages.en
echo "parentEmploymentIncomeCY.checkYourAnswersLabel = parentEmploymentIncomeCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def parentEmploymentIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](ParentEmploymentIncomeCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def parentEmploymentIncomeCY: Option[AnswerRow] = userAnswers.parentEmploymentIncomeCY map {";\
     print "    x => AnswerRow(\"parentEmploymentIncomeCY.checkYourAnswersLabel\", s\"$x\", false, routes.ParentEmploymentIncomeCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ParentEmploymentIncomeCY complete"
