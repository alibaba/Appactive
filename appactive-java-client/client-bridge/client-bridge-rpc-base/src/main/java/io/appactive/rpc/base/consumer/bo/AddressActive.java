package io.appactive.rpc.base.consumer.bo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddressActive<T> {

    private String resourceType;

    private List<T> originalList;

    private Map<String, List<T>> unitServersMap;

    public AddressActive(String resourceType, Map<String, List<T>> unitServersMap,List<T> originalList) {
        this.resourceType = resourceType;
        this.unitServersMap = unitServersMap;
        this.originalList = originalList;
    }

    public String getResourceType() {
        return resourceType;
    }

    public Map<String, List<T>> getUnitServersMap() {
        return unitServersMap;
    }

    public List<T> getOriginalList() {
        return originalList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        AddressActive<?> that = (AddressActive<?>)o;
        return Objects.equals(resourceType, that.resourceType) && Objects.equals(originalList,
            that.originalList) && Objects.equals(unitServersMap, that.unitServersMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceType, originalList, unitServersMap);
    }

    @Override
    public String toString() {
        return "AddressActive{" +
            "resourceType='" + resourceType + '\'' +
            ", originalList=" + originalList +
            ", unitServersMap=" + unitServersMap +
            '}';
    }
}
