import java.io.IOException;
import java.util.Optional;

public class PaymentService {
    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) {
        this.repo = repo;
    }

    public Pagamento createPayment(Integer clienteId, Double valorTotal) throws IOException {
        if (repo.existsPending()) {
            throw new IllegalStateException("Já existe pagamento pendente. Não é possível criar novo.");
        }
        int id = repo.nextId();
        Pagamento p = new Pagamento(id, clienteId, valorTotal);
        repo.appendPagamento(p);
        return p;
    }

    public Optional<Pagamento> processFirstPending() throws IOException {
        Optional<Pagamento> pend = repo.firstPending();
        if (pend.isEmpty()) return Optional.empty();
        Pagamento p = pend.get();
        boolean autorizado = simulateAuthorization(p);
        if (autorizado) repo.updateStatus(p.getId(), "APROVADO");
        else repo.updateStatus(p.getId(), "RECUSADO");
        return repo.getById(p.getId());
    }

    private boolean simulateAuthorization(Pagamento p) {
        return true;
    }

    public ReportResponse generateReport() throws IOException {
        return repo.generateReportAll();
    }
}
