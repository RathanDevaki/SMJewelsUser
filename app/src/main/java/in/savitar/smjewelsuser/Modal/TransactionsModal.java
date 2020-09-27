package in.savitar.smjewelsuser.Modal;

public class TransactionsModal {

    private String Comments;
    private String Date;
    private String Amount;

    public TransactionsModal() {
    }

    public TransactionsModal(String comments, String date, String amount) {
        Comments = comments;
        Date = date;
        Amount = amount;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }
}
