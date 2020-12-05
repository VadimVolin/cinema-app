package vadim.volin.movie_api.locator;

public class ServiceLocator {

    private static ServiceCache serviceCache = new ServiceCache();

    private static LookupService lookupService = new LookupService();

    public static LookupService getLookupService() {
        return lookupService;
    }

    public static Object getService(Class className) {
        Object service = serviceCache.getService(className);

        if (service != null) {
            return service;
        } else {
            service = lookupService.lookupService(className);
            serviceCache.addService(service);
        }
        return service;
    }
}
