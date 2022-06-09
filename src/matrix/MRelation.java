package matrix;

import traditional.Pair;
import traditional.Relation;

import java.util.*;

public class MRelation<T> {

    private final MList<T> universalSet;
    private final boolean[][] matrix;
    private int size;

    public MRelation(Relation<T> relation) {
        MList<T> universalSet = new MList<>(relation.getUniversalSet().getElements());
        this.universalSet = universalSet;
        this.size = universalSet.size();
        MList<Pair<T>> pairs = new MList<>(relation.getPairs().getElements());
        matrix = new boolean[universalSet.size()][universalSet.size()];
        pairs.forEach(pair -> matrix[universalSet.indexOf(pair.getFirst())][universalSet.indexOf(pair.getSecond())]  = true);
    }

    public MRelation(Collection<T> universalSet, boolean[][] matrix) {
        this.universalSet = new MList<>(universalSet);
        this.matrix = matrix;
    }

    public MRelation(T[] universalSet, boolean[][] matrix) {
        this.universalSet = new MList<>(Arrays.asList(universalSet));
        this.matrix = matrix;
    }

    public List<T> getUniversalSet() {
        return universalSet;
    }

    public boolean[][] getMatrix() {
        return matrix;
    }

    /**
     * T = R ∪ S if and only if MT = MR + MS
     * @param otherRelation S
     * @return self ∪ S
     */
    public MRelation<T> union(MRelation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        boolean[][] newMatrix = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                newMatrix[i][j] = matrix[i][j] || otherRelation.matrix[i][j];
            }
        }
        return new MRelation<>(universalSet, newMatrix);
    }

    /**
     * T = ∼R if and only if MT = −MR
     * @return ∼self
     */
    public MRelation<T> complement() {
        boolean[][] newMatrix = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                newMatrix[i][j] = !matrix[i][j];
            }
        }
        return new MRelation<>(universalSet, newMatrix);
    }

    /**
     * T = R ∩ S if and only if MT = MR · MS
     * @param otherRelation S
     * @return self ∩ S
     */
    public MRelation<T> intersection(MRelation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        boolean[][] newMatrix = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                newMatrix[i][j] = matrix[i][j] && otherRelation.matrix[i][j];
            }
        }
        return new MRelation<>(universalSet, newMatrix);
    }

    /**
     * @param otherRelation S
     * @return self ∼ S
     */
    public MRelation<T> difference(MRelation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        boolean[][] newMatrix = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                newMatrix[i][j] = matrix[i][j] && !otherRelation.matrix[i][j];
            }
        }
        return new MRelation<>(universalSet, newMatrix);
    }

    /**
     * @param otherRelation S
     * @return self Δ S
     */
    public MRelation<T> symmetricDifference(MRelation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        boolean[][] newMatrix = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                newMatrix[i][j] = (matrix[i][j] && !otherRelation.matrix[i][j]) || (!matrix[i][j] && otherRelation.matrix[i][j]);
            }
        }
        return new MRelation<>(universalSet, newMatrix);
    }

    /**
     * T = R | S if and only if MT = MR ⊙ MS
     * @param otherRelation S
     * @return self | S
     */
    public MRelation<T> composition(MRelation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        boolean[][] newMatrix = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    newMatrix[i][j] = matrix[i][j] || (matrix[i][k] && otherRelation.matrix[k][j]);
                }
            }
        }
        return new MRelation<>(universalSet, newMatrix);
    }

    /**
     * T = R † S if and only if MT = MR ⊕ MS
     * @param otherRelation S
     * @return self † S
     */
    public MRelation<T> sum(MRelation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        boolean[][] newMatrix = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    newMatrix[i][j] = matrix[i][j] && (matrix[i][k] || otherRelation.matrix[k][j]);
                }
            }
        }
        return new MRelation<>(universalSet, newMatrix);
    }

    /**
     * T = R⁻¹ if and only if MT = (MR)^T
     * @return self⁻¹
     */
    public MRelation<T> converse() {
        boolean[][] newMatrix = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                newMatrix[i][j] = matrix[j][i];
            }
        }
        return new MRelation<>(universalSet, newMatrix);
    }


    /**
     * R ⊆ S
     * @param otherRelation S
     * @return self ⊆ S
     */
    public boolean isSubsetOf(MRelation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] && !otherRelation.matrix[i][j]) return false;
            }
        }
        return true;
    }


    /**
     * R = S
     * @param otherRelation S
     * @return self = S
     */
    public boolean isEqual(MRelation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] != otherRelation.matrix[i][j]) return false;
            }
        }
        return true;
    }

    /**
     * The pair (α, α) is in R for every element α in U
     * @return whether it is reflexive
     */
    public boolean isReflexive() {
        for (int i = 0; i < size; i++) {
            if (!matrix[i][i]) return false;
        }
        return true;
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
     * @return whether it is a partial order
     */
    public boolean isPartialOrder() {
        return isReflexive() && isAntiSymmetric() && isTransitive();
    }

    /**
     * (α, β) ∈ R and (α, γ) ∈ R implies β = γ
     * @return whether it is a function
     */
    public boolean isFunction() {
        for (int i = 0; i < size; i++) {
            boolean found = false;
            for (int j = 0; j < size; j++) {
                if (matrix[i][j]) {
                    if (found) return false;
                    found = true;
                }
            }
        }
        return true;
    }

    /**
     * (α, γ) ∈ R and (β, γ) ∈ R implies α = β
     * @return whether it is one-to-one
     */
    public boolean isOneToOne() {
        for (int j = 0; j < size; j++) {
            boolean found = false;
            for (int i = 0; i < size; i++) {
                if (matrix[i][j]) {
                    if (found) return false;
                    found = true;
                }
            }
        }
        return true;
    }

    /**
     * An element α belongs to the domain of an arbitrary relation R on a set U iff the
     * pair (α, α) belongs to the relation R | (U × U)
     * @return left-hand part of all element pairs
     */
    public MList<T> getDomain() {
        // todo see other relation domain todo
        MList<T> domain = new MList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j]) domain.add(universalSet.get(i));
            }
        }
        return domain;
    }

    /**
     * An element α belongs to the range of an arbitrary relation R on a set U iff the
     * pair (α, α) belongs to the relation (U × U) | R
     * @return right-hand part of all element pairs
     */
    public MList<T> getRange() {
        // todo see domain todo
        MList<T> range = new MList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j]) range.add(universalSet.get(j));
            }
        }
        return range;
    }

    /**
     * R and S are functions with the property that, for any two elements α and β in U, there
     * is always an element γ in U such that (γ,α) ∈ R and (γ,β) ∈ S
     * ∼[(R⁻¹ | R) ∪ (S⁻¹ | S)] ∪ idU ] ∩ (R⁻¹ | S) = U × U
     * ∼[(R⁻¹ | R) ∪ (S⁻¹ | S)] ∪ idU = U × U and R⁻¹ | S = U × U
     * @param otherRelation S
     * @return whether self and S are conjugated quasi-projections on U
     */
    public boolean conjugatedQuasiProjection(MRelation<T> otherRelation) throws UnmatchedUniversalSetsException {
        if (!universalSet.equals(otherRelation.universalSet)) throw new UnmatchedUniversalSetsException();
        return isFunction() && otherRelation.isFunction() && converse().composition(otherRelation).isEqual(universalSet.universalRelation());
    }

}
