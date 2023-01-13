package prison.nf.economy.exceptions;

public class InvalidTransactionAmountException extends BadRequestException
{
    public InvalidTransactionAmountException()
    {
        super("Invalid transaction amount");
    }
}
