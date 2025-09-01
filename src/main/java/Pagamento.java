import java.time.Instant;

public class Pagamento {
    private Integer id;
    private final Integer clienteId;
    private final Instant data;
    private final Double valorTotal;
    private final String metodoPagamento;
    private String status;

    public Pagamento(Integer id, Integer clienteId, Double valorTotal, String metodo) {
        this.id = id;
        this.clienteId = clienteId;
        this.valorTotal = valorTotal;
        this.metodoPagamento = metodo;
        this.data = Instant.now();
        this.status = "PENDENTE";
    }

    public void marcarPendente() {
        this.status = "PENDENTE";
    }
    public void marcarValidado() {
        this.status = "VALIDADO";
    }
    public void marcarAprovado() {
        this.status = "APROVADO";
    }
    public void marcarFalha(String motivo) {
        this.status = "FALHA: " + motivo;
    }

    public Integer getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }
}
