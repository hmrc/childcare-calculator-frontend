#!/bin/bash

echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /$pluralName;format="decap"$/overview                     uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.$className$OverviewController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "" >> ../conf/app.routes
echo "GET        /$pluralName;format="decap"$/add                          uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Add$className$Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /$pluralName;format="decap"$/add                          uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Add$className$Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "" >> ../conf/app.routes
echo "GET        /any$pluralName$                                          uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Any$pluralName$Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /any$pluralName$                                          uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Any$pluralName$Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "" >> ../conf/app.routes
echo "GET        /$pluralName;format="decap"$/:index/change                uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Edit$className$Controller.onPageLoad(index: Int, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /$pluralName;format="decap"$/:index/change                uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Edit$className$Controller.onSubmit(index: Int, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "" >> ../conf/app.routes
echo "GET        /$pluralName;format="decap"$/:index/remove                uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Delete$className$Controller.onPageLoad(index: Int, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /$pluralName;format="decap"$/:index/remove                uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Delete$className$Controller.onSubmit(index: Int, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "" >> ../conf/app.routes
echo "GET        /change$pluralName$/overview               uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.$className$OverviewController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "" >> ../conf/app.routes
echo "GET        /change$pluralName$/add                    uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Add$className$Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /change$pluralName$/add                    uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Add$className$Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "" >> ../conf/app.routes
echo "GET        /changeAny$pluralName$                     uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Any$pluralName$Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAny$pluralName$                     uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Any$pluralName$Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "" >> ../conf/app.routes
echo "GET        /change$pluralName$/:index/change          uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Edit$className$Controller.onPageLoad(index: Int, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /change$pluralName$/:index/change          uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Edit$className$Controller.onSubmit(index: Int, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "" >> ../conf/app.routes
echo "GET        /change$pluralName$/:index/remove          uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Delete$className$Controller.onPageLoad(index: Int, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /change$pluralName$/:index/remove          uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.Delete$className$Controller.onSubmit(index: Int, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.field1 = Field 1" >> ../conf/messages.en
echo "$className;format="decap"$.field2 = Field 2" >> ../conf/messages.en
echo "$pluralName;format="decap"$.add.title = Add $className$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.add.heading = Add $className$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.any$pluralName$.title = Any $pluralName$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.any$pluralName$.heading = Any $pluralName$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.any$pluralName$.checkYourAnswersLabel = Any $pluralName$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.delete.title = Remove $className$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.delete.heading = Remove $className$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.edit.title = Edit $className$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.edit.heading = Edit $className$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.overview.title = $pluralName$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.overview.heading = $pluralName$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.overview.add_link = Add another $className$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.overview.guidance = Guidance for $className$" >> ../conf/messages.en
echo "$pluralName;format="decap"$.add_link = Add another $className$" >> ../conf/messages.en

echo "Adding helper lines into UserAnswers"
awk '/class/ {\
     print;\
     print "  def $pluralName;format="decap"$: Option[Seq[$className$]] = cacheMap.getEntry[Seq[$className$]]($pluralName$Id.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

awk '/class/ {\
     print;\
     print "  def any$pluralName$: Option[Boolean] = cacheMap.getEntry[Boolean](Any$pluralName$Id.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/controllers.routes/ {\
     print;\
     print "import uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="decap"$.{routes => $className;format="decap"$Routes}";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

awk '/class/ {\
     print;\
     print "";\
     print "  def $pluralName$Section: Option[RepeaterAnswerSection] = any$pluralName$ map { x =>";\
     print "    RepeaterAnswerSection(\"$pluralName;format="decap"$\", x, $pluralName;format="decap"$, \"$pluralName;format="decap"$.add_link\", $className;format="decap"$Routes.Add$className$Controller.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

awk '/class/ {\
     print;\
     print "";\
     print "  def $pluralName;format="decap"$: Seq[RepeaterAnswerRow] = userAnswers.$pluralName;format="decap"$.map {";\
     print "    $pluralName;format="decap"$ => $pluralName;format="decap"$.zipWithIndex.map {";\
     print "      case ($className;format="decap"$, index) => RepeaterAnswerRow($className;format="decap"$.field1, $className;format="decap"$Routes.Edit$className$Controller.onPageLoad(index, CheckMode).url, $className;format="decap"$Routes.Delete$className$Controller.onPageLoad(index, CheckMode).url)";\
     print "    }";\
     print "  }.getOrElse(Seq())";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

awk '/class/ {print;\
     print "";\
     print "  def any$pluralName$: Option[AnswerRow] = userAnswers.any$pluralName$ map {";\
     print "    x => AnswerRow(\"$pluralName;format="decap"$.any$pluralName$.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, $className;format="decap"$Routes.Any$pluralName$Controller.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo ""
echo "All done.  Please see migrations/$className$_migration_notes.md for info and next steps."
echo "Migration $className;format="snake"$ complete"