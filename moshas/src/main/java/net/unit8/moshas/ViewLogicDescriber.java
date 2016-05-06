package net.unit8.moshas;

import java.io.Serializable;

/**
 *
 * @author kawasima
 */
@FunctionalInterface
public interface ViewLogicDescriber extends Serializable {
    void describe(ViewLogicDescriptor t);
}
