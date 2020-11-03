package com.example.mlib;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class mClass {
    public static String method() {
        Log.d("mlib", "hello");
        return "hello from mlib";
    }

    public static mReading makeMeasurement(int mask) {
        List<List<List<Integer>>> res = new ArrayList<>();
        int cores = Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < cores; i++) {
            if ((mask & (1 << i)) != 0) {
                String filePath = "/sys/devices/system/cpu/cpu" + i + "/cpufreq/stats/time_in_state";
                try {
                    Scanner sc = new Scanner(new File(filePath));
                    List<List<Integer>> tmp = new ArrayList<>();
                    while (sc.hasNextLine()) {
                        List<Integer> arr = new ArrayList<>();
                        String[] strs = sc.nextLine().split(" ");
                        for (String str : strs) {
                            arr.add(Integer.valueOf(str));
                        }
                        tmp.add(arr);
                    }
                    res.add(tmp);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return new mReading(res, mask);
    }

    public static mReading findDiff(mReading r1, mReading r2) {
        if (r1.mask == r2.mask) {
            List<List<List<Integer>>> diff = new ArrayList<>();
            for (int i = 0; i < r1.reading.size(); i++) {
                List<List<Integer>> proc = new ArrayList<>();
                for (int j = 0; j < r1.reading.get(i).size(); j++) {
                    List<Integer> state = new ArrayList<>();
                    for (int k = 0; j < r1.reading.get(i).get(j).size(); k++) {
                        state.add(r2.reading.get(i).get(j).get(k) - r1.reading.get(i).get(j).get(k));
                    }
                    proc.add(state);
                }
                diff.add(proc);
            }
            return new mReading(diff, r1.mask);
        }
        return null;
    }

    static class mReading {
       private List<List<List<Integer>>> reading;
       private int mask;

       protected mReading(List<List<List<Integer>>> data, int mask) {
           this.reading = data;
           this.mask = mask;
       }

        protected List<List<List<Integer>>> getReading() {
            return reading;
        }

        public String toString() {
           StringBuilder res = new StringBuilder();
           res.append(mask).append("\n");
            for (List<List<Integer>> proc : reading) {
                for (List<Integer> state : proc) {
                    for (int value: state) {
                        res.append(value);
                    }
                    res.append("\n");
                }
                res.append("\n");
            }
            return res.toString();
        }
    }
}
