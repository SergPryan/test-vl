package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
//        String fileName = "C:/work/github/access1.log";
        if (args.length != 6){
            throw new RuntimeException("не удалось прочитать параметры");
        }

        String fileName = null;
        Float timeArgs = null;
        Float uptimeArgs = null;

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-f" -> fileName = args[i + 1];
                    case "-t" -> timeArgs = Float.parseFloat(args[i + 1]);
                    case "-u" -> uptimeArgs = Float.parseFloat(args[i + 1]);
                }
            }
        }catch (Exception e){
            throw new RuntimeException("не удалось прочитать параметры");
        }
        if(fileName == null || timeArgs == null || uptimeArgs == null){
            throw new RuntimeException("параметры не инициализированы");
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss");
        LinkedList<TimeRecord> timeRecords = new LinkedList<>();
        LinkedList<TimeRecordResult> result = new LinkedList<>();
        try (BufferedReader fileBufferReader = new BufferedReader(new FileReader(fileName))) {
            String fileLineContent;
            LocalDateTime timeStart = null;
            LocalDateTime intervalEnd = null;
            while ((fileLineContent = fileBufferReader.readLine()) != null) {

                LocalDateTime date = LocalDateTime.parse(fileLineContent.substring(20, 39), dateTimeFormatter);
                if (timeStart == null) {
                    timeStart = date;
                    intervalEnd = date.plusSeconds(30);
                }
                if (date.isAfter(intervalEnd)) {
                    timeStart = date;
                    intervalEnd = date.plusSeconds(30);

                    float countErros = timeRecords.stream().filter(TimeRecord::isError).count();
                    float availableTime = 100 - ((countErros / timeRecords.size()) * 100);
                    if (availableTime < uptimeArgs) {
                        result.add(new TimeRecordResult(timeRecords.getFirst().date(), timeRecords.getLast().date(), availableTime));
                    }
                    timeRecords.clear();
                }

                boolean isError = checkParamersInString(timeArgs, fileLineContent);
                timeRecords.add(new TimeRecord(date, isError));
            }
        }catch (Exception e){
            throw new RuntimeException("ошибка при работе приложения", e);
        }

        result.forEach(System.out::println);
    }

    public static boolean checkParamersInString(Float timeArgs, String fileLineContent) {
        int indexStart = fileLineContent.indexOf("\" ");
        int indexLast = fileLineContent.indexOf(" \"-\" \"");
        String requestInfo = fileLineContent.substring(indexStart + 2, indexLast);
        String[] arr = requestInfo.split(" ");
        int httpCode = 0;
        try {
            httpCode = Integer.parseInt(arr[0]);
        } catch (Exception ignored) {
        }
        double time = 0;
        try {
            time = Double.parseDouble(arr[2]);
        } catch (Exception ignored) {
        }
        boolean isError = false;
        if (httpCode > 500 || time > timeArgs) {
            isError = true;
        }
        return isError;
    }
}

record TimeRecord(LocalDateTime date, boolean isError) {
}

record TimeRecordResult(LocalDateTime dateStart, LocalDateTime dateEnd, float uptime) {
}
