/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.childcarecalculatorfrontend.jobs

import javax.inject.Inject
import org.joda.time.Duration
import play.api.Logger
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.DefaultDB
import uk.gov.hmrc.childcarecalculatorfrontend.repositories.{ReactiveMongoRepository, SessionRepository}
import uk.gov.hmrc.lock.{LockKeeper, LockRepository}
import uk.gov.hmrc.play.scheduling.ExclusiveScheduledJob
import play.api.mvc.Results.Ok
import uk.gov.hmrc.play.config.ServicesConfig

import scala.concurrent.{ExecutionContext, Future}

trait ClearDataJob extends ExclusiveScheduledJob with JobConfig with ServicesConfig {

  val lock: LockKeeper
  val repo: ReactiveMongoRepository
  val batchSize: Int

  override def executeInMutex(implicit ec: ExecutionContext): Future[Result] = {
    lock.tryLock {
      Logger.info(s"Triggered $name")
      repo.clearStaleData(batchSize) map { res =>
        Logger.info(s"Deleted $res documents")
        Ok
      }
    } map {
      case Some(x) =>
        Logger.info(s"successfully acquired lock for $name")
        Result(s"$name")
      case None =>
        Logger.info(s"failed to acquire lock for $name")
        Result(s"$name failed")
    } recover {
      case _: Exception => Result(s"$name failed")
    }
  }
}

class ClearDataJobImpl @Inject()(val sessionRepo: SessionRepository) extends ClearDataJob {

  val name = "clear-data-job"
  val repo = sessionRepo()
  val batchSize: Int = getInt("batchSize")

  override lazy val lock: LockKeeper = new LockKeeper() {
    override val lockId = s"$name-lock"
    override val forceLockReleaseAfter: Duration = lockTimeout
    implicit private val mongo: () => DefaultDB = new MongoDbConnection {}.db
    override val repo = new LockRepository()
  }
}
