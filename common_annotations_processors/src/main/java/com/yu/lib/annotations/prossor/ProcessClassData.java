package com.yu.lib.annotations.prossor;

import com.squareup.javapoet.ClassName;

import java.util.Objects;

import javax.lang.model.element.TypeElement;

public class ProcessClassData {
    private String fullClassName;
    private String className;
    private TypeElement typeElement;
    private ClassName interfaceClassName;
    private String packageName;

    public ProcessClassData(String fullClassName, String className, TypeElement typeElement,
                            ClassName interfaceClassName, String packageName) {
        this.className = className;
        this.fullClassName = fullClassName;
        this.typeElement = typeElement;
        this.interfaceClassName = interfaceClassName;
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public ClassName getInterfaceClassName() {
        return interfaceClassName;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessClassData that = (ProcessClassData) o;
        return Objects.equals(fullClassName, that.fullClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullClassName);
    }
}
