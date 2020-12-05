package vadim.volin.mobile.error;

import androidx.annotation.NonNull;

import vadim.volin.mobile.R;

public enum ServiceStatus {

    NO_CONTENT_204(R.string.NO_CONTENT_204),
    BAD_REQUEST_400(R.string.BAD_REQUEST_400),
    NET_CONNECTION(R.string.NET_CONNECTION),
    NOT_FOUND_404(R.string.NOT_FOUND_404),
    SERVER_ERROR_500(R.string.SERVER_ERROR_500),
    BAD_GATEWAY_502(R.string.BAD_GATEWAY_502),
    SERVICE_UNEVAILABLE_503(R.string.SERVICE_UNEVAILABLE_503);

    private final int resourceId;

    ServiceStatus(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return this.resourceId;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
