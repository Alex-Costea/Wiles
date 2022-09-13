package in.costea.wiles.dataclasses;

import java.util.Objects;

public record Token(String content, int line, int lineIndex) {

    @Override
    public String toString() {
        return content+" at line: "+line+" index: "+ lineIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token)
            return Objects.equals(content, ((Token) obj).content);
        return false;
    }
}
