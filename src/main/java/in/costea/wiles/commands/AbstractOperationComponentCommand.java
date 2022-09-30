package in.costea.wiles.commands;

import in.costea.wiles.services.TokenTransmitter;

public abstract class AbstractOperationComponentCommand extends AbstractCommand
{
    public AbstractOperationComponentCommand(TokenTransmitter transmitter)
    {
        super(transmitter);
    }
}
