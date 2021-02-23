package me.minhael.design.job

/**
 * API to schedule a future job for execution.
 *
 * Duplicated jobs of the same name are replaced.
 */
interface JobManager {

    fun set(name: String, trigger: JobTrigger, job: Job): String
    fun remove(name: String)

    interface Scheduler {
        fun setup(trigger: JobTrigger, name: String, job: Job): String
        fun setup(trigger: JobTrigger.OneShot, name: String, job: Job): String
        fun setup(trigger: JobTrigger.Periodic, name: String, job: Job): String
        fun setup(trigger: JobTrigger.Cron, name: String, job: Job): String
        fun setup(trigger: JobTrigger.Boot, name: String, job: Job): String
    }
}