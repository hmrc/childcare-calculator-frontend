package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import javax.inject.Inject

import uk.gov.hmrc.childcarecalculatorfrontend.models.{WhichBenefitsEnum, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

sealed trait Household
case class SingleHousehold(parent: Parent) extends Household
case class JointHousehold(parent: Parent, partner: Parent) extends Household

class HouseholdFactory @Inject() () {

  def apply(answers: UserAnswers): Option[Household] = {
    answers.doYouLiveWithPartner.flatMap {
      case true =>

        for {

          areYouInPaidWork  <- answers.areYouInPaidWork
          anyBenefits <- answers.doYouOrYourPartnerGetAnyBenefits

          parentHours <- if (areYouInPaidWork) {
            answers.whoIsInPaidEmployment.flatMap {
              case str if str != YouPartnerBothEnum.PARTNER.toString =>
                answers.parentWorkHours
              case _ =>
                Some(BigDecimal(0))
            }
          } else {
            Some(BigDecimal(0))
          }

          partnerHours <- if (areYouInPaidWork) {
            answers.whoIsInPaidEmployment.flatMap {
              case str if str != YouPartnerBothEnum.YOU.toString =>
                answers.partnerWorkHours
              case _ =>
                Some(BigDecimal(0))
            }
          } else {
            Some(BigDecimal(0))
          }

          parentBenefits <- if (anyBenefits) {
            answers.whoGetsBenefits.flatMap {
              case str if str != YouPartnerBothEnum.PARTNER.toString =>
                answers.whichBenefitsYouGet
              case _ =>
                Some(Set.empty)
            }
          } else {
            Some(Set.empty)
          }

          partnerBenefits <- if (anyBenefits) {
            answers.whoGetsBenefits.flatMap {
              case str if str != YouPartnerBothEnum.YOU.toString =>
                answers.whichBenefitsPartnerGet
              case _ =>
                Some(Set.empty)
            }
          } else {
            Some(Set.empty)
          }
        } yield JointHousehold(
          Parent(parentHours, parentBenefits.map(WhichBenefitsEnum.withName)),
          Parent(partnerHours, partnerBenefits.map(WhichBenefitsEnum.withName))
        )
      case false =>
        for {

          areYouInPaidWork <- answers.areYouInPaidWork
          hours            <- if (areYouInPaidWork) {
            answers.parentWorkHours
          } else {
            Some(BigDecimal(0))
          }

          doYouGetAnyBenefits <- answers.doYouGetAnyBenefits
          benefits            <- if (doYouGetAnyBenefits) {
            answers.whichBenefitsYouGet
          } else {
            Some(Set.empty)
          }
        } yield SingleHousehold(Parent(hours, benefits.map(WhichBenefitsEnum.withName)))
    }
  }
}
