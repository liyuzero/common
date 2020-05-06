package com.yu.lib.annotations.prossor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.yu.lib.annotations.prossor.process.BindViewProcessor;
import com.yu.lib.annotations.prossor.process.IProcessor;
import com.yu.lib.annotations.aspect.MainThread;
import com.yu.lib.annotations.aspect.RunThread;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    private HashMap<Class<? extends Annotation>, IProcessor> mAnnotationProcessorMap = new HashMap<>();
    //生成文件的工具类
    private Filer mFiler;
    private Messager mMessage;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessage = processingEnv.getMessager();
        mMessage.printMessage(Diagnostic.Kind.NOTE, "===================================");
        mMessage.printMessage(Diagnostic.Kind.NOTE, "初始化");
        //生成文件的工具类
        mFiler = processingEnv.getFiler();
        //元素相关
        Elements elementUtils = processingEnv.getElementUtils();
        mAnnotationProcessorMap.put(BindView.class, new BindViewProcessor(elementUtils));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mMessage.printMessage(Diagnostic.Kind.NOTE, "处理一下");

        HashMap<ProcessClassData, HashSet<MethodSpec>> allData = findProcessData(annotations, roundEnv);

        for (Map.Entry<ProcessClassData, HashSet<MethodSpec>> entry: allData.entrySet()) {
            ProcessClassData processClassData = entry.getKey();
            HashSet<MethodSpec> methodSpecHashSet = entry.getValue();

            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(processClassData.getClassName() + "_Inject")
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ParameterizedTypeName.get(processClassData.getInterfaceClassName(), ClassName.get(processClassData.getTypeElement())));

            for (MethodSpec methodSpec: methodSpecHashSet) {
                typeBuilder.addMethod(methodSpec);
            }

            JavaFile javaFile = JavaFile.builder(processClassData.getPackageName(), typeBuilder.build())
                    .addFileComment("auto generate class, can not modify")
                    .build();

            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                mMessage.printMessage(Diagnostic.Kind.NOTE, "错误信息：" + e.getMessage());
                e.printStackTrace();
            }
        }
        return true;
    }

    private HashMap<ProcessClassData, HashSet<MethodSpec>> findProcessData(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        HashMap<ProcessClassData, HashSet<MethodSpec>> allMethodData = new HashMap<>();

        //收集注解信息
        for (Map.Entry<Class<? extends Annotation>, IProcessor> entry: mAnnotationProcessorMap.entrySet()) {
            //1、获取要处理的注解的元素的集合
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(entry.getKey());

            //过滤无用信息
            if(elements != null && elements.size() > 0) {
                //处理每个注解到的元素信息，生成数据集合
                HashMap<ProcessClassData, MethodSpec> curAnnotationData = entry.getValue().process(elements);
                //收集每个注解标注的某个类内的所有信息
                for (Map.Entry<ProcessClassData, MethodSpec> curAnnotationEntry: curAnnotationData.entrySet()) {
                    HashSet<MethodSpec> existData = allMethodData.get(curAnnotationEntry.getKey());
                    if(existData == null) {
                        existData = new HashSet<>();
                        allMethodData.put(curAnnotationEntry.getKey(), existData);
                    }
                    existData.add(curAnnotationEntry.getValue());
                }
            }
        }

        return allMethodData;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        for (Class<? extends Annotation> clazz: getSupportAnnotations()) {
            set.add(clazz.getCanonicalName());
        }
        return set;
    }

    private Set<Class<? extends Annotation>> getSupportAnnotations() {
        Set<Class<? extends Annotation>> annotations = new HashSet<>();
        annotations.add(BindView.class);
        annotations.add(MainThread.class);
        annotations.add(RunThread.class);
        return annotations;
    }
}
