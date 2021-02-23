package me.minhael.design.job

interface JobTrigger {

    fun visit(scheduler: JobManager.Scheduler, name: String, job: Job): String

    class OneShot(val afterMs: Long) : JobTrigger {
        override fun visit(scheduler: JobManager.Scheduler, name: String, job: Job): String {
            return scheduler.setup(this, name, job)
        }
    }

    class Periodic(val startAfterMs: Long, val periodMs: Long, val flexMs: Long = 0) : JobTrigger {
        override fun visit(scheduler: JobManager.Scheduler, name: String, job: Job): String {
            return scheduler.setup(this, name, job)
        }
    }

    class Cron(val expression: String) : JobTrigger {
        override fun visit(scheduler: JobManager.Scheduler, name: String, job: Job): String {
            return scheduler.setup(this, name, job)
        }
    }

    class Boot: JobTrigger {
        override fun visit(scheduler: JobManager.Scheduler, name: String, job: Job): String {
            return scheduler.setup(this, name, job)
        }
    }
}