package com.uni.share.common.exceptions.types;

import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;

public class ValidationErrorTO extends AbstractBusinessError<ConstraintViolationException> {

    private List<BusinessErrorTO> errors = new ArrayList<>();


    public ValidationErrorTO() {
    }


    public ValidationErrorTO(final List<BusinessErrorTO> errors) {
        this.errors = errors;
    }


    public List<BusinessErrorTO> getErrors() {
        return errors;
    }


    public void setErrors(final List<BusinessErrorTO> errors) {
        this.errors = errors;
    }


    public void addError(final BusinessErrorTO error) {
        this.errors.add(error);
    }


    @Override
    public ValidationErrorTO fromException(final ConstraintViolationException exception) {
        final ValidationErrorTO error = new ValidationErrorTO();
        exception.getConstraintViolations().forEach(it -> {
            final String errorMessage = it.getMessage();
            final BusinessErrorTO errorTO = new BusinessErrorTO();
            errorTO.setErrorMessage(errorMessage);
            error.addError(errorTO);
        });
        return error;

    }


    public static Response buildResponse(final ConstraintViolationException exception) {
        final ValidationErrorTO result = new ValidationErrorTO();
        return Response.status(Response.Status.BAD_REQUEST).entity(result.fromException(exception)).build();
    }
}
