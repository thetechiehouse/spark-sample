package com.thetechiehouse.spark.sample;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import scala.Tuple2;

public class FileUtilSample {
    public static void main(String[] args) {
        String logFile = "/<>/../sparkFaq.txt";

        SparkConf conf = new SparkConf().setAppName("The Techie house spark app").setMaster("local");

        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> inputFile = sc.textFile(logFile);
        /*
         * inputFile
         * .flatMap(line => line.split(" "))
         * .map(word => (word, 1)).reduceByKey(_ + _)
         * .saveAsTextFile("hdfs://...");
         */

        JavaRDD<String> logData = sc.textFile(logFile).cache();

        JavaRDD<String> words = inputFile.flatMap(new FlatMapFunction<String, String>() {
            public Iterable<String> call(String s) {
                return Arrays.asList(s.split(" "));
            }
        });
        JavaPairRDD<String, Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {
            public Tuple2<String, Integer> call(String s) {
                return new Tuple2<String, Integer>(s, 1);
            }
        });
        JavaPairRDD<String, Integer> counts = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
            public Integer call(Integer a, Integer b) {
                return a + b;
            }
        });

        counts.foreach(new VoidFunction<Tuple2<String, Integer>>() {
            private static final long serialVersionUID = 1L;

            public void call(Tuple2<String, Integer> t) throws Exception {
                System.out.println(t._1() + " " + t._2());
            }
        });
        System.out.println("counts: " + counts);

        List<String> numAs = logData.filter(new Function<String, Boolean>() {
            public Boolean call(String s) {
                return s.contains("cluster");
            }
        }).toArray();

        long clusterCounts = logData.filter(new Function<String, Boolean>() {
            public Boolean call(String s) {
                return s.contains("cluster");
            }
        }).count();

        System.out.println("Lines with cluster: " + numAs + "");
        System.out.println("cluster word count " + clusterCounts);

        JavaRDD<String> errors = inputFile.filter(new Function<String, Boolean>() {
            public Boolean call(String s) {
                return s.contains("ERROR");
            }
        });
        // Count all the errors
        errors.count();
        // Count errors mentioning MySQL
        errors.filter(new Function<String, Boolean>() {
            public Boolean call(String s) {
                return s.contains("MySQL");
            }
        }).count();
        // Fetch the MySQL errors as an array of strings
        errors.filter(new Function<String, Boolean>() {
            public Boolean call(String s) {
                return s.contains("MySQL");
            }
        }).collect();

    }
}