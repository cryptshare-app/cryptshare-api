package com.uni.share.common.control;

import java.time.Instant;
import java.util.Date;

/**
 * Provider to get current time.
 *
 * @author Felix Rottler , 26.11.2018
 **/
public class TimeProvider {

    /**
     * Get the current Date
     *
     * @return the current Date.
     */
    public Date getCurrentDate() {
        return Date.from(Instant.now());
    }


}
