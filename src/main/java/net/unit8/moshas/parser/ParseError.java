package net.unit8.moshas.parser;

/**
 *
 * @author kawasima
 */
public class ParseError {
    private int pos;
    private String errorMsg;

    ParseError(int pos, String errorMsg) {
        this.pos = pos;
        this.errorMsg = errorMsg;
    }

    ParseError(int pos, String errorFormat, Object... args) {
        this.errorMsg = String.format(errorFormat, args);
        this.pos = pos;
    }

    /**
     * Retrieve the error message.
     * @return the error message.
     */
    public String getErrorMessage() {
        return errorMsg;
    }

    /**
     * Retrieves the offset of the error.
     * @return error offset within input
     */
    public int getPosition() {
        return pos;
    }

    @Override
    public String toString() {
        return pos + ": " + errorMsg;
    }    
}
