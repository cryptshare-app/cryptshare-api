package com.uni.share.common.exceptions.types;

public abstract class AbstractBusinessError<T extends RuntimeException> {

    public abstract AbstractBusinessError fromException(final T t);
}
