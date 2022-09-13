package in.costea.wiles.data;

import org.jetbrains.annotations.Contract;

public record TokenLocation(int line, int lineIndex) {
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TokenLocation locationObj)
            return line()==locationObj.line() || lineIndex()== locationObj.lineIndex();
        return false;
    }
}
