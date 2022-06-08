import java.util.HashSet;
import java.util.stream.Collectors;

public class Relation<T> {

    // todo add checks for same universal set for methods that take
    //  in another relation
    // todo maybe generate the identity for the universal set for each
    //  relation (depends of usage of methods that require it)
    // todo maybe think about extracting some operations (e.g. conjugated
    //  quasi-projections) to a separate service class

    private final RSet<T> universalSet;
    private final RSet<Pair<T>> pairs;

    public Relation(RSet<T> universalSet, RSet<Pair<T>> pairs) {
        this.universalSet = universalSet;
        this.pairs = pairs;
    }

    /**
     * R ∪ S = {(α, β):(α, β) ∈ R or (α, β) ∈ S}
     * @param otherRelation S
     * @return self ∪ S
     */
    public Relation<T> union(Relation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        return new Relation<>(universalSet, pairs.union(otherRelation.pairs));
    }

    /**
     * ∼R = {(α, β):(α, β) ∈ U × U and (α, β) ∉ R}
     * @return ∼self
     */
    public Relation<T> complement() {
        return new Relation<>(universalSet, universalSet.universalRelation().pairs.relativeComplement(pairs));
    }

    /**
     * R ∩ S = {(α, β):(α, β) ∈ R and (α, β) ∈ S}
     * R ∩ S = ∼(∼R ∪ ∼S)
     * @param otherRelation S
     * @return self ∩ S
     */
    public Relation<T> intersection(Relation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        return new Relation<>(universalSet, pairs.intersection(otherRelation.pairs));
        // you can implement intersection using complement and union but easy to implement without
        // return complement().union(otherRelation.complement()).complement();
    }

    /**
     * R ∼ S = {(α, β):(α, β) ∈ R and (α, β) ∉ S}
     * R ∼ S = R ∩ ∼S = ∼(∼R ∪ S)
     * @param otherRelation S
     * @return self ∼ S
     */
    public Relation<T> difference(Relation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        return complement().union(otherRelation).complement();
    }

    /**
     * R Δ S = {(α, β):(α, β) ∈ R ∼ S or (α, β) ∈ S ∼ R}
     * R Δ S = (R ∼ S) ∪ (S ∼ R)
     * @param otherRelation S
     * @return self Δ S
     */
    public Relation<T> symmetricDifference(Relation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        return difference(otherRelation).union(otherRelation.difference(this));
    }

    /**
     * R | S = {(α, β):(α, γ) ∈ R and (γ,β) ∈ S for some γ ∈ U}
     * @param otherRelation S
     * @return self | S
     */
    public Relation<T> composition(Relation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        // todo not sure if this is the best way to do it if this works
        HashSet<Pair<T>> selfElements = new HashSet<>(pairs.getElements());
        HashSet<Pair<T>> otherElements = new HashSet<>(pairs.getElements());
        HashSet<Pair<T>> composedElements = new HashSet<>();
        for (Pair<T> selfPair : selfElements) {
            for (Pair<T> otherPair : otherElements) {
                if (selfPair.getSecond().equals(otherPair.getFirst())) {
                    composedElements.add(new Pair<>(selfPair.getFirst(), otherPair.getSecond()));
                }
            }
        }
        return new Relation<>(universalSet, new RSet<>(composedElements));
    }

    /**
     * R † S = {(α, β):(α, γ) ∈ R or (γ,β) ∈ S for all γ ∈ U}
     * R † S = ∼(∼R | ∼S)
     * @param otherRelation S
     * @return self † S
     */
    public Relation<T> sum(Relation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        return complement().composition(otherRelation.complement()).complement();
    }

    /**
     * R⁻¹ = {(α, β):(β,α) ∈ R}
     * @return self⁻¹
     */
    public Relation<T> converse() {
        HashSet<Pair<T>> reversedSet = new HashSet<>(pairs.getElements());
        reversedSet.forEach(Pair::reverse);
        return new Relation<>(universalSet, new RSet<>(reversedSet));
    }

    /**
     * R ⊆ S
     * @param otherRelation S
     * @return self ⊆ S
     */
    public boolean isSubsetOf(Relation<T> otherRelation) throws UnmatchedUniversalSetsException {
        // todo maybe use R ⊆ S if and only if R ∪ S = S
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        return otherRelation.pairs.contains(pairs);
    }


    /**
     * R = S
     * @param otherRelation S
     * @return R = S
     */
    public boolean isEqual(Relation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        return pairs.equals(otherRelation.pairs);
    }

    /**
     * The pair (α, α) is in R for every element α in U
     * idU ⊆ R
     * @return idU ⊆ self
     */
    public boolean isReflexive() {
        try {
            return universalSet.identityRelation().isSubsetOf(this);
        } catch (UnmatchedUniversalSetsException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * (α, β) ∈ R implies (β,α) ∈ R
     * R⁻¹ ⊆ R
     * @return self⁻¹ ⊆ self
     */
    public boolean isSymmetric() {
        try {
            return converse().isSubsetOf(this);
        } catch (UnmatchedUniversalSetsException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * (α, γ) ∈ R and (γ,β) ∈ R implies (α, β) ∈ R
     * R | R ⊆ R
     * @return self | self ⊆ self
     */
    public boolean isTransitive() {
        try {
            return composition(this).isSubsetOf(this);
        } catch (UnmatchedUniversalSetsException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * idU ∪ R⁻¹ ∪ (R | R) ⊆ R
     * idU ∪ (R | R⁻¹) = R
     * @return whether it is an equivalence relation
     */
    public boolean isEquivalence() {
        return isReflexive() && isSymmetric() && isTransitive();
    }

    /**
     * (α, β) ∈ R and (β,α) ∈ R implies α = β
     * R ∩ R⁻¹ ⊆ idU
     * @return self ∩ self⁻¹ ⊆ idU
     */
    public boolean isAntiSymmetric() {
        try {
            return intersection(converse()).isSubsetOf(universalSet.identityRelation());
        } catch (UnmatchedUniversalSetsException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * [(idU ∪ (R | R)) ∼ R] ∪ [(R ∩ R⁻¹) ∼ idU ] = ∅
     * (idU ∪ (R | R)) ∼ R = ∅ and (R ∩ R⁻¹) ∼ idU = ∅
     * idU ∪ (R | R) ⊆ R and R ∩ R⁻¹ ⊆ idU
     * @return whether it is a partial order
     */
    public boolean isPartialOrder() {
        return isReflexive() && isAntiSymmetric() && isTransitive();
    }

    /**
     * (α, β) ∈ R and (α, γ) ∈ R implies β = γ
     * R⁻¹ | R ⊆ idU
     * @return self⁻¹ | self ⊆ idU
     */
    public boolean isFunction() {
        try {
            return converse().composition(this).isSubsetOf(universalSet.identityRelation());
        } catch (UnmatchedUniversalSetsException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * (α, γ) ∈ R and (β, γ) ∈ R implies α = β
     * R | R⁻¹ ⊆ idU
     * @return self | self⁻¹ ⊆ idU
     */
    public boolean isOneToOne() {
        try {
            return composition(converse()).isSubsetOf(universalSet.identityRelation());
        } catch (UnmatchedUniversalSetsException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * An element α belongs to the domain of an arbitrary relation R on a set U iff the
     * pair (α, α) belongs to the relation R | (U × U)
     * (R | (U × U)) ∩ idU
     * @return left-hand part of all element pairs
     */
    public RSet<T> getDomain() {
        // todo the given formula gives a relation with all identity pairs relating to the
        //  domain so decide whether to return set (just split up the set and remove
        //  duplicates) or relation (in which case use the given formula)
        return new RSet<>(pairs.getElements().stream().map(Pair::getFirst).collect(Collectors.toCollection(HashSet::new)));

    }

    /**
     * An element α belongs to the range of an arbitrary relation R on a set U iff the
     * pair (α, α) belongs to the relation (U × U) | R
     * ((U × U) | R) ∩ idU
     * @return right-hand part of all element pairs
     */
    public RSet<T> getRange() {
        // todo see domain todo
        return new RSet<>(pairs.getElements().stream().map(Pair::getSecond).collect(Collectors.toCollection(HashSet::new)));
    }

    /**
     * R and S are functions with the property that, for any two elements α and β in U, there
     * is always an element γ in U such that (γ,α) ∈ R and (γ,β) ∈ S
     * ∼[(R⁻¹ | R) ∪ (S⁻¹ | S)] ∪ idU ] ∩ (R⁻¹ | S) = U × U
     * ∼[(R⁻¹ | R) ∪ (S⁻¹ | S)] ∪ idU = U × U and R⁻¹ | S = U × U
     * @param otherRelation S
     * @return whether self and S are conjugated quasi-projections on U
     */
    public boolean conjugatedQuasiProjection(Relation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        return isFunction() && otherRelation.isFunction() && converse().composition(otherRelation).pairs.equals(universalSet.universalRelation().pairs);
    }

}
