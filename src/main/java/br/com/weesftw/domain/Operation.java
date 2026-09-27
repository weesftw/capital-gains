package br.com.weesftw.domain;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Operation {

    protected static final BigDecimal ZERO = new BigDecimal("0.00");

    protected BigDecimal unitCost;
    protected Integer quantity;

    protected Operation(BigDecimal unitCost, Integer quantity) {
        this.unitCost = unitCost;
        this.quantity = quantity;
    }

    /**
     * Cria uma instância de {@link Operation} com base no tipo especificado no objeto JSON.
     * O tipo da operação deve ser informado no campo {@code "operation"} (ex: {@code "buy"} ou {@code "sell"}).
     *
     * @param json objeto JSON contendo os dados da operação
     * @return uma instância de {@link BuyOperation} ou {@link SellOperation ou {@code null} caso o tipo de operação não seja reconhecido
     */
    public static Operation build(JSONObject json) {
        final String operation = json.getString("operation");
        final BigDecimal unitCost = json.optBigDecimal("unit-cost", ZERO);
        final Integer quantity = json.optIntegerObject("quantity", 0);

        return switch (operation) {
            case "buy" -> new BuyOperation(unitCost, quantity);
            case "sell" -> new SellOperation(unitCost, quantity);
            default -> null;
        };
    }

    /**
     * Calcula o valor do imposto de renda a ser cobrado da operação.
     *
     * @param context contexto contendo as informações necessárias para o cálculo
     * @return valor do imposto calculado
     */
    public abstract BigDecimal getTax(final OperationContext context);

    /**
     * Arredonda o valor para duas casas decimais.
     *
     * @param value valor a ser arredondado (ex.: 16.6666666667)
     * @return valor arredondado para duas casas decimais (ex.: 16.67)
     */
    protected BigDecimal roundDecimal(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
