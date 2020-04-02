package com.docwei.compiler;


import com.docwei.annotation.LifePhaseEvent;
import com.docwei.annotation.LifePhasing;
import com.docwei.annotation.OnLifePhaseEvent;
import com.docwei.annotation.generated.GeneratedAdapter;
import com.google.auto.common.MoreElements;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;


@SupportedAnnotationTypes("com.docwei.annotation.OnLifePhaseEvent")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class LifePhaseProcess extends AbstractProcessor {
    Messager mMessager;
    Filer mFiler;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "start:--------");
        //可能注解在方法ExecutableElement 可能在类上 TypeElement 可能在成员变量VariableElement
        //这里只考虑注解在方法上的类型
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(OnLifePhaseEvent.class);
        Map<TypeElement, Map<LifePhaseEvent, List<ExecutableElement>>> typeElements = new HashMap<>();
        for (Element element : set) {
            if (element.getKind() == ElementKind.METHOD) {
                OnLifePhaseEvent annotation = element.getAnnotation(OnLifePhaseEvent.class);
                LifePhaseEvent event = annotation.value();

                //基于方法Element拿到当前的类TypeElement 把相同TypeElement的放到一起
                TypeElement typeElement = (TypeElement) element.getEnclosingElement();
                if (!typeElements.containsKey(typeElement)) {
                    //新类是首次添加进来的
                    Map<LifePhaseEvent, List<ExecutableElement>> methods = new HashMap<>();
                    List<ExecutableElement> executableElements = new ArrayList<>();
                    executableElements.add((ExecutableElement) element);
                    methods.put(event, executableElements);
                    mMessager.printMessage(Diagnostic.Kind.NOTE, event.toString());
                    typeElements.put(typeElement, methods);
                } else {
                    Map<LifePhaseEvent, List<ExecutableElement>> methods = typeElements.get(typeElement);
                    if (methods.containsKey(event)) {
                        List<ExecutableElement> list = methods.get(event);
                        list.add((ExecutableElement) element);
                    } else {
                        List<ExecutableElement> executableElements = new ArrayList<>();
                        executableElements.add((ExecutableElement) element);
                        methods.put(event, executableElements);
                        typeElements.put(typeElement, methods);
                    }
                }

            }
        }

        for (Map.Entry<TypeElement, Map<LifePhaseEvent, List<ExecutableElement>>> entry : typeElements.entrySet()) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, entry.getKey().toString());
            for (Map.Entry<LifePhaseEvent, List<ExecutableElement>> entry2 : entry.getValue().entrySet()) {
                mMessager.printMessage(Diagnostic.Kind.NOTE, "------------" + "LifePhaseEvent." + entry2.getKey().toString());
                for (ExecutableElement element : entry2.getValue()) {
                    mMessager.printMessage(Diagnostic.Kind.NOTE, "------------------------------------" + element.toString());
                }
            }
        }
        generateAdapterClass(typeElements);
        return true;
    }

    private void generateAdapterClass(Map<TypeElement, Map<LifePhaseEvent, List<ExecutableElement>>> typeElements) {
        for (Map.Entry<TypeElement, Map<LifePhaseEvent, List<ExecutableElement>>> entry : typeElements.entrySet()) {
            wirteAdapterClass(entry.getKey(), entry.getValue());
        }
    }


    private ParameterSpec eventParmeter = ParameterSpec.builder(ClassName.get(LifePhaseEvent.class), "event").build();

    private void wirteAdapterClass(TypeElement typeElement, Map<LifePhaseEvent, List<ExecutableElement>> methods) {
        //final MyPresenter2 mReceiver;
        FieldSpec receiverField = FieldSpec.builder(ClassName.get(typeElement), "mReceiver", Modifier.FINAL).build();
        //调用的方法
        MethodSpec.Builder dispatchMethodBuilder = MethodSpec.methodBuilder("callMethod")
                .returns(TypeName.VOID)
                .addParameter(eventParmeter)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);
        for (Map.Entry<LifePhaseEvent, List<ExecutableElement>> entry : methods.entrySet()) {
            dispatchMethodBuilder.beginControlFlow("if ($N == $T.$L)", eventParmeter, LifePhaseEvent.class, entry.getKey());
            for (ExecutableElement element : entry.getValue()) {
                dispatchMethodBuilder.addStatement("$N.$L()", receiverField, element.getSimpleName());
            }
            dispatchMethodBuilder.addStatement("return")
                    .endControlFlow();
        }


        //构造参数设计
        ParameterSpec receiverParam = ParameterSpec.builder(ClassName.get(typeElement), "receiver").build();
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addParameter(receiverParam)
                .addStatement("this.$N = $N", receiverField, receiverParam)
                .build();
        //构造类
        String adapterName = LifePhasing.getAdapterName(typeElement.getSimpleName().toString());
        TypeSpec adapterTypeSpecBuilder = TypeSpec.classBuilder(adapterName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(GeneratedAdapter.class)
                .addField(receiverField)
                .addMethod(constructor)
                .addMethod(dispatchMethodBuilder.build()).build();

        try {
            JavaFile.builder(MoreElements.getPackage(typeElement).getQualifiedName().toString(), adapterTypeSpecBuilder)
                    .build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
