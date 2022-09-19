package in.costea.wiles.commands;

import in.costea.wiles.services.TokenTransmitter;

public abstract class AbstractOperationComponent extends SyntaxTree
{
    public AbstractOperationComponent(TokenTransmitter transmitter)
    {
        super(transmitter);
    }
}
