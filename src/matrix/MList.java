package matrix;

import java.util.ArrayList;
import java.util.Collection;

public class MList<T> extends ArrayList<T> {

    private MRelation<T> identity = null;

    public MList(int initialCapacity) {
        super(initialCapacity);
    }

    public MList() {
        super();
    }

    public MList(Collection<? extends T> c) {
        super(c);
    }

    /**
     * idU = {(α, β) : α, β ∈ U and α = β}
     * @return idU
     */
    public MRelation<T> identityRelation() {
        if (identity == null) {
            boolean[][] identityPairs = new boolean[size()][size()];
            for (int i = 0; i < size(); i++) {
                identityPairs[i][i] = true;
            }
            identity = new MRelation<>(this, identityPairs);
        }
        return identity;
    }

    /**
     * @return ∅
     */
    public MRelation<T> emptyRelation() {
        return new MRelation<>(this, new boolean[size()][size()]);
    }

    /**
     * U × U = {(α, β) : α, β ∈ U}
     * @return U × U
     */
    public MRelation<T> universalRelation() {
        boolean[][] allPairs = new boolean[size()][size()];
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                allPairs[i][j] = true;
            }
        }
        return new MRelation<>(this, allPairs);
    }

    /**
     * diU = {(α, β) : α, β ∈ U and α ≠ β}
     * @return diU
     */
    public MRelation<T> diversityRelation() {
        boolean[][] diversityPairs = new boolean[size()][size()];
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (i != j) diversityPairs[i][j] = true;
            }
        }
        return new MRelation<>(this, diversityPairs);
    }

}
