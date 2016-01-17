package com.thetechiehouse.spark.sample;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

public class ErrorScanner {
    public static void main(String[] args) {
        String logFile = "/<file_root>/../error_sample.log";

        SparkConf conf = new SparkConf().setAppName("The Techie house spark app").setMaster("local");

        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> inputFile = sc.textFile(logFile);

        JavaRDD<String> errors = inputFile.filter(new Function<String, Boolean>() {
            public Boolean call(String s) {
                return s.contains("ERROR");
            }
        });
        errors.count();
        errors.filter(new Function<String, Boolean>() {
            public Boolean call(String s) {
                return s.contains("MySQL");
            }
        }).count();
        errors.filter(new Function<String, Boolean>() {
            public Boolean call(String s) {
                return s.contains("MySQL");
            }
        }).collect();

        for (String word : errors.collect()) {
            System.out.println(word);
        }
    }
}