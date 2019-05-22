package main.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BeanUnits {

    /**
     * Scans object "from" for all getters. If object "to"
     * contains correspondent setter, it will invoke it
     * to set property value for "to" which equals to the property
     * of "from".
     * <p/>
     * The type in setter should be compatible to the value returned
     * by getter (if not, no invocation performed).
     * Compatible means that parameter type in setter should
     * be the same or be superclass of the return type of the getter.
     * <p/>
     * The method takes care only about public methods.
     *
     * @param to Object which properties will be set.
     * @param from Object which properties will be used to get values.
     */

    public static void assign(Object to, Object from) {
        ArrayList<Method> setters = getSetters(to.getClass());
        ArrayList<Method> getters = getGetters(from.getClass());

        for (Method getter: getters) {
            for (Method setter: setters) {
                Class getType = getter.getReturnType();
                Class setType = setter.getParameterTypes()[0];

                if (getType.equals(setType) || getType.getSuperclass().equals(setType)) {
                    try {
                        setter.invoke(to, getter.invoke(from));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            
        }

    }


    private static ArrayList<Method> getSetters(Class objClass) {
        ArrayList<Method> setters = new ArrayList<>();

        for (Method method: objClass.getMethods()) {
            if (method.getName().startsWith("set") && method.getParameterCount() == 1
                    && void.class.equals(method.getReturnType())) {
                setters.add(method);
            }
        }

        return setters;
    }

    private static ArrayList<Method> getGetters(Class objClass) {
        ArrayList<Method> getters = new ArrayList<>();

        for (Method method: objClass.getMethods()) {
            if (method.getName().startsWith("get") && method.getParameterCount() == 0
                    && !void.class.equals(method.getReturnType())) {
                getters.add(method);
            }
        }

        return getters;
    }
}
