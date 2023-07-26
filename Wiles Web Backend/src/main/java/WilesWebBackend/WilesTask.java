package WilesWebBackend;

import kotlin.Pair;
import wiles.WilesCompiler;

import java.util.List;
import java.util.concurrent.Callable;

public class WilesTask implements Callable<Pair<String, String>> {
    List<String> args;

    public WilesTask(List<String> args)
    {
        this.args = args;
    }
    @Override
    public Pair<String, String> call() {
        return WilesCompiler.getOutput(args.toArray(java.lang.String[]::new));
    }
}
