package exception;
public class LoanLimitExceededException extends Exception {
    public LoanLimitExceededException(String msg) { super(msg); }
}