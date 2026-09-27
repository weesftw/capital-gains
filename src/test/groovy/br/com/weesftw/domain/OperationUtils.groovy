package br.com.weesftw.domain

class OperationUtils {

    static class NoopOperation extends Operation {

        private BigDecimal tax = BigDecimal.ZERO;

        NoopOperation() {
            super(BigDecimal.ZERO, 0)
        }

        NoopOperation(String tax) {
            super(BigDecimal.ZERO, 0)
            this.tax = new BigDecimal(tax)
        }

        @Override
        BigDecimal getTax(OperationContext context) {
            return super.roundDecimal(tax)
        }
    }

    static OperationContext contextWithCurrentVmp(String currentVmp) {
        def context = new OperationContext()
        context.currentVmp = new BigDecimal(currentVmp)
        return context
    }

    static OperationContext contextWithCurrentQuantity(int currentQuantity) {
        def context = new OperationContext()
        context.currentQuantity = currentQuantity
        return context
    }

    static OperationContext contextWithCurrentVmpAndQuantity(String currentVmp, int currentQuantity) {
        def context = new OperationContext()
        context.currentVmp = new BigDecimal(currentVmp)
        context.currentQuantity = currentQuantity
        return context
    }

    static OperationContext contextWithCurrentVmpAndLoss(String currentVmp, String loss) {
        def context = new OperationContext()
        context.currentVmp = new BigDecimal(currentVmp)
        context.loss = new BigDecimal(loss)
        return context
    }

    static OperationContext contextFull(String currentVmp, int currentQuantity, String loss) {
        def context = new OperationContext()
        context.currentVmp = new BigDecimal(currentVmp)
        context.currentQuantity = currentQuantity
        context.loss = new BigDecimal(loss)
        return context
    }
}
