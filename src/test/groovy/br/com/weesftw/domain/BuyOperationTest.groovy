package br.com.weesftw.domain

import spock.lang.Specification

import static br.com.weesftw.domain.OperationUtils.contextWithCurrentVmpAndQuantity

class BuyOperationTest extends Specification {

    def "após receber operação de compra, o contexto deve ser atualizado com a nova quantidade e o valor médio ponderado recalculado"(OperationContext context, int quantity, BigDecimal vmp) {
        given:
        Operation operation = new BuyOperation(new BigDecimal("50.00"), 30)
        BigDecimal tax = operation.getTax(context)

        expect:
        tax.toPlainString() == "0.00"
        context.currentQuantity == quantity
        context.currentVmp == vmp

        where:
        context                                       | quantity | vmp
        contextWithCurrentVmpAndQuantity("12.00", 10) | 40       | 40.50
        contextWithCurrentVmpAndQuantity("42.00", 20) | 50       | 46.80
    }

    def "após receber operação de compra, se a quantidade atual estiver zerada, o valor médio ponderado deve ser atualizado para o valor da própria compra, assim como a nova quantidade"() {
        given:
        Operation operation = new BuyOperation(new BigDecimal("123.00"), 72)
        def context = contextWithCurrentVmpAndQuantity("15.00", 0)

        when:
        BigDecimal tax = operation.getTax(context)

        then:
        tax.toPlainString() == "0.00"
        context.currentVmp == 123.00
        context.currentQuantity == 72
    }
}
