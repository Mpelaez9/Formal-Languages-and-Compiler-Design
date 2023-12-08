package domain;

public class Pair<T,Q> {

    private T key;
    private Q value;

    public Pair(T key,Q value) {
        this.key=key;
        this.value=value;
    }

    public T getKey() {
        return key;
    }

    public Q getValue() {
        return value;
    }
}
