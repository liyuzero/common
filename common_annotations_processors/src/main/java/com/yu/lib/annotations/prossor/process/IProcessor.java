package com.yu.lib.annotations.prossor.process;

import com.squareup.javapoet.MethodSpec;
import com.yu.lib.annotations.prossor.ProcessClassData;

import java.util.HashMap;
import java.util.Set;

import javax.lang.model.element.Element;

public interface IProcessor {
    HashMap<ProcessClassData, MethodSpec> process(Set<? extends Element> elements);
}
