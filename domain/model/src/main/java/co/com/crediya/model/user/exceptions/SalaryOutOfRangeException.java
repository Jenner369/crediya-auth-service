package co.com.crediya.model.user.exceptions;

import co.com.crediya.model.common.exceptions.DomainException;

import java.math.BigDecimal;

public class SalaryOutOfRangeException extends DomainException {
    public SalaryOutOfRangeException(BigDecimal lowerBound, BigDecimal upperBound) {
        super("USER_SALARY_OUT_OF_RANGE", String.format("El salario debe estar entre %s y %s", lowerBound, upperBound));
    }
}
