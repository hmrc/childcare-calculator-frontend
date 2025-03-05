# childcare-calculator-frontend

The Childcare Calculator will help parents quickly self-assess the options for their childcare support, allowing them to
make a decision on which scheme will best suit their needs. The Childcare Calculator will calculate the data input by
the users, inform them of their eligibility and how much support that they could receive for the Tax-Free Childcare (TFC),
Employer-Supported Childcare (ESC) and Free Hours (including free hours for working parents) schemes.

ESC eligibility requires the user to already be receiving them, if they answer that they do not get vouchers
then the income questions will be skipped. The users are not informed of being ineligible for those on the summary page.

Free hours schemes is available to everyone regardless of work status or country, however England users may be able to get
free hours for working parents instead if eligible.

The Childcare Calculator invokes cc-eligibility microservice([Eligibility documentation](https://github.com/hmrc/cc-eligibility/blob/master/README.md)).

The Childcare Calculator Frontend service, collects data input by the users from the fields on the presented pages.
This data is collated and passed to the Childcare Calculator backend processes. The results are returned to the Childcare
Calculator Frontend service to display to the user.

Running the service
To run the microservices locally start the dependent microservices using service manager sm2 --start CCC_ALL

* **Endpoint URL :** /childcare-calc

* **Port Number :** 9381

## Testing

#### Unit Tests
To run the unit tests for the application run the following:

1. `sbt test`

To run a single unit test/spec

1. `sbt`
2. `testOnly *SpecToUse*` - Example being the class name of your UnitSpec

#### Test Coverage
To run the test coverage suite

1. `sbt clean coverage test coverageReport`

#### Acceptance Tests

**NOTE:** Cucumber/acceptance tests are available in a separate project at:
`https://github.com/hmrc/childcare-calculator-acceptance-tests`

#### Performance Tests

**NOTE:** Performance tests are available in a separate project at:
`https://github.com/hmrc/childcare-calculator-performance-tests`

## Messages

To provide messages files with variables that are passed in then use the following format:

```
@Messages("cc.compare.total.household.spend", totalHouseholdSpend)
cc.compare.total.household.spend = You told us your childcare costs are {0} a month
```

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
