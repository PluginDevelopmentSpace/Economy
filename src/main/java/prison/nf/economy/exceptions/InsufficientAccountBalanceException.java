package prison.nf.economy.exceptions;

public class InsufficientAccountBalanceException extends BadRequestException
{
    public InsufficientAccountBalanceException()
    {
        super("Insufficient account balance");
    }
}
