# childcare-calculator-frontend

  ![Download](https://api.bintray.com/packages/hmrc/releases/childcare-calculator-frontend/images/download.svg) 
 (https://bintray.com/hmrc/releases/childcare-calculator-frontend/_latestVersion)

The Childcare Calculator will help parents quickly self-assess the options for their childcare support, allowing them to
make a decision on which scheme will best suit their needs. The Childcare Calculator will calculate the data input by
the users, inform them of their eligibility and how much support that they could receive for the Tax-Free Childcare (TFC),
Tax Credits (TC) and Employer-Supported Childcare (ESC) schemes.

The Childcare Calculator invokes cc-eligibility microservice([Eligibility documentation](https://github.com/hmrc/cc-eligibility/blob/master/README.md)).

The Childcare Calculator Frontend service, collects data input by the users from the fields on the presented pages.
This data is collated and passed to the Childcare Calculator backend processes. The results are returned to the Childcare
Calculator Frontend service to display to the user.

* **Endpoint URL :** /childcare-calc

* **Port Number :** 9381

## Getting started with the service
## Adding a page

The steps to add a new page are as follows:

1. In sbt run `g8Scaffold yesNoPage` or whichever scaffold you want to apply - the options are:
    1. `intPage` - a page with a single text field for a whole-number value
    2. `optionsPage` - a page with a set of radio buttons
    3. `questionPage` - a page with multiple questions on it backed by a case class
    4. `repeater` - a set of screens to allow multiple items to be added
    5. `yesNoPage` - a page with a pair of yes/no radio options
    6. `bigDecimalPage`- a page with a single text field for a big decimal 
    7. `checkboxPage` - a page with a set of check boxes
    8. `contentPage` - a simple content page without a form
2. It will ask for a name which should be CamelCased, e.g. `ChildAgedTwo`.  This will be used throughout, so for example you'll get a `ChildAgedTwoController`, `ChildAgedTwoForm` etc. as needed
3. Exit sbt and run the script `./migrate.sh`
4. Run `sbt test`
5. `git add .` and `git commit` your work at this point

You'll then need to touch these areas yourself as needed (and the order is less important):

* Change `NavigatorSpec` and `Navigator` to include your page in the routing
* If necessary, change `CascadeUpsertSpec` and `CascadeUpsert` to add any data-cleanup logic you may need
* Add in the messages, and add any guidance etc. you need to the screen.  You can easily test this in the relevant `ViewSpec`
* Change the URLs in `app.routes` as necessary
* If you're using a Check Your Answers page, add the new screen to that and add appropriate tests
* If you added an `optionsPage` you can change the available options in `forms\<YourClass>Form`
* If you added a `questionPage` you'll need to change `models\<YourClass>`, `forms\<YourClass>Form` and `views\<yourClass>` as needed, along with the relevant tests and messages
* If you added an `intPage` you may want to change the validation in `forms\<YourClass>Form` - by default it expects non-negative numbers (0 or greater) which you may want to change, or you may need to add a maximum value etc.

## Testing

#### Unit Tests
To run the unit tests for the application run the following:

1. `cd $workspace`
2. `sbt test`

To run a single unit test/spec

1. `cd $workspace`
2. `sbt`
3. `test-only *SpecToUse*` - Example being the class name of your UnitSpec

#### Test Coverage
To run the test coverage suite `scoverage`

1. `sbt clean scoverage:test`

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
