package org.oci.task.error;

/**
 * @brief List of error codes that OCI Task Service may return.
 * @author rajeshkurup@live.com
 */
public enum OciErrorCode {

    INTERNAL_ERROR(1001),
    DATABASE_ERROR(1002),
    INVALID_ARGUMENT(2001),
    NO_DATA_FOUND(4001);

    private final int errorCode;

    private OciErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return String.valueOf(errorCode);
    }

}
