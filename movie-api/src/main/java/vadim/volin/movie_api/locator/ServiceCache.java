package vadim.volin.movie_api.locator;

import java.util.ArrayList;
import java.util.List;

public class ServiceCache {

    private List<Object> serviceList = new ArrayList<>();

    public Object getService(Class className) {
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).getClass() == className) {
                return serviceList.get(i);
            }
        }
        return null;
    }

    public void addService(Object service) {
        serviceList.add(service);
    }

}
