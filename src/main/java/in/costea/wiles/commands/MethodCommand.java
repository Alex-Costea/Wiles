package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.services.TokenTransmitter;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.*;

public class MethodCommand extends SyntaxTree {
    private final List<SyntaxTree> components=new ArrayList<>();
    private String methodName;
    private final CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();

    public MethodCommand(TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    public SYNTAX_TYPE getType() {
        return SYNTAX_TYPE.METHOD;
    }

    @Override
    public List<SyntaxTree> getComponents() {
        return components;
    }

    @Override
    public CompilationExceptionsCollection process() {
        try {
            methodName=expect(x->x.length()>1 && x.startsWith(IDENTIFIER_START),"Expected method name!").
                    content().substring(1);
            expect(ROUND_BRACKET_START_ID);
            //TODO: method declaration
            expect(ROUND_BRACKET_END_ID);
            expect(START_BLOCK_ID);
            //method body
            var MethodBodyCommand = new MethodBodyCommand(transmitter,false);
            exceptions.add(MethodBodyCommand.process());
            components.add(MethodBodyCommand);
            expect(END_BLOCK_ID);
            try
            {
                expect(x -> x.equals(NEWLINE_ID) || x.equals(FINISH_STATEMENT), "Expected line end!");
            }
            catch(UnexpectedEndException ignored) {}
        }
        catch (CompilationException ex)
        {
            exceptions.add(ex);
            readRestOfLineIgnoringErrors(false);
        }
        return exceptions;
    }

    @Override
    public String toString() {
        return super.toString(methodName!=null?methodName:"");
    }
}
