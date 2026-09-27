package br.com.weesftw

import br.com.weesftw.domain.Operation
import br.com.weesftw.domain.OperationUtils.NoopOperation
import br.com.weesftw.service.OperationService
import spock.lang.Specification

class OperationServiceTest extends Specification {

    def "deve retornar as taxas aplicadas em cada operação processada"() {
        given:
        List<Operation> operations = List.of(
                new NoopOperation("3.00"),
                new NoopOperation("6.00"),
                new NoopOperation("7.00"),
        )

        when:
        List<BigDecimal> taxes = OperationService.getTaxes(operations)

        then:
        taxes.size() == 3
        taxes.toString() == '[3.00, 6.00, 7.00]'
    }
}
