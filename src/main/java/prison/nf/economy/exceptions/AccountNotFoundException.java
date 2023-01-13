package prison.nf.economy.exceptions;

public class AccountNotFoundException extends BadRequestException
{
    public AccountNotFoundException()
    {
        super("Account not found");
    }
}
