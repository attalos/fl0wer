package org.attalos.fl0wer.utils;

import java.util.HashMap;
import java.util.Map;

public class ConstantValues {
    private static boolean initialised = false;
    private static boolean time_information = true;
    private static boolean dots = false;
    private static boolean subsumption = false;
    private static Integer debug_level = 0;
    private static Map<String, Timer> timer_map = new HashMap<String, Timer>();

    /**
     *
     * @param debug_level select how detailed the debug info should be
     * @param time_information true if time information should get display
     * @param dots true if at dots graph of rete network should get created (only do so if the ontology is very small)
     * @param subsumption true if subsumption relation should get decided, false if subsumerset should get created
     */
    public static void initialise(Integer debug_level, boolean time_information, boolean dots, boolean subsumption) {
        if (initialised) {
            throw new RuntimeException("ConstantValues could only be initialized once");
        }

        ConstantValues.time_information = time_information;
        ConstantValues.dots = dots;
        ConstantValues.debug_level = debug_level;
        ConstantValues.subsumption = subsumption;

        initialised = true;
    }

    public static void purge() {
        initialised = false;
        timer_map = new HashMap<>();
    }

//    public static boolean debug(Integer debug_level) {
//        if (!initialised) {
//            throw new RuntimeException("ConstantValues accessed before initialisation");
//        }
//
//        return debug_level <= ConstantValues.debug_level ;
//    }

    public static boolean time_information() {
        if (!initialised) {
            throw new RuntimeException("ConstantValues accessed before initialisation");
        }

        return time_information;
    }

    public static void start_timer(String timer_name) {
        if (!time_information) {
            return;
        }

        Timer timer = timer_map.get(timer_name);

        if (timer == null) {
            timer = new Timer();
            timer_map.put(timer_name, timer);
        }

        timer.start();
    }

    public static void stop_timer(String timer_name) {
        if (!time_information) {
            return;
        }

        Timer timer = timer_map.get(timer_name);

        if (timer == null) {
            timer = new Timer();
            timer_map.put(timer_name, timer);
        }

        timer.stop();
    }

    public static void print_times() {
        if (!time_information) {
            return;
        }

        System.out.println("timer results - start");

        for (Map.Entry<String, Timer> mapentry : timer_map.entrySet()) {
            System.out.println(mapentry.getKey() + "\t\t" + mapentry.getValue().get_total_time());
        }

        System.out.println("timer results - stop");
    }

    public static boolean dots() {
        if (!initialised) {
            throw new RuntimeException("ConstantValues accessed before initialisation");
        }

        return dots;
    }

    public static boolean is_subsumption() {
        if (!initialised) {
            throw new RuntimeException("ConstantValues accessed before initialisation");
        }

        return subsumption;
    }

//    public static Integer debug_level() {
//        if (!initialised) {
//            throw new RuntimeException("ConstantValues accessed before initialisation");
//        }
//
//        return debug_level;
//    }
//
//    public static void debug_info(String debug_info, Integer debug_level){
//        if (!initialised) {
//            throw new RuntimeException("ConstantValues accessed before initialisation");
//        }
//
//        if(debug_level <= ConstantValues.debug_level) {
//            System.out.println(debug_info);
//        }
//    }
}
