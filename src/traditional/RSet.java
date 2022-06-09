package traditional;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class RSet<T> {

    // todo consider arraylist (much more work)
    // todo make work with open sets

    private Relation<T> identity = null;
    private final HashSet<T> elements;

    public RSet(HashSet<T> elements) {
        this.elements = elements;
    }

    public RSet(Collection<T> elements) {
        this.elements = new HashSet<>(elements);
    }

    public RSet(T[] elements) {
        this.elements = new HashSet<>(Arrays.asList(elements));
    }


    public RSet() {
        this.elements = new HashSet<>();
    }

    public HashSet<T> getElements() {
        return elements;
    }

    public int size() {
        return elements.size();
    }

    /**
     * @param otherSet S
     * @return self ∪ S
     */
    public RSet<T> union(RSet<T> otherSet) {
        HashSet<T> unionSet = new HashSet<>(elements);
        unionSet.addAll(otherSet.elements);
        return new RSet<>(unionSet);
    }

    /**
     * @param otherSet S
     * @return self ∩ S
     */
    public RSet<T> intersection(RSet<T> otherSet) {
        HashSet<T> intersectSet = new HashSet<>(elements);
        intersectSet.retainAll(otherSet.elements);
        return new RSet<>(intersectSet);
    }

    /**
     * @param otherSet S
     * @return self \ S
     */
    public RSet<T> relativeComplement(RSet<T> otherSet) {
        HashSet<T> differenceSet = new HashSet<>(elements);
        differenceSet.removeAll(otherSet.elements);
        return new RSet<>(differenceSet);
    }

    /**
     * idU = {(α, β) : α, β ∈ U and α = β}
     * @return idU
     */
    public Relation<T> identityRelation() {
        if (identity == null) {
            HashSet<Pair<T>> identityPairs = elements.stream().map(e -> new Pair<>(e, e)).collect(Collectors.toCollection(HashSet::new));
            identity = new Relation<>(this, new RSet<>(identityPairs));
        }
        return identity;

    }

    /**
     * ∅ = ∼(∼idU ∪ idU )
     * @return ∅
     */
    public Relation<T> emptyRelation() {
        return new Relation<>(this, new RSet<>());
        // traditional.Relation<T> identity = identityRelation();
        // return identity.complement().union(identity).complement();
    }

    /**
     * U × U = {(α, β) : α, β ∈ U}
     * U × U = ∼idU ∪ idU
     * @return U × U
     */
    public Relation<T> universalRelation() {
        try {
            Relation<T> identity = identityRelation();
            return identity.complement().union(identity);
        } catch (UnmatchedUniversalSetsException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * diU = {(α, β) : α, β ∈ U and α ≠ β}
     * diU = ∼idU
     * @return diU
     */
    public Relation<T> diversityRelation() {
        return identityRelation().complement();
    }

    /**
     * S ⊆ R
     * @param otherSet S
     * @return S ⊆ self
     */
    public boolean contains(RSet<T> otherSet) {
        return elements.containsAll(otherSet.elements);
    }

    /**
     * R = S
     * @param otherSet S
     * @return self = S
     */
    public boolean equals(RSet<T> otherSet) {
        return elements.equals(otherSet.elements);
    }

}
