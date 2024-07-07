package org.brutality.utils.exceptions;

/**
 * This is placeholder that you can put inside of methods you don't feel like making yet
 */
public class NotImplementedException extends RuntimeException {
    /**
     * This just sets the exception message to the name of the class the exception was thrown in
     */
    public NotImplementedException() {
        super(Thread.currentThread().getStackTrace()[2].getClassName());
    }
}
