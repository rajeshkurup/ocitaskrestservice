package org.oci.task.error;

/**
 * @brief Error message container for OCI Task Service.
 * @author rajeshkurup@live.com
 */
public class OciError {

    private OciErrorCode errorCode;

    private String errorMessage;

    public OciError() {
        // Empty
    }

    public OciError(OciErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public OciErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(OciErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
