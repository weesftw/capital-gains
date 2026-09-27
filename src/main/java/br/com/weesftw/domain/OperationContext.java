package br.com.weesftw.domain;

import java.math.BigDecimal;

import static br.com.weesftw.domain.Operation.ZERO;

public class OperationContext {

    BigDecimal currentVmp = ZERO; // valor médio ponderado atual
    Integer currentQuantity = 0;
    BigDecimal loss = ZERO;

    @Override
    public String toString() {
        return "OperationContext{" +
                "currentVmp=" + currentVmp +
                ", currentQuantity=" + currentQuantity +
                ", loss=" + loss +
                '}';
    }
}
