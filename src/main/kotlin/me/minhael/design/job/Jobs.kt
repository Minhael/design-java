package me.minhael.design.job

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

    fun set(name: String, trigger: Trigger, builder: () -> Job)
    fun remove(name: String)

    interface Trigger {
        fun visit(scheduler: Scheduler, name: String, builder: () -> Job)
    }

    interface Scheduler {
        fun setup(trigger: Trigger, name: String, builder: () -> Job)
        fun setup(trigger: OneShot, name: String, builder: () -> Job)
        fun setup(trigger: Periodic, name: String, builder: () -> Job)
        fun setup(trigger: Boot, name: String, builder: () -> Job)
    }

    interface Job {
        /**
         * true: Success
         * false: Failure
         * null: Retry
         */
        fun execute(): Boolean?
    }

    class OneShot(val timestamp: Long) : Trigger {
        override fun visit(scheduler: Scheduler, name: String, builder: () -> Job) {
            scheduler.setup(this, name, builder)
        }
    }

    class Periodic(val timestamp: Long, val periodMs: Long, val flexMs: Long = periodMs) : Trigger {
        override fun visit(scheduler: Scheduler, name: String, builder: () -> Job) {
            scheduler.setup(this, name, builder)
        }
    }

    class Boot: Trigger {
        override fun visit(scheduler: Scheduler, name: String, builder: () -> Job) {
            scheduler.setup(this, name, builder)
        }
    }
}