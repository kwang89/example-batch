package com.example.examplebatch.sample;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import lombok.RequiredArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.batch.core.Job;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SampleContoller {

    private final Scheduler scheduler;

    private final Job sampleJob;
    @GetMapping("/test")
    public void getJob() throws SchedulerException {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "sampleJob");

        // define the job and tie it to our HelloJob class
        JobDetail job = newJob(JobLauncherDetails.class)
            .withIdentity("myJob", "group1")
            .usingJobData("jobName", "sampleJob")
            .build();

        // Trigger the job to run now, and then every 40 seconds
        Trigger trigger = newTrigger()
            .withIdentity("sampleJobTrigger", "sampleJobTriggerGroup")
            .withSchedule(cronSchedule("0/5 * * * * ?"))
            .forJob(job)
            .build();

        scheduler.scheduleJob(job, trigger);
    }
}
