package br.com.weesftw.domain;

import java.math.BigDecimal;

class SellOperation extends Operation {

    static final BigDecimal TAX_PERCENTAGE = new BigDecimal("0.20");
    static final BigDecimal TAX_LIMIT_VALUE = new BigDecimal("20000.00");

    public SellOperation(BigDecimal unitCost, Integer quantity) {
        super(unitCost, quantity);
    }

    @Override
    public BigDecimal getTax(final OperationContext context) {
        if (context.currentQuantity == 0) {
            return ZERO;
        }

        this.deallocateStock(context);

        final boolean hasLoss = super.unitCost.compareTo(context.currentVmp) < 0;
        if (hasLoss) {
            final BigDecimal calculateLoss = this.calculateLoss(context);
            context.loss = context.loss.add(calculateLoss);
            return ZERO;
        }

        return this.calculateTaxGain(context);
    }

    void deallocateStock(final OperationContext context) {
        if (super.quantity >= context.currentQuantity) {
            super.quantity = context.currentQuantity;
            context.currentQuantity = 0;
            return;
        }

        context.currentQuantity -= super.quantity;
    }

    BigDecimal calculateLoss(final OperationContext context) {
        final BigDecimal currentVmp = context.currentVmp;
        final BigDecimal unitCost = super.unitCost;
        final BigDecimal quantity = BigDecimal.valueOf(super.quantity);

        return this.calculateGainOrLoss(unitCost, currentVmp, quantity);
    }

    BigDecimal calculateTaxGain(final OperationContext context) {
        final BigDecimal currentVmp = context.currentVmp;
        final BigDecimal quantity = BigDecimal.valueOf(super.quantity);
        final BigDecimal unitCost = super.unitCost;
        final BigDecimal loss = context.loss;

        /*
         * Se a venda total atingir o limite de isenção ou menos, mesmo tendo lucro:
         * não é pago imposto e NÃO pode usar esse lucro para diminuir prejuízos anteriores.
         */
        final BigDecimal saleTotal = unitCost.multiply(quantity);
        if (saleTotal.compareTo(TAX_LIMIT_VALUE) <= 0) {
            return ZERO;
        }

        BigDecimal gain = this.calculateGainOrLoss(unitCost, currentVmp, quantity);
        if (context.loss.compareTo(ZERO) < 0) {
            final BigDecimal auxGain = gain;

            gain = gain.add(loss).max(ZERO);
            context.loss = loss.add(auxGain).min(ZERO);
        }

        if (gain.compareTo(ZERO) > 0) {
            return super.roundDecimal(gain.multiply(TAX_PERCENTAGE));
        }

        return ZERO;
    }

    /**
     * Calcula a operação: (unitCost - currentVmp) * quantity.
     *
     * @param unitCost custo por unidade
     * @param currentVmp valor médio ponderado atual
     * @param quantity quantidade
     * @return BigDecimal representando o resultado bruto (positivo = ganho, negativo = prejuizo)
     */
    private BigDecimal calculateGainOrLoss(final BigDecimal unitCost,
                                           final BigDecimal currentVmp,
                                           final BigDecimal quantity) {
        return unitCost.subtract(currentVmp)
                .multiply(quantity);
    }
}
