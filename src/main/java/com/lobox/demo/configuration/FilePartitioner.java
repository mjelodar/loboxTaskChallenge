package com.lobox.demo.configuration;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

//@Component
public class FilePartitioner implements Partitioner {

    private final String filePath;
    private final int partitionSize;

    public FilePartitioner(String filePath, int partitionSize) {
        this.filePath = filePath;
        this.partitionSize = partitionSize;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int totalLines = (int) reader.lines().count();
            int partitionNumber = 0;

            for (int start = 0; start < totalLines; start += partitionSize) {
                ExecutionContext context = new ExecutionContext();
                context.putInt("startLine", start);
                context.putInt("endLine", Math.min(start + partitionSize - 1, totalLines - 1));
                context.putString("filePath", filePath);
                partitions.put("partition" + partitionNumber, context);
                System.out.println("=======>> startLine" + start);
                System.out.println("=======>> endLine" + Math.min(start + partitionSize - 1, totalLines - 1));
                System.out.println("=======>> partition" + partitionNumber);
                partitionNumber++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partitions;
    }
}
