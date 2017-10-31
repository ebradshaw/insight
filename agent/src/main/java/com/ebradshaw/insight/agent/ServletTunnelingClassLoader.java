package com.ebradshaw.insight.agent;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.WeakHashMap;

/**
 * Since the runtime is loaded at the top of the classloader heirarchy, but depends on the Servlet API, we use
 * this classloader to tunnel up the classloader chain via the calling thread's ContextClassLoader when
 * javax.servlet.* classes are required.
 */
public class ServletTunnelingClassLoader extends URLClassLoader {

    private final WeakHashMap<ClassLoader, ClassLoader> loaderMap = new WeakHashMap<>();

    ServletTunnelingClassLoader(URL runtimeUrl, ClassLoader parent) {
        super(new URL[]{ runtimeUrl }, parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith("javax.servlet")) {
            return getCurrentServletApiClassLoader().loadClass(name);
        }

        return super.loadClass(name);
    }

    private ClassLoader getCurrentServletApiClassLoader() throws ClassNotFoundException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if(!loaderMap.containsKey(contextClassLoader)){
            loaderMap.put(contextClassLoader, contextClassLoader.loadClass("javax.servlet.Servlet").getClassLoader());
        }
        return loaderMap.get(contextClassLoader);
    }
}
