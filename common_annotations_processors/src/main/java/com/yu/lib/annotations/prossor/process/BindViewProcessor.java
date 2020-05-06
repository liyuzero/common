package com.yu.lib.annotations.prossor.process;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.yu.lib.annotations.prossor.ProcessClassData;
import com.yu.lib.annotations.prossor.BindView;
import com.yu.lib.annotations.prossor.api.IBindView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public class BindViewProcessor implements IProcessor {
    private Elements elementUtils;

    public BindViewProcessor(Elements elementUtils) {
        this.elementUtils = elementUtils;
    }

    @Override
    public HashMap<ProcessClassData, MethodSpec> process(Set<? extends Element> elements) {
        HashMap<ProcessClassData, MethodSpec> result = new HashMap<>();

        HashMap<ProcessClassData, List<VariableElement>> variableMap = new HashMap<>();
        for (Element element : elements) {
            VariableElement variableElement = (VariableElement) element;
            //获取父元素信息
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            String fullClassName = typeElement.getQualifiedName().toString();

            ProcessClassData processClassData = new ProcessClassData(fullClassName, typeElement.getSimpleName().toString(), typeElement,
                    ClassName.get(IBindView.class), elementUtils.getPackageOf(element).getQualifiedName().toString());
            List<VariableElement> list = variableMap.get(processClassData);
            if (list == null) {
                list = new ArrayList<>();
                variableMap.put(processClassData, list);
            }

            list.add(variableElement);
        }

        for (Map.Entry<ProcessClassData, List<VariableElement>> entry : variableMap.entrySet()) {
            ProcessClassData processClassData = entry.getKey();
            List<VariableElement> list = entry.getValue();

            MethodSpec.Builder builder = MethodSpec.methodBuilder("_inject_findAllBindView")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(ClassName.get(processClassData.getTypeElement()), "target")
                    .addParameter(Object.class, "source");

            for (VariableElement variableElement : list) {
                //获取被注解元素的包名
                String packageName = elementUtils.getPackageOf(variableElement).getQualifiedName().toString();

                //获取属性名字
                String name = variableElement.getSimpleName().toString();
                //获取注解值
                int viewId = variableElement.getAnnotation(BindView.class).value();

                if (viewId == 0) {
                    builder.addStatement("int id_$L = target.getResources().getIdentifier($S, $S, $S);", name, name, "id", packageName);
                    builder.addStatement(" if (source instanceof android.app.Activity){target.$L = ((android.app.Activity) source).findViewById(id_$L);}" +
                            "else{target.$L = ((android.view.View)source).findViewById(id_$L);}", name, name, name, name);
                } else {
                    builder.addStatement(" if (source instanceof android.app.Activity){target.$L = ((android.app.Activity) source).findViewById($L);}" +
                            "else{target.$L = ((android.view.View)source).findViewById($L);}", name, viewId, name, viewId);
                }
            }

            result.put(processClassData, builder.build());
        }

        return result;
    }
}
