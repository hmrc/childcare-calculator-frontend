# Migration $className;format="snake"$

## TODO

Once you've applied the migration (by executing `migrate.sh` from the root of the project),
you'll probably want to do the following at a minimum.

### Set up the routes in your Navigator

The usual pattern for the repeater you've just added is to start at the Any$pluralName$ question.  Answering no
will take them on to some other page.  Answering yes will take the used to Add$className$ if they haven't yet set
up any $pluralName;format="decap"$, or to $className$Overview if they have.  The Add, Delete and Edit pages
route to the Overview page.

To implement this, add the following import to both Navigator and NavigatorSpec:

``` scala
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.{routes => $className;format="decap"$Routes}
```

Add the following function to Navigator and implement the missing bits:

``` scala
  private def any$pluralName$Route(answers: UserAnswers) = (answers.any$pluralName$, answers.$pluralName;format="decap"$) match {
    case (Some(false), _) => ??? // Put in the route to the next page
    case (Some(true), None) => $className;format="decap"$Routes.Add$className$Controller.onPageLoad(NormalMode)
    case (Some(true), Some(_)) => $className;format="decap"$Routes.$className$OverviewController.onPageLoad(NormalMode)
    case _ => ???
  }
```

Add the following lines to the `routeMap` in Navigator and implement the missing bits:

``` scala
    Any$pluralName$Id -> ((ua) => any$pluralName$Route(ua)),
    Add$className$Id -> (_ => $className;format="decap"$Routes.$className$OverviewController.onPageLoad(NormalMode)),
    Delete$className$Id -> (_ => $className;format="decap"$Routes.$className$OverviewController.onPageLoad(NormalMode)),
    Edit$className$Id -> (_ => $className;format="decap"$Routes.$className$OverviewController.onPageLoad(NormalMode))
```

Decide what behaviour you want to have in Check mode; this will vary by application and needs input from your interaction designer.
Add routes to the `editRouteMap` as needed.
 
Add some tests to NavigatorSpec.  In the `in NormalMode mode` block:

``` scala
    "go to $className$Overview from Add$className$" in {
      navigator.nextPage(Add$className$Id, NormalMode)(mock[UserAnswers]) mustBe $className;format="decap"$Routes.$className$OverviewController.onPageLoad(NormalMode)
    }

    "go to $className$Overview from Delete$className$" in {
      navigator.nextPage(Delete$className$Id, NormalMode)(mock[UserAnswers]) mustBe $className;format="decap"$Routes.$className$OverviewController.onPageLoad(NormalMode)
    }

    "go to $className$Overview from Edit$className$" in {
      navigator.nextPage(Edit$className$Id, NormalMode)(mock[UserAnswers]) mustBe $className;format="decap"$Routes.$className$OverviewController.onPageLoad(NormalMode)
    }

    "go to ??? from $className$Overview" in {
      navigator.nextPage($className$OverviewId, NormalMode)(mock[UserAnswers]) mustBe ???
    }

    "go to Add$className$ from Any$pluralName$ when the answer is true and no $pluralName;format="decap"$ exist already" in {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.any$pluralName$) thenReturn Some(true)
      when(mockAnswers.$pluralName;format="decap"$) thenReturn None
      navigator.nextPage(Any$pluralName$Id, NormalMode)(mockAnswers) mustBe $className;format="decap"$Routes.Add$className$Controller.onPageLoad(NormalMode)
    }

    "go to $className$Overview from Any$pluralName$ when the answer is true and a $className;format="decap"$ exists already" in {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.any$pluralName$) thenReturn Some(true)
      when(mockAnswers.$pluralName;format="decap"$) thenReturn Some(Seq($className$("a", "b")))
      navigator.nextPage(Any$pluralName$Id, NormalMode)(mockAnswers) mustBe $className;format="decap"$Routes.$className$OverviewController.onPageLoad(NormalMode)
    }

    "go to ??? from Any$pluralName$ when the answer is false" in {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.any$pluralName$) thenReturn Some(false)
      navigator.nextPage(Any$pluralName$Id, NormalMode)(mockAnswers) mustBe ???
    }
```

Add tests to the `in Check mode` block as needed.

### Add data clean up code to CascadeUpsert

If the user answers No to the Any$pluralName$ question, we probably want to remove any $pluralName$ they might have added in the past.
To do this, create a function in `utils/CascadeUpsert` along these lines:

``` scala
  private def cleanup$pluralName$[A](value: A, cacheMap: CacheMap)(implicit fmt: Format[A]): CacheMap =
    clearIfFalse(Any$pluralName$Id.toString, value, Set($pluralName$Id.toString), cacheMap)
```

And add an entry to the `funcMap` in the same file:

``` scala
    Any$pluralName$Id.toString -> ((v, cm) => cleanup$pluralName$(v, cm))
```

And of course, add some appropriate tests ot `CascadeUpsertSpec`, e.g.:

``` scala
  "calling apply for Any$pluralName$" when {
    "the new value is false" must {
      "save the value to the cache map and remove any $pluralName$" in {
        val originalCacheMap = new CacheMap("id", Map(
          Any$pluralName$Id.toString -> JsBoolean(true),
          $pluralName$Id.toString -> Json.toJson(Seq($className$("a", "b")))
        ))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(Any$pluralName$Id.toString, false, originalCacheMap)
        result.data mustBe Map(Any$pluralName$Id.toString -> JsBoolean(false))
      }
    }

    "the new value is true" must {
      "save the value and not remove any existing $pluralName$" in {
        val originalCacheMap = new CacheMap("id", Map(
          Any$pluralName$Id.toString -> JsBoolean(false),
          $pluralName$Id.toString -> Json.toJson(Seq($className$("a", "b")))
        ))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(Any$pluralName$Id.toString, true, originalCacheMap)
        result.data mustBe Map(
          Any$pluralName$Id.toString -> JsBoolean(true),
          $pluralName$Id.toString -> Json.toJson(Seq($className$("a", "b")))
        )
      }
    }
  }
```

### Change your model

The migration has created a dummy model which you'll need to change as appropriate.  You'll probably need to touch:

*  `app/.../models/$className$`
*  `app/.../forms/$className$Form`
*  `app/.../views/$className;format="decap"$`
*  `test/.../forms/$className$FormSpec`
*  `test/.../forms/$className$ViewSpec`
*  `conf/messages`

### Tidy up

Dummy messages have been added to the bottom of the `conf/messages` file, which you'll need to change as needed.
You may want to add more guidance, hints etc. to some pages by editing the relevant view(s) and adding more messages.

Routes have been added to `conf/app.routes` but you will probably want to change the URLs.
