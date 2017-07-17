# childcare-calculator-frontend

[![Build Status](https://travis-ci.org/hmrc/childcare-calculator-frontend.svg)](https://travis-ci.org/hmrc/childcare-calculator-frontend) [ ![Download](https://api.bintray.com/packages/hmrc/releases/childcare-calculator-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/childcare-calculator-frontend/_latestVersion)

The Childcare Calculator will help parents quickly self-assess the options for their childcare support, allowing them to
make a decision on which scheme will best suit their needs. The Childcare Calculator will calculate the data input by
the users, inform them of their eligibility and how much support that they could receive for the Tax-Free Childcare (TFC),
Tax Credits (TC) and Employer-Supported Childcare (ESC) schemes.

The Childcare Calculator invokes cc-eligibility microservice([Eligibility documentation](https://github.com/hmrc/cc-eligibility/blob/master/README.md)) and cc-calculator microservice([Calculator documentation](https://github.com/hmrc/cc-calculator/blob/master/README.md)) to get the desired results.

The Childcare Calculator has a feature where in users can do email registration to send the email the calculator invokes cc-email-capture microserive([Email capture documentation](https://github.com/hmrc/cc-email-capture/blob/master/README.md))

The Childcare Calculator Frontend service, collects the data input by the users from the fields on the presented pages.
This data is collated and passed to the Childcare Calculator backend processes. The results are returned to the Childcare
Calculator Frontend service to display to the user.

* **Endpoint URL :** /childcare-calc

* **Port Number :** 9381

### Testing ###

#### Unit Tests
To run the unit tests for the application run the following:

1. `cd $workspace`
2. `sbt test`

To run a single unit test/spec

1. `cd $workspace`
2. `sbt`
3. `test-only */path/to/unitspec/Example*` - Example being the class name of your UnitSpec

#### Test Coverage
To run the test coverage suite `scoverage`

1. `sbt clean scoverage:test`

#### Acceptance Tests

**NOTE:** Cucumber/acceptance tests are available in a separate project at:
`http://github.tools.tax.service.gov.uk/ddcn/cc-acceptance-tests`

## Messages

To provide messages files with variables that are passed in then use the following format:

```
@Messages("cc.compare.total.household.spend", totalHouseholdSpend)
cc.compare.total.household.spend = You told us your childcare costs are {0} a month
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")