package com.uni.share.common.boundary;

import javax.ejb.Stateless;

/**
 * Simple PING facade.
 *
 * @author Felix Rottler
 */
@Stateless
public class PingBF {

    private static final String PING = "PING";

    public String getPing() {
        return PING;
    }
}
