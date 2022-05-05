package com.example.announcementsloading;

public class SpeedMonitor {
    private int metricSpeed;
    private int imperialSpeed;

    /**
     * Constructor: Default Constructor
     * Setting speeds to Zero
     */
    public SpeedMonitor() {
        metricSpeed = 0;
        imperialSpeed = 0;

    }

    /**
     *
     * @param data is the data collected from the obd2 device
     *             this is going to be serialzed and queried for the speed
     */
    public SpeedMonitor(String data) {

        boolean isImperial = true;
        new SpeedMonitor(Integer.parseInt(data), isImperial);
    }

    /**
     *
     * @param speed is the initial speed in double form
     * @param isImperial if it is imperial then it is true or else it is false for metric
     *
     * Calling the overloaded constructor and down-casting the speed from double to int
     */
    public SpeedMonitor(double speed, boolean isImperial) {
        new SpeedMonitor((int)speed, isImperial);
    }

    /**
     *
     * @param speed is the initial speed from obd2 device
     * @param isImperial if it is imperial then it is true or else it is false for metric
     *
     * if the imperial is true set imperial speed, then set metric speed to converted value
     * if the imperial is false set metric speed, then set imperial speed to converted value
     */
    public SpeedMonitor(int speed, boolean isImperial) {

        if(isImperial) {
            imperialSpeed = speed;
            metricSpeed = imperialToMetric(imperialSpeed);
        } else {
            metricSpeed = speed;
            imperialSpeed = metricToImperial(metricSpeed);
        }
    }



    //--------------Mutators--------//

    /**
     * Reassigning imperial speed
     * Recalculating metric speed
     * Accepting double parameter
     * Calling overloaded method with integer parameter
     * @param imperialSpeed
     */
    public void setImperialSpeed(double imperialSpeed) {
        setImperialSpeed((int)imperialSpeed);
    }

    /**
     * Reassigning imperial speed
     * Recalculating metric speed
     * Accepting integer parameter
     * @param imperialSpeed setting its value
     */
    public void setImperialSpeed(int imperialSpeed) {
        this.imperialSpeed = imperialSpeed;
    }

    /**
     * Reassigning metric speed
     * Recalculating imperial speed
     * Accepting double parameter
     * Calling overloaded method with integer parameter
     * @param metricSpeed
     */
    public void setMetricSpeed(double metricSpeed) {
        setMetricSpeed((int)metricSpeed);
    }

    /**
     * Reassigning metric speed
     * Recalculating imperial speed
     * Accepting integer parameter
     * @param metricSpeed setting its value
     */
    public void setMetricSpeed(int metricSpeed) {
        this.metricSpeed = metricSpeed;
    }


    //-----------------Accessors------------//
    /**
     * @return metricSpeed value as an int
     */
    public int getMetricSpeed() {
        return this.metricSpeed;
    }

    /**
     * @return imperialSpeed value as an int
     */
    public int getImperialSpeed() {
        return this.imperialSpeed;
    }

    /**
     * Converting metric unit to imperial unit
     * it accepts the double value of metric speed
     * @return returns the imperial speed in integer form
     * Calls overloaded method by down-casting the double value to int
     */
    public int metricToImperial(double metricSpeed) {
        return (int)(metricSpeed * 0.621371192);
    }


    /**
     * Converting metric speed to imperial
     * accepts the integer value of metric speed
     * @return imperial value is also an integer
     */
    public int metricToImperial(int metricSpeed) {
        return (int)(metricSpeed * 0.621371192);
    }

    /**
     * Converting imperial unit to metric unit
     * it accepts double value of imperial unit
     * @return returns the metric value in an integer form
     */
    public int imperialToMetric (double imperialSpeed) {
        return (int)(imperialSpeed * 1.609344);
    }

    /**
     * Converting imperial speed to matric speed
     *
     * @param imperialSpeed accepts integer value
     * @return returns metric value in integer form
     */
    public int imperialToMetric (int imperialSpeed) {
        return (int)(imperialSpeed * 1.609344);
    }

    /**
     * Accelerates the speed by @increment kph
     * Calling overloaded method to increment value from double to int
     *
     */
    public void metricAccelerate(double increment) {
        metricAccelerate((int)increment);
    }

    /**
     * Accelerates the speed by @increment kph
     */
    public void metricAccelerate(int increment) {
        metricSpeed += increment;
        imperialSpeed += metricToImperial(increment);
    }

    /**
     * Accelerates the speed by @increment mph
     * Calling overloaded method to increment value from double to int
     */
    public void imperialAccelerate(double increment) {
        imperialAccelerate((int)increment);
    }


    /**
     * Accelerates the speed by @increment mph
     * @param increment
     */
    public void imperialAccelerate(int increment) {
        imperialSpeed += increment;
        metricSpeed += imperialToMetric(increment);
    }

    /**
     * Decelerates the speed by @decrement kph
     * Calling overloaded method to decrement value from double to integer
     */
    public void metricDecelerate(double decrement) {
        metricDecelerate((int)decrement);
    }

    /**
     *
     * @param decrement
     */
    public void metricDecelerate(int decrement) {
        metricSpeed -= (metricSpeed - decrement < 0 ? 0 : decrement);
        imperialSpeed = metricToImperial(metricSpeed);
    }

    /**
     * Decelerates the speed by @decrement mph
     * Calling overloaded method to decrement value from double to integer
     */
    public void imperialDecelerate(double decrement) {
       imperialDecelerate((int)decrement);
    }

    /**
     *
     * @param decrement
     */
    public void imperialDecelerate(int decrement) {

        imperialSpeed -= (imperialSpeed - decrement < 0 ? 0 : decrement);
        metricSpeed = imperialToMetric(imperialSpeed);
    }
}
