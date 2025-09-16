import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class PaymentRepository {
    private final Path path;
    private static final String HEADER = "id,clienteId,valor,status";

    public PaymentRepository(String filename) {
        this.path = Paths.get(filename);
    }

    public synchronized void ensureFileAndHeader() throws IOException {
        if (Files.notExists(path) || Files.size(path) == 0) {
            Files.write(path, List.of(HEADER), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return;
        }
        List<String> lines = Files.readAllLines(path);
        if (lines.isEmpty() || !lines.getFirst().equals(HEADER)) {
            Files.write(path, List.of(HEADER), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    public synchronized List<Pagamento> readAll() throws IOException {
        ensureFileAndHeader();
        List<String> lines = Files.readAllLines(path);
        List<Pagamento> list = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String l = lines.get(i).trim();
            if (l.isEmpty()) continue;
            Optional<Pagamento> op = Pagamento.fromCSV(l);
            op.ifPresent(list::add);
        }
        return list;
    }

    public synchronized void appendPagamento(Pagamento p) throws IOException {
        if (p == null) {
            throw new IllegalArgumentException("Pagamento não pode ser null");
        }
        ensureFileAndHeader();
        Files.write(path, List.of(p.toCSVLine()), StandardOpenOption.APPEND);
    }

    public synchronized Optional<Pagamento> firstPending() throws IOException {
        return readAll().stream().filter(Pagamento::isPendente).findFirst();
    }

    public synchronized boolean existsPending() throws IOException {
        return firstPending().isPresent();
    }

    public synchronized int nextId() throws IOException {
        return readAll().stream().mapToInt(Pagamento::getId).max().orElse(0) + 1;
    }

    public synchronized void updateStatus(int id, String novoStatus) throws IOException {
        ensureFileAndHeader();
        List<String> lines = Files.readAllLines(path);
        List<String> out = new ArrayList<>();
        if (lines.isEmpty()) {
            out.add(HEADER);
        } else {
            out.add(lines.getFirst());
        }

        for (int i = 1; i < lines.size(); i++) {
            String[] cols = lines.get(i).split(",", -1);
            if (cols.length < 4) {
                out.add(lines.get(i));
                continue;
            }
            try {
                int curId = Integer.parseInt(cols[0]);
                if (curId == id) {
                    cols[3] = novoStatus;
                }
            } catch (NumberFormatException _) {
            }
            out.add(String.join(",", cols));
        }
        Files.write(path, out, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public synchronized Optional<Pagamento> getById(int id) throws IOException {
        return readAll().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public synchronized ReportResponse generateReportAll() throws IOException {
        List<Pagamento> all = readAll();
        int qtd = all.size();
        double soma = all.stream().mapToDouble(Pagamento::getValorTotal).sum();
        return new ReportResponse(qtd, soma);
    }
}