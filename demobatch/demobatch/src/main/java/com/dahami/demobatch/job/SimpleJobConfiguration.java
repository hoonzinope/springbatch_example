package com.dahami.demobatch.job;

import com.dahami.demobatch.job.steps.Proc1;
import com.dahami.demobatch.job.steps.Proc2;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJOb() {
        LocalDate localDate = LocalDate.now();
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep())
                .next(simpleStep2())
                .next(simpleStep3())
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep() {
        return stepBuilderFactory
                .get("simpleStep")
                .tasklet((stepContribution, chunkContext) -> {
                    JobParameters jobParameters = stepContribution.getStepExecution().getJobExecution().getJobParameters();
                    String dateTime = jobParameters.getString("date");
                    log.info("step1 -> "+dateTime);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep2() {
        return stepBuilderFactory
                .get("proc1")
                .tasklet((stepContribution, chunkContext) -> {
                    JobParameters jobParameters = stepContribution.getStepExecution().getJobExecution().getJobParameters();
                    String dateTime = jobParameters.getString("date");

                    log.info("proc1 -> "+dateTime);
                    Proc1 proc1 = new Proc1();
                    proc1.process();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep3() {
        return stepBuilderFactory
                .get("proc2")
                .tasklet((stepContribution, chunkContext) -> {
                    JobParameters jobParameters = stepContribution.getStepExecution().getJobExecution().getJobParameters();
                    String dateTime = jobParameters.getString("date");

                    log.info("proc2 -> "+dateTime);
                    Proc2 proc2 = new Proc2();
                    proc2.process();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
