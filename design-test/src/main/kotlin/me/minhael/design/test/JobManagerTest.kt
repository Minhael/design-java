package me.minhael.design.test

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import me.minhael.design.job.Job
import me.minhael.design.job.JobManager
import me.minhael.design.job.JobTrigger
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.mock.SerializableMode

interface JobManagerTest {

    val subject: JobManager

    fun await(name: String, millis: Long, iteration: Long = 0)
    fun hotBoot()
    fun advanceTo(name: String, timestamp: Long, isAfterTrigger: Boolean)

    @Test
    fun testOneShot() {
        val job = mock<Job>(serializable = true, serializableMode = SerializableMode.ACROSS_CLASSLOADERS)
        val task = mock<Job.Task>(serializable = true, serializableMode = SerializableMode.ACROSS_CLASSLOADERS)
        `when`(job.build()).thenReturn(task)
        `when`(task.execute()).thenReturn(true)

        val name = "oneshot"
        val delay = 15 * 60 * 1000L

        //  Remove before executed
        subject.set(name, JobTrigger.OneShot(delay), job)
        subject.remove(name)
        await(name, delay)
        verify(task, times(0)).execute()

        //  Normal
        subject.set(name, JobTrigger.OneShot(delay), job)
        await(name, delay)
        verify(task).execute()
        await(name, delay)
        verify(task).execute()
    }

    @Test
    fun testPeriodic() {
        val job = mock<Job>(serializable = true, serializableMode = SerializableMode.ACROSS_CLASSLOADERS)
        val task = mock<Job.Task>(serializable = true, serializableMode = SerializableMode.ACROSS_CLASSLOADERS)
        `when`(job.build()).thenReturn(task)
        `when`(task.execute()).thenReturn(true)

        val name = "periodic"
        val period = 15 * 60 * 1000L

        //  Remove before executed
        subject.set(name, JobTrigger.Periodic(period, period), job)
        subject.remove(name)
        await(name, period)
        verify(task, times(0)).execute()

        //  Normal
        subject.set(name, JobTrigger.Periodic(period, period), job)
        await(name, period)
        verify(task).execute()
        await(name, period, 1)
        verify(task, times(2)).execute()

        //  Remove after first execution
        reset(task)
        subject.set(name, JobTrigger.Periodic(period, period), job)
        await(name, period)
        verify(task).execute()
        subject.remove(name)
        await(name, period, 1)
        verify(task).execute()
    }

    @Test
    fun testPeriodicWithFlex() {
        val job = mock<Job>(serializable = true, serializableMode = SerializableMode.ACROSS_CLASSLOADERS)
        val task = mock<Job.Task>(serializable = true, serializableMode = SerializableMode.ACROSS_CLASSLOADERS)
        `when`(job.build()).thenReturn(task)
        `when`(task.execute()).thenReturn(true)

        val name = "periodicWithFlex"
        val period = 15 * 60 * 1000L
        val flex = 5 * 60 * 1000L

        subject.set(name, JobTrigger.Periodic(period, period, flex), job)
        verify(task, times(0)).execute()
        await(name, period + period - flex)
        verify(task).execute()
        await(name, period, 1)
        verify(task, times(2)).execute()
    }

    @Test
    fun testBoot() {
        val job = mock<Job>(serializable = true, serializableMode = SerializableMode.ACROSS_CLASSLOADERS)
        val task = mock<Job.Task>(serializable = true, serializableMode = SerializableMode.ACROSS_CLASSLOADERS)
        `when`(job.build()).thenReturn(task)
        `when`(task.execute()).thenReturn(true)

        val name = "Boot"

        subject.set(name, JobTrigger.Boot(), job)
        subject.remove(name)
        hotBoot()
        verify(task, times(0)).execute()

        subject.set(name, JobTrigger.Boot(), job)
        hotBoot()
        verify(task).execute()
        hotBoot()
        verify(task).execute()
    }

    @Test
    fun testCron() {
        val job = mock<Job>(serializable = true, serializableMode = SerializableMode.ACROSS_CLASSLOADERS)
        val task = mock<Job.Task>(serializable = true, serializableMode = SerializableMode.ACROSS_CLASSLOADERS)
        `when`(job.build()).thenReturn(task)
        `when`(task.execute()).thenReturn(true)

        val name = "cron"
        val expression = "0 0 23 * * ?"

        val now = DateTime.now()
        val pt1 = DateTime(now.year, now.monthOfYear, now.dayOfMonth, 23, 0).plusDays(1)
        val pt2 = pt1.plusDays(1)
        val before1 = pt1.minusMillis(1)
        val before2 = pt2.minusMillis(1)

        //  Remove before execute
        subject.set(name, JobTrigger.Cron(expression), job)
        subject.remove(name)
        advanceTo(name, pt1.millis, true)
        verify(task, times(0)).execute()

        //  Normal
        subject.set(name, JobTrigger.Cron(expression), job)
        advanceTo(name, before1.millis, false)
        verify(task, times(0)).execute()
        advanceTo(name, pt1.millis, true)
        verify(task).execute()
        advanceTo(name, before2.millis, false)
        verify(task).execute()
        advanceTo(name, pt2.millis, true)
        verify(task, times(2)).execute()

        //  Remove before consecutive execution
        reset(task)
        subject.set(name, JobTrigger.Cron(expression), job)
        advanceTo(name, before1.millis, false)
        verify(task, times(0)).execute()
        advanceTo(name, pt1.millis, true)
        verify(task).execute()
        subject.remove(name)
        advanceTo(name, before2.millis, false)
        verify(task).execute()
        advanceTo(name, pt2.millis, true)
        verify(task).execute()
    }
}