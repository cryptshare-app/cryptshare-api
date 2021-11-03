package com.uni.share.common.mapper;


import javax.ws.rs.core.Response;

/**
 * Helper class to wrap entity objects into responses.
 *
 * @author Felix Rottler , 07.11.2018
 **/
public final class ResponseMapper {
    /**
     * Empty constructor.
     */
    private ResponseMapper() {
        // empty constructor.
    }

    /**
     * Maps a given entity to an 200 OK response
     *
     * @param entity the entity to map
     * @return the response wrapping the given entity.
     */
    public static Response map(final Object entity) {
        return Response.ok(entity).build();
    }
}
