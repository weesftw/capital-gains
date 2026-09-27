package br.com.weesftw.service;

import br.com.weesftw.domain.Operation;
import br.com.weesftw.domain.OperationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OperationService {

    /**
     * Realiza a iteração para capturar o calculo de imposto aplicado em cada uma das operações em individual.
     *
     * @param operations operações
     * @return lista contendo as taxas aplicadas em cada operação
     */
    public static List<BigDecimal> getTaxes(final List<Operation> operations) {
        final List<BigDecimal> taxes = new ArrayList<>(1);
        final OperationContext context = new OperationContext();

        for (var operation : operations) {
            taxes.add(operation.getTax(context));
        }

        return taxes;
    }
}

