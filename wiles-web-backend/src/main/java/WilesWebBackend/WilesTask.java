package WilesWebBackend;

import wiles.WilesCompiler;
import wiles.shared.OutputData;

import java.util.List;
import java.util.concurrent.Callable;

public class WilesTask implements Callable<OutputData> {
    List<String> args;

    public WilesTask(List<String> args)
    {
        this.args = args;
    }
    @Override
    public OutputData call() {
        return WilesCompiler.getOutput(args.toArray(java.lang.String[]::new));
    }
}
