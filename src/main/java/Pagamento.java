import java.util.Optional;

public class Pagamento {
    private final Integer id;
    private final Integer clienteId;
    private final Double valorTotal;
    private String status;

    public Pagamento(Integer id, Integer clienteId, Double valorTotal) {
        this.id = id;
        this.clienteId = clienteId;
        this.valorTotal = valorTotal;
        this.status = "PENDENTE";
    }

    public Integer getId() {
        return id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public boolean isPendente() {
        return status != null && status
                .equalsIgnoreCase("PENDENTE");
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toCSVLine() {
        return String.join(",", String.valueOf(id),
                String.valueOf(clienteId),
                String.valueOf(valorTotal),
                status == null ? "" : status);
    }

    public static Optional<Pagamento> fromCSV(String line) {
        try {
            String[] cols = line.split(",", -1);
            if (cols.length < 4) return Optional.empty();
            int id = Integer.parseInt(cols[0]);
            int clienteId = Integer.parseInt(cols[1]);
            double valor = Double.parseDouble(cols[2]);
            String status = cols[3];
            Pagamento p = new Pagamento(id, clienteId, valor);
            p.setStatus(status);
            return Optional.of(p);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return "Pagamento{id=" + id + ", clienteId=" + clienteId + ", valor=" + valorTotal + ", status=" + status + "}";
    }
}
