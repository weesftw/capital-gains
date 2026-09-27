package br.com.weesftw.domain

import br.com.weesftw.domain.OperationUtils.NoopOperation
import spock.lang.Specification

class OperationTest extends Specification {

    def "deve ser arredondado os números racionais"(BigDecimal actual, BigDecimal expected) {
        expect:
        Operation unit = new NoopOperation()
        unit.roundDecimal(actual) == expected

        where:
        actual             | expected
        16.6666666667      | 16.67
        123.455555         | 123.46
    }
}
