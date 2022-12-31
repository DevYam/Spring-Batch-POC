package com.devyam.springbatch.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/load")
public class LoadController {

    // Job launcher is created by the framework
    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @GetMapping
    public BatchStatus load() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters parameter = new JobParameters(maps);
        JobExecution jobExecution = jobLauncher.run(job, parameter);

        System.out.println("Job Execution: "+jobExecution.getStatus());

        System.out.println("Batch is running");
        while (jobExecution.isRunning()){
            System.out.println("...");
        }

        return jobExecution.getStatus();
    }

}
