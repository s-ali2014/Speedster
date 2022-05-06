package com.example.announcementsloading;

public class SpeedMonitor {
    private int metricSpeed;
    private int imperialSpeed;

    /**
     * Constructor: Default Constructor
     * Set speeds to Zero
     */
    public SpeedMonitor() {
        metricSpeed = 0;
        imperialSpeed = 0;

    }


    public SpeedMonitor(String data) {


        boolean isImperial = true;
        new SpeedMonitor(Integer.parseInt(data), isImperial);
    }


    public SpeedMonitor(double speed, boolean isImperial) {
        new SpeedMonitor((int)speed, isImperial);
    }


    public SpeedMonitor(int speed, boolean isImperial) {
        if(isImperial) {
            imperialSpeed = speed;
            metricSpeed = imperialToMetric(imperialSpeed);
        } else {
            metricSpeed = speed;
            imperialSpeed = metricToImperial(metricSpeed);
        }
    }




    public void setImperialSpeed(double imperialSpeed) {
        setImperialSpeed((int)imperialSpeed);
    }


    public void setImperialSpeed(int imperialSpeed) {
        this.imperialSpeed = imperialSpeed;
    }


    public void setMetricSpeed(double metricSpeed) {
        setMetricSpeed((int)metricSpeed);
    }


    public void setMetricSpeed(int metricSpeed) {
        this.metricSpeed = metricSpeed;
    }



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
     * Calls overloaded method by down-casting the double value to int
     */
    public int metricToImperial(double metricSpeed) {
        return (int)(metricSpeed * 0.621371192);
    }


    /**
     * Convert metric speed to imperial
     */
    public int metricToImperial(int metricSpeed) {
        return (int)(metricSpeed * 0.621371192);
    }

    /**
     * Convert imperial speed to metric
     */
    public int imperialToMetric (double imperialSpeed) {
        return (int)(imperialSpeed * 1.609344);
    }


    public int imperialToMetric (int imperialSpeed) {
        return (int)(imperialSpeed * 1.609344);
    }

    /**
     * Accelerates the speed by @increment kph
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
     */
    public void imperialAccelerate(double increment) {
        imperialAccelerate((int)increment);
    }



    public void imperialAccelerate(int increment) {
        imperialSpeed += increment;
        metricSpeed += imperialToMetric(increment);
    }

    /**
     * Decelerates the speed by @decrement kph
     */
    public void metricDecelerate(double decrement) {
        metricDecelerate((int)decrement);
    }


    public void metricDecelerate(int decrement) {
        metricSpeed -= (metricSpeed - decrement < 0 ? 0 : decrement);
        imperialSpeed = metricToImperial(metricSpeed);
    }

    /**
     * Decelerates the speed by @decrement mph
     */
    public void imperialDecelerate(double decrement) {
       imperialDecelerate((int)decrement);
    }


    public void imperialDecelerate(int decrement) {

        imperialSpeed -= (imperialSpeed - decrement < 0 ? 0 : decrement);
        metricSpeed = imperialToMetric(imperialSpeed);
    }
}
