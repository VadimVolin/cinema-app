package vadim.volin.response;

/**
 * Server response type
 *
 * @author Vadym Volin
 */
public enum ServiceStatus {

    /**
     * The type of server response with code 400
     */
    BAD_REQUEST_400,
    /**
     * The type of server response with code 404
     */
    NOT_FOUND_404,
    /**
     * The type of server response with code 500
     */
    SERVER_ERROR_500,
    /**
     * The type of server response with code 503
     */
    SERVICE_UNAVAILABLE_503

}
