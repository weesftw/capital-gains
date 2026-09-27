package br.com.weesftw.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

class BuyOperation extends Operation {

    public BuyOperation(BigDecimal unitCost, Integer quantity) {
        super(unitCost, quantity);
    }

    @Override
    public BigDecimal getTax(final OperationContext context) {
        if (context.currentQuantity == 0) {
            context.currentVmp = super.unitCost;
        } else {
            final BigDecimal currentQuantity = BigDecimal.valueOf(context.currentQuantity);
            final BigDecimal currentVmp = context.currentVmp;
            final BigDecimal quantity = BigDecimal.valueOf(super.quantity);
            final BigDecimal unitCost = super.unitCost;

            context.currentVmp = this.calculateCurrentVmp(currentQuantity, currentVmp, quantity, unitCost);
        }

        context.currentQuantity += super.quantity;
        return ZERO;
    }

    /**
     * Calcula o novo valor médio ponderado (VMP) após uma compra:
     *       (currentQuantity * currentVmp) + (quantity * unitCost)
     *           --------------------------------------------
     *                 (currentQuantity + quantity)
     *
     * @param currentQuantity quantidade atual antes da compra
     * @param currentVmp valor médio ponderado atual
     * @param quantity quantidade adquirida na operação
     * @param unitCost custo unitário da nova compra
     * @return novo valor médio ponderado
     */
    private BigDecimal calculateCurrentVmp(final BigDecimal currentQuantity,
                                           final BigDecimal currentVmp,
                                           final BigDecimal quantity,
                                           final BigDecimal unitCost) {
        return (currentQuantity.multiply(currentVmp)).add((quantity.multiply(unitCost)))
                .divide(currentQuantity.add(quantity), RoundingMode.HALF_UP);
    }
}
