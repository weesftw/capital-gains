package br.com.weesftw

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ApplicationCLITest extends Specification {

    def "operações processadas através da leitura de um arquivo .txt"() {
        given:
        Path path = Paths.get("input.txt")
        byte[] fileBytes = Files.readAllBytes(path)
        InputStream fakeIn = new ByteArrayInputStream(fileBytes)

        when:
        String result = this.runWithInput(fakeIn)

        then:
        result == '[{"tax": 0.00}, {"tax": 10000.00}]'
                .concat(System.lineSeparator())
                .concat('[{"tax": 0.00}, {"tax": 0.00}]')
                .concat(System.lineSeparator())
                .concat('[{"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 16600.00}, {"tax": 9000.00}]')
                .concat(System.lineSeparator())
    }

    def "case #1"() {
        given:
        String line1 = '[{"operation":"buy", "unit-cost":10.00, "quantity": 100}, {"operation":"sell", "unit-cost":15.00, "quantity": 50}, {"operation":"sell", "unit-cost":15.00, "quantity": 50}]'

        when:
        String result = this.runWithInput(line1)

        then:
        result == '[{"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}]'.concat(System.lineSeparator())
    }

    def "case #2"() {
        given:
        String line1 = '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"sell", "unit-cost":20.00, "quantity": 5000}, {"operation":"sell", "unit-cost":5.00, "quantity": 5000}]'

        when:
        String result = this.runWithInput(line1)

        then:
        result == '[{"tax": 0.00}, {"tax": 10000.00}, {"tax": 0.00}]'.concat(System.lineSeparator())
    }

    def "case #1 + case #2"() {
        given:
        String line1 = '[{"operation":"buy", "unit-cost":10.00, "quantity": 100}, {"operation":"sell", "unit-cost":15.00, "quantity": 50}, {"operation":"sell", "unit-cost":15.00, "quantity": 50}]'
        String line2 = '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"sell", "unit-cost":20.00, "quantity": 5000}, {"operation":"sell", "unit-cost":5.00, "quantity": 5000}]'

        when:
        String result = this.runWithInput(line1, line2)

        then:
        result == '[{"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}]'
                .concat(System.lineSeparator())
                .concat('[{"tax": 0.00}, {"tax": 10000.00}, {"tax": 0.00}]')
                .concat(System.lineSeparator())
    }

    def "case #3"() {
        given:
        String line1 = '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"sell", "unit-cost":5.00, "quantity": 5000}, {"operation":"sell", "unit-cost":20.00, "quantity": 3000}]'

        when:
        String result = this.runWithInput(line1)

        then:
        result == '[{"tax": 0.00}, {"tax": 0.00}, {"tax": 1000.00}]'.concat(System.lineSeparator())
    }

    def "case #4"() {
        given:
        String line1 = '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"buy", "unit-cost":25.00, "quantity": 5000}, {"operation":"sell", "unit-cost":15.00, "quantity": 10000}]'

        when:
        String result = this.runWithInput(line1)

        then:
        result == '[{"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}]'.concat(System.lineSeparator())
    }

    def "case #5"() {
        given:
        String line1 = '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"buy", "unit-cost":25.00, "quantity": 5000}, {"operation":"sell", "unit-cost":15.00, "quantity": 10000}, {"operation":"sell", "unit-cost":25.00, "quantity": 5000}]'

        when:
        String result = this.runWithInput(line1)

        then:
        result == '[{"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 10000.00}]'.concat(System.lineSeparator())
    }

    def "case #6"() {
        given:
        String line1 = '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"sell", "unit-cost":2.00, "quantity": 5000}, {"operation":"sell", "unit-cost":20.00, "quantity": 2000}, {"operation":"sell", "unit-cost":20.00, "quantity": 2000}, {"operation":"sell", "unit-cost":25.00, "quantity": 1000}]'

        when:
        String result = this.runWithInput(line1)

        then:
        result == '[{"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 3000.00}]'.concat(System.lineSeparator())
    }

    def "case #7"() {
        given:
        String line1 = '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"sell", "unit-cost":2.00, "quantity": 5000}, {"operation":"sell", "unit-cost":20.00, "quantity": 2000}, {"operation":"sell", "unit-cost":20.00, "quantity": 2000}, {"operation":"sell", "unit-cost":25.00, "quantity": 1000}, {"operation":"buy", "unit-cost":20.00, "quantity": 10000}, {"operation":"sell", "unit-cost":15.00, "quantity": 5000}, {"operation":"sell", "unit-cost":30.00, "quantity": 4350}, {"operation":"sell", "unit-cost":30.00, "quantity": 650}]'

        when:
        String result = this.runWithInput(line1)

        then:
        result == '[{"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 3000.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 3700.00}, {"tax": 0.00}]'.concat(System.lineSeparator())
    }

    def "case #8"() {
        given:
        String line1 = '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"sell", "unit-cost":50.00, "quantity": 10000}, {"operation":"buy", "unit-cost":20.00, "quantity": 10000}, {"operation":"sell", "unit-cost":50.00, "quantity": 10000}]'

        when:
        String result = this.runWithInput(line1)

        then:
        result == '[{"tax": 0.00}, {"tax": 80000.00}, {"tax": 0.00}, {"tax": 60000.00}]'.concat(System.lineSeparator())
    }

    def "case #9"() {
        given:
        String line1 = '[{"operation": "buy", "unit-cost": 5000.00, "quantity": 10}, {"operation": "sell", "unit-cost": 4000.00, "quantity": 5}, {"operation": "buy", "unit-cost": 15000.00, "quantity": 5}, {"operation": "buy", "unit-cost": 4000.00, "quantity": 2}, {"operation": "buy", "unit-cost": 23000.00, "quantity": 2}, {"operation": "sell", "unit-cost": 20000.00, "quantity": 1}, {"operation": "sell", "unit-cost": 12000.00, "quantity": 10}, {"operation": "sell", "unit-cost": 15000.00, "quantity": 3}]'

        when:
        String result = this.runWithInput(line1)

        then:
        result == '[{"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 1000.00}, {"tax": 2400.00}]'.concat(System.lineSeparator())
    }

    def "operações com venda de ações acima da quantidade disponível no estoque"() {
        given:
        String line1 = '[ {"operation": "buy",  "unit-cost": 5000.00, "quantity": 10}, {"operation": "sell", "unit-cost": 4000.00, "quantity": 12}, {"operation": "buy",  "unit-cost": 15000.00, "quantity": 6}, {"operation": "sell", "unit-cost": 16000.00, "quantity": 3}, {"operation": "sell", "unit-cost": 60000.00, "quantity": 2}, {"operation": "sell", "unit-cost": 60000.00, "quantity": 3} ]'

        when:
        String result = this.runWithInput(line1)

        then:
        result == '[{"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}, {"tax": 16600.00}, {"tax": 9000.00}]'.concat(System.lineSeparator())
    }

    def "operações com 'operation' inválido, devem ser ignorados"() {
        given:
        String line1 = '[{"operation":"buy", "unit-cost":10.00, "quantity": 100}, {"operation":"selll", "unit-cost":15.00, "quantity": 50}, {"operation":"sell", "unit-cost":15.00, "quantity": 50}, {"operation":"foo", "unit-cost":10.00, "quantity": 100}, {"operation":"sell", "unit-cost":15.00, "quantity": 50}]'

        when:
        String result = this.runWithInput(line1)

        then:
        result == '[{"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}]'.concat(System.lineSeparator())
    }

    private String runWithInput(String... inputs) {
        String fullInput = inputs.collect { String input -> input + System.lineSeparator() }.join()
        InputStream fakeIn = new ByteArrayInputStream((fullInput + System.lineSeparator()).bytes)
        OutputStream fakeOut = new ByteArrayOutputStream()

        ApplicationCLI.run(fakeIn, fakeOut)

        return new String(fakeOut.toByteArray())
    }

    private String runWithInput(InputStream fakeIn) {
        OutputStream fakeOut = new ByteArrayOutputStream()

        ApplicationCLI.run(fakeIn, fakeOut)

        return new String(fakeOut.toByteArray())
    }
}