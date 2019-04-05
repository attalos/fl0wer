package org.attalos.fl0wer.utils;

import java.util.HashMap;
import java.util.Map;

public class ConstantValues {
    private static boolean initialised = false;
    private static boolean timeInformation = true;
    private static boolean dots = false;
    private static boolean progress = false;
    private static boolean animateFunctionalModelTree = false;
    private static boolean showAppliedRules = false;
    private static boolean subsumption = false;
    private static Integer debugLevel = 0;
    private static Map<String, Timer> timerMap = new HashMap<String, Timer>();

    /**
     *
     * @param debugLevel select how detailed the debug info should be
     * @param timeInformation true if time information should get display
     * @param dots true if at dots graph of rete network should get created (only do so if the ontology is very small)
     * @param subsumption true if subsumption relation should get decided, false if subsumerset should get created
     */
    public static void initialise(Integer debugLevel, boolean timeInformation, boolean dots, boolean subsumption) {
        if (initialised)
            throw new RuntimeException("ConstantValues could only be initialized once");
        else
            initialised = true;

        ConstantValues.timeInformation = timeInformation;
        ConstantValues.dots = dots;
        ConstantValues.progress = false;
        ConstantValues.animateFunctionalModelTree = false;
        ConstantValues.debugLevel = debugLevel;
        ConstantValues.subsumption = subsumption;


    }

    public static void purge() {
        initialised = false;
        timerMap = new HashMap<>();
    }

//    public static boolean debug(Integer debugLevel) {
//        if (!initialised) {
//            throw new RuntimeException("ConstantValues accessed before initialisation");
//        }
//
//        return debugLevel <= ConstantValues.debugLevel ;
//    }

    /*public static boolean timeInformation() {
        if (!initialised) {
            throw new RuntimeException("ConstantValues accessed before initialisation");
        }

        return timeInformation;
    }*/

    public static void startTimer(String timerName) {
        if (!timeInformation) return;

        Timer timer = getTimer(timerName);

        timer.start();
    }

    public static void stopTimer(String timerName) {
        if (!timeInformation) return;

        Timer timer = getTimer(timerName);

        timer.stop();
    }

    private static Timer getTimer(String timerName) {
        Timer timer = timerMap.get(timerName);

        if (timer == null) {
            timer = new Timer();
            timerMap.put(timerName, timer);
        }
        return timer;
    }

    public static void printTimes() {
        if (!timeInformation) return;

        System.out.println("timer results - start");

        for (Map.Entry<String, Timer> mapentry : timerMap.entrySet()) {
            System.out.println(mapentry.getKey() + "\t\t" + mapentry.getValue().get_total_time());
        }

        System.out.println("timer results - stop");
    }

    public static boolean dots() {
        ready();
        return dots;
    }

    public static boolean progress() {
        ready();
        return progress;
    }

    public static boolean animateFunctionalModelTree() {
        ready();
        return animateFunctionalModelTree;
    }

    public static boolean showAppliedRules() {
        ready();
        return showAppliedRules;
    }

    public static boolean isSubsumption() {
        ready();
        return subsumption;
    }

    private static void ready() {
        if (!initialised) {
            throw new RuntimeException("ConstantValues accessed before initialisation");
        }
    }

//    public static Integer debugLevel() {
//        if (!initialised) {
//            throw new RuntimeException("ConstantValues accessed before initialisation");
//        }
//
//        return debugLevel;
//    }
//
//    public static void debug_info(String debug_info, Integer debugLevel){
//        if (!initialised) {
//            throw new RuntimeException("ConstantValues accessed before initialisation");
//        }
//
//        if(debugLevel <= ConstantValues.debugLevel) {
//            System.out.println(debug_info);
//        }
//    }
}
