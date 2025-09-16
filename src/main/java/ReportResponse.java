public class ReportResponse {
    private final int quantity;
    private final double total;

    public ReportResponse(int quantity, double total) {
        this.quantity = quantity;
        this.total = total;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return total;
    }
}
