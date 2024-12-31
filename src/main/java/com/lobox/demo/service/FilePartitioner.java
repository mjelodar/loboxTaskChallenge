package com.lobox.demo.service;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FilePartitioner implements Partitioner {

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources("*.tsv");

            for (int i = 0; i < resources.length; i++) {
                ExecutionContext context = new ExecutionContext();
                context.putString("fileName", resources[i].getFile().getAbsolutePath());
                partitions.put("partition" + i, context);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error partitioning files", e);
        }

        return partitions;
    }
}
