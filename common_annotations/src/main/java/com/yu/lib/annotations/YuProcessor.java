package com.yu.lib.annotations;

import com.yu.lib.annotations.prossor.api.IBindView;

public class YuProcessor {

    public static void bind(Object object) {
        String injectClassName = object.getClass().getName() + "_Inject";
        try {
            Object instance = Class.forName(injectClassName).newInstance();
            if(instance instanceof IBindView) {
                IBindView<Object> iBindView = (IBindView)instance;
                iBindView._inject_findAllBindView(object, object);
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
