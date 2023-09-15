package com.example.examplebatch.sample;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
@RequiredArgsConstructor
public class JobLauncherDetails extends QuartzJobBean {
    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;
    public static final String JOB_NAME = "jobName";

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Map<String, Object> jobDataMap = context.getMergedJobDataMap();
        String jobName = (String) jobDataMap.get(JOB_NAME);
        if (log.isInfoEnabled()) {
            log.info("Quartz trigger firing with Spring Batch jobName=" + jobName);
        }
        JobParameters jobParameters = getJobParametersFromJobMap(jobDataMap);
        try {
            jobLauncher.run(jobLocator.getJob(jobName), jobParameters);
        }
        catch (Exception e) {
            log.error("Could not execute job.", e);
            throw new RuntimeException(e);
        }
    }

    private JobParameters getJobParametersFromJobMap(Map<String, Object> jobDataMap) {

        JobParametersBuilder builder = new JobParametersBuilder();

        for (Entry<String, Object> entry : jobDataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String && !key.equals(JOB_NAME)) {
                builder.addString(key, (String) value);
            }
            else if (value instanceof Float || value instanceof Double) {
                builder.addDouble(key, ((Number) value).doubleValue());
            }
            else if (value instanceof Integer || value instanceof Long) {
                builder.addLong(key, ((Number) value).longValue());
            }
            else if (value instanceof Date) {
                builder.addDate(key, (Date) value);
            }
            else {
                log.debug("JobDataMap contains values which are not job parameters (ignoring).");
            }
        }

        return builder.toJobParameters();

    }
}
