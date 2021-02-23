package me.minhael.design.job

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class TriggerTest {

    @Mock
    lateinit var scheduler: JobManager.Scheduler

    @Mock
    lateinit var job: Job

    private val name = "job"
    private val uuid = UUID.randomUUID().toString()

    @Test
    fun testOneShot() {
        `when`(scheduler.setup(any<JobTrigger.OneShot>(), any(), any())).thenReturn(uuid)

        val input = System.currentTimeMillis()
        val trigger = JobTrigger.OneShot(input)
        assertEquals(uuid, trigger.visit(scheduler, name, job))
        verify(scheduler).setup(trigger, name, job)
    }

    @Test
    fun testPeriodic() {
        `when`(scheduler.setup(any<JobTrigger.Periodic>(), any(), any())).thenReturn(uuid)

        val input = System.currentTimeMillis()
        val period = 30000L
        val flex = 300L
        val trigger = JobTrigger.Periodic(input, period, flex)
        assertEquals(uuid, trigger.visit(scheduler, name, job))
        verify(scheduler).setup(trigger, name, job)
    }

    @Test
    fun testBoot() {
        `when`(scheduler.setup(any<JobTrigger.Boot>(), any(), any())).thenReturn(uuid)

        val trigger = JobTrigger.Boot()
        assertEquals(uuid, trigger.visit(scheduler, name, job))
        verify(scheduler).setup(trigger, name, job)
    }

    @Test
    fun testCron() {
        `when`(scheduler.setup(any<JobTrigger.Cron>(), any(), any())).thenReturn(uuid)

        val input = "* * * * *"
        val trigger = JobTrigger.Cron(input)
        assertEquals(uuid, trigger.visit(scheduler, name, job))
        verify(scheduler).setup(trigger, name, job)
    }
}