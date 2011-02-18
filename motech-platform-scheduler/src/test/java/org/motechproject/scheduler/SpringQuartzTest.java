package org.motechproject.scheduler;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={"/applicationContext.xml"})

public class SpringQuartzTest {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

     String groupName = "group1";


    @Test
    public void scheduleUnscheduleTest() throws Exception{

        String uuidStr = UUID.randomUUID().toString();

        JobDetail job = new JobDetail(uuidStr, groupName, MotechScheduledJob.class);
        job.getJobDataMap().put("eventType", "PillReminder");
        job.getJobDataMap().put("patientId", "001");

        Trigger trigger = new SimpleTrigger(uuidStr, groupName);

        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        scheduler.scheduleJob(job, trigger);

        scheduler = null;

        scheduler = schedulerFactoryBean.getScheduler();


        String[] jobNames = scheduler.getJobNames(groupName);
        assertEquals(1, jobNames.length);

        String[] triggerNames = scheduler.getTriggerNames(groupName);
        assertEquals(1, triggerNames.length);


        scheduler.unscheduleJob(uuidStr, groupName);
        scheduler.deleteJob(uuidStr, groupName);

        jobNames = scheduler.getJobNames(groupName);
        assertEquals(0, jobNames.length);

        triggerNames = scheduler.getTriggerNames(groupName);
        assertEquals(0, triggerNames.length);

    }
}
