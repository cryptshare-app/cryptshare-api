package com.uni.share.authentication.types;

import com.uni.share.authentication.control.JWTValidationException;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class ValidationResult {
    private final boolean value;

    public ValidationResult(final boolean value) {
        this.value = value;
    }

    /**
     * @param supplier
     * @return
     */
    public boolean orElseThrow(Supplier<? extends JWTValidationException> supplier) {
        if (!this.value) {
            throw supplier.get();
        }
        return true;
    }

    /**
     * @param value
     * @return
     */
    public static ValidationResult of(final boolean value) {
        return new ValidationResult(value);
    }

    /**
     * @return
     */
    public boolean isValid() {
        return this.value;
    }

    /**
     * @return
     */
    public boolean get() {
        if (!value) {
            throw new NoSuchElementException("No value present");
        }
        return true;

    }
}