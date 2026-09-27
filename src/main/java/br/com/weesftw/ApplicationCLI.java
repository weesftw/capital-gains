package br.com.weesftw;

import br.com.weesftw.domain.Operation;
import br.com.weesftw.service.OperationService;
import org.json.JSONArray;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.IntStream;

public class ApplicationCLI {

    public static void main(String[] args) throws Exception {
        run(System.in, System.out);
    }

    static void run(InputStream in, OutputStream out) throws Exception {
        final Scanner scanner = new Scanner(in);

        while (scanner.hasNextLine()) {
            final String stdin = scanner.nextLine();
            if (stdin.isEmpty())
                break;

            final JSONArray line = new JSONArray(stdin);
            final List<Operation> operations = IntStream.range(0, line.length())
                    .mapToObj(line::getJSONObject)
                    .map(Operation::build)
                    .filter(Objects::nonNull)
                    .toList();
            final List<BigDecimal> taxes = OperationService.getTaxes(operations);

            final String stdout = taxes.stream()
                    .map("{\"tax\": %s}"::formatted)
                    .toList()
                    .toString();

            out.write(stdout.getBytes());
            out.write(System.lineSeparator().getBytes());
            out.flush();
        }
    }
}

