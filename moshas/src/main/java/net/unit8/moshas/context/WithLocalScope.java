package net.unit8.moshas.context;

import java.io.Serializable;

/**
 *
 * @author kawasima
 */
@FunctionalInterface
public interface WithLocalScope extends Serializable {
    void evaluate();
}
