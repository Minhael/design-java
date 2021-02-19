package me.minhael.design.job

import me.minhael.design.Sr
import java.io.Serializable

/**
 * API to schedule a future job for execution.
 *
 * Builder of [Job] must be a serializable function that consist primitive data only.
 * Application could make use of any DI framework to inject necessary beans or services into [Job].
 *
 * Job details are transparent to scheduler.
 * Application should listen to the result by side effect produced during execution.
 *
 * Duplicated jobs of the same name are replaced.
 */
interface Jobs {

    fun set(name: String, trigger: Trigger, job: Job)
    fun remove(name: String)

    interface Trigger {
        fun visit(scheduler: Scheduler, name: String, job: Job)
    }

    interface Scheduler {
        fun setup(trigger: Trigger, name: String, job: Job)
        fun setup(trigger: OneShot, name: String, job: Job)
        fun setup(trigger: Periodic, name: String, job: Job)
        fun setup(trigger: Boot, name: String, job: Job)
    }

    interface Job : Serializable {
        /**
         * true: Success
         * false: Failure
         * null: Retry
         */
        fun execute(sr: Sr): Boolean?
    }

    class OneShot(val timestamp: Long) : Trigger {
        override fun visit(scheduler: Scheduler, name: String, job: Job) {
            scheduler.setup(this, name, job)
        }
    }

    class Periodic(val timestamp: Long, val periodMs: Long, val flexMs: Long = 0) : Trigger {
        override fun visit(scheduler: Scheduler, name: String, job: Job) {
            scheduler.setup(this, name, job)
        }
    }

    class Boot: Trigger {
        override fun visit(scheduler: Scheduler, name: String, job: Job) {
            scheduler.setup(this, name, job)
        }
    }
}