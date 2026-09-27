package br.com.weesftw.domain

import spock.lang.Specification

import static br.com.weesftw.domain.OperationUtils.*

class SellOperationTest extends Specification {

    def "verificar taxa da operação de venda com prejuizo"() {
        given:
        Operation operation = new SellOperation(new BigDecimal("15000.00"), 10)
        def context = contextWithCurrentVmpAndQuantity("30000.00", 0)

        when:
        BigDecimal tax = operation.getTax(context)

        then:
        tax.toPlainString() == "0.00"
    }

    def "verificar taxa da operação de venda com lucro"() {
        given:
        Operation operation = new SellOperation(new BigDecimal("30000.00"), 6)
        def context = contextWithCurrentVmpAndQuantity("21000.00", 12)

        when:
        BigDecimal tax = operation.getTax(context)

        then:
        tax.toPlainString() == "10800.00"
    }

    def "verificar taxa da operação de venda com lucro porém, com prejuizo"() {
        given:
        Operation operation = new SellOperation(new BigDecimal("30000.00"), 6)
        def context = contextFull("21000.00", 12, "-14321.21")

        when:
        BigDecimal tax = operation.getTax(context)

        then:
        context.loss.toPlainString() == "0.00"
        tax.toPlainString() == "7935.76"
    }

    def "após receber operação de venda: deve ser atualizado a quantidade atual de ações disponiveis e a quantidade de ações de fato vendidas"(OperationContext context, int selled, int expectedQuantity) {
        given:
        Operation operation = new SellOperation(new BigDecimal("50.00"), 10)
        operation.deallocateStock(context)

        expect:
        context.currentQuantity == expectedQuantity
        operation.quantity == selled

        where:
        context                        | selled | expectedQuantity
        contextWithCurrentQuantity(10) | 10     | 0
        contextWithCurrentQuantity(20) | 10     | 10
        contextWithCurrentQuantity(5)  | 5      | 0
        contextWithCurrentQuantity(1)  | 1      | 0
    }

    def "calculo de prejuizo"(OperationContext context, BigDecimal unitCost, BigDecimal expectedLoss) {
        given:
        Operation operation = new SellOperation(unitCost, 5)
        BigDecimal loss = operation.calculateLoss(context)

        expect:
        loss == expectedLoss

        where:
        context                         | unitCost | expectedLoss
        contextWithCurrentVmp("32.12")  | 11.19    | -104.65
        contextWithCurrentVmp("89.87")  | 65.00    | -124.35
        contextWithCurrentVmp("312.22") | 22.01    | -1451.05
    }

    def "calculo de taxa de imposto do lucro conquistado"(OperationContext context, BigDecimal unitCost, BigDecimal expectedTax) {
        given:
        Operation operation = new SellOperation(unitCost, 4)
        BigDecimal tax = operation.calculateTaxGain(context)

        expect:
        tax == expectedTax

        where:
        context                           | unitCost | expectedTax
        contextWithCurrentVmp("6.00")     | 8.00     | 0.00
        contextWithCurrentVmp("20000.00") | 30000.00 | 8000.00
        contextWithCurrentVmp("17000.89") | 23680.00 | 5343.29
    }

    def "calculo de taxa de imposto do lucro conquistado mas com prejuizo de vendas anteriores"(OperationContext context, BigDecimal unitCost, BigDecimal expectedLoss, BigDecimal expectedTax) {
        given:
        Operation operation = new SellOperation(unitCost, 4)
        BigDecimal tax = operation.calculateTaxGain(context)

        expect:
        tax == expectedTax
        context.loss == expectedLoss

        where:
        context                                               | unitCost | expectedLoss | expectedTax
        contextWithCurrentVmpAndLoss("6.00", "-12000.00")     | 8.00     | -12000.00    | 0.00
        contextWithCurrentVmpAndLoss("20000.00", "-90540.12") | 30000.00 | -50540.12    | 0.00
        contextWithCurrentVmpAndLoss("17000.89", "-9876.77")  | 23680.00 | 0            | 3367.93
    }
}
