package com.devyam.springbatch.config;

import com.devyam.springbatch.model.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.sql.DataSource;
import java.net.MalformedURLException;


@Configuration
@EnableBatchProcessing
public class SpringBatchConfig extends DefaultBatchConfigurer {

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,
                   StepBuilderFactory stepBuilderFactory,
                   ItemReader<User> itemReader,
                   ItemProcessor<User, User> itemProcessor,
                   ItemWriter<User> itemWriter){

        // Creating step of our batch job
        Step step = stepBuilderFactory.get("ETL-file-load")
                .<User, User>chunk(100)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();


        // Creating our batch job
        Job job = jobBuilderFactory.get("ETL-Load")
                /*
                  incrementor is for assigning ids to the job
                  and RunIdIncrementor comes by default, and
                  it will assign ids to out job starting from 1

                  A job can have multiple steps
                 */
                .incrementer(new RunIdIncrementer())

                /*
                if your job has multiple steps the use
                .flow(step).next() in place of start()
                Here we have single step, so we are passing
                it directly to start
                 */
                .start(step)
                .build();

        return job;
    }


    @Bean
    public FlatFileItemReader<User> itemReader(
            @Value("${input}") Resource resource) throws MalformedURLException {

        Resource resource1 = new UrlResource("https://drive.google.com/uc?export=download&id=1XexTdQGUgh7_rfbWRk-PU9ygWb5AjwZZ");


        FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(resource1);
        flatFileItemReader.setName("CSV-Reader");
        // Always skipping 1st line of csv file as it will be header
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;

    }

    @Bean
    public LineMapper<User> lineMapper() {
        DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "name", "dept", "salary");

        // Setting value of each field into the pojo
        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(null);
    }
}
