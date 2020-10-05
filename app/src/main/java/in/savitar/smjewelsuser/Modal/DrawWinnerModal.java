package in.savitar.smjewelsuser.Modal;

public class DrawWinnerModal {

private String Month;
private String Winner1;
private String Winner2;
private String Photo1;
private String Photo2;
private String UserID1;
private String UserID2;

    public DrawWinnerModal() {
    }

    public DrawWinnerModal(String month, String winner1, String winner2, String photo1, String photo2, String userID1, String userID2) {
        Month = month;
        Winner1 = winner1;
        Winner2 = winner2;
        Photo1 = photo1;
        Photo2 = photo2;
        UserID1 = userID1;
        UserID2 = userID2;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getWinner1() {
        return Winner1;
    }

    public void setWinner1(String winner1) {
        Winner1 = winner1;
    }

    public String getWinner2() {
        return Winner2;
    }

    public void setWinner2(String winner2) {
        Winner2 = winner2;
    }

    public String getPhoto1() {
        return Photo1;
    }

    public void setPhoto1(String photo1) {
        Photo1 = photo1;
    }

    public String getPhoto2() {
        return Photo2;
    }

    public void setPhoto2(String photo2) {
        Photo2 = photo2;
    }

    public String getUserID1() {
        return UserID1;
    }

    public void setUserID1(String userID1) {
        UserID1 = userID1;
    }

    public String getUserID2() {
        return UserID2;
    }

    public void setUserID2(String userID2) {
        UserID2 = userID2;
    }
}
