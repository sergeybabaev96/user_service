package school.faang.user_service.config.premium;

import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import school.faang.user_service.service.premium.PremiumRemovalJob;

import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Configuration
public class QuartzConfig {

    private final PremiumConfig premiumConfig;

    @Bean
    public JobDetail premiumRemovalJobDetail() {
        return JobBuilder.newJob(PremiumRemovalJob.class)
                .withIdentity("premiumRemovalJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger premiumRemovalTrigger(JobDetail premiumRemovalJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(premiumRemovalJobDetail)
                .withIdentity("premiumRemovalTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(premiumConfig.getCron()))
                .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobDetail premiumRemovalJobDetail, Trigger premiumRemovalTrigger) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobDetails(premiumRemovalJobDetail);
        schedulerFactoryBean.setTriggers(premiumRemovalTrigger);

        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);

        schedulerFactoryBean.setTaskExecutor(Executors.newFixedThreadPool(premiumConfig.getThreadPoolSize()));

        return schedulerFactoryBean;
    }
}