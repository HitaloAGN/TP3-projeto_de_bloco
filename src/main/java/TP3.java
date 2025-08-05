import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class TP3 {
    private static final String CSV = "pagamentos.csv";
    private static final String HEADER = "id,clienteId,valor,status";

    public static void main(String[] args) {
        try {
            Path path = Paths.get(CSV);
            boolean needHeader = Files.notExists(path)
                    || Files.size(path) == 0
                    || !Files.readAllLines(path).getFirst().equals(HEADER);

            if (needHeader) {
                Files.write(path, List.of(HEADER), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }

            List<String> linhas = Files.readAllLines(path);

            boolean existePendente = linhas.stream()
                    .skip(1)
                    .map(line -> line.split(","))
                    .anyMatch(cols -> cols.length > 3
                            && "PENDENTE".equalsIgnoreCase(cols[3].trim()));

            if (existePendente) {
                System.out.println("Já existe pagamento pendente. Nenhum registro foi adicionado.");
                return;
            }

            int nextId = linhas.size(); // header + registros
            String novaLinha = String.join(",", String.valueOf(nextId), "123", "99.90", "PENDENTE");
            Files.write(path, List.of(novaLinha), StandardOpenOption.APPEND);
            System.out.println("Novo pagamento registrado: " + novaLinha);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
