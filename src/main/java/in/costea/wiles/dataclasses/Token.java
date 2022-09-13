package in.costea.wiles.dataclasses;

import java.util.Objects;

public record Token(String content, int line, int index) {

    @Override
    public String toString() {
        return content+" line: "+line+" index: "+index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token)
            return Objects.equals(content, ((Token) obj).content);
        return false;
    }
}
