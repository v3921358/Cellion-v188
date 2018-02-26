package tools;

import java.io.Serializable;

/**
 *
 * @author Lloyd Korn
 */
public class Tuple<A, B, C> implements Serializable {

    private static final long serialVersionUID = 11791993413738569L;
    private final A _1;
    private final B _2;
    private final C _3;

    /**
     * Class constructor - pairs two objects together.
     *
     * @param _1 The first object.
     * @param _2 The second object.
     * @param _3 The third object.
     */
    public Tuple(A _1, B _2, C _3) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }

    /**
     * Gets the first value.
     *
     * @return The first value.
     */
    public A get_1() {
        return _1;
    }

    /**
     * Gets the second value.
     *
     * @return The second value.
     */
    public B get_2() {
        return _2;
    }

    /**
     * Gets the third value.
     *
     * @return The third value.
     */
    public C get_3() {
        return _3;
    }

    /**
     * Turns the pair into a string.
     *
     * @return Each value of the pair as a string joined by a colon.
     */
    @Override
    public String toString() {
        return _1.toString() + ":" + _2.toString() + ":" + _3.toString();
    }

    /**
     * Gets the hash code of this pair.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
        result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
        result = prime * result + ((_3 == null) ? 0 : _3.hashCode());

        return result;
    }
}
