package kache.model;

import java.util.Arrays;

public class PersistAOFEntry<K,V> {

    private Object[] params;
    private String methodName;
    public static PersistAOFEntry newInstance() {
        return new PersistAOFEntry();
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "PersistAofEntry{" +
                "params=" + Arrays.toString(params) +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
