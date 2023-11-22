package kr.samjo.javabuilderfill

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import kr.samjo.javabuilderfill.generator.ResultStringGenerator
import kr.samjo.javabuilderfill.generator.impl.BuilderResultStringGenerator
import kr.samjo.javabuilderfill.generator.impl.ConstructorResultStringGenerator


/**
 * BuilderFillCompletion
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/22/23
 */
class BuilderFillCompletion : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val document = parameters.editor.document
        val offset = parameters.offset

        if (offset > 0 && document.charsSequence[offset - 1] == '.') {
            val psiElement = parameters.originalFile.findElementAt(offset - 2)
            val findClass = psiElement?.let {
                JavaPsiFacade.getInstance(parameters.originalFile.project)
                    .findClass(it.text, GlobalSearchScope.allScope(parameters.originalFile.project))
            }

            findClass?.let {
                if( it.getAnnotation("Builder") != null ){
                    val builderElement = LookupElementBuilder.create("builder with default values")
                        .withInsertHandler { context, _ ->
                            val startOffset = context.startOffset - (psiElement.textLength + 1)
                            val tailOffset = context.tailOffset
                            WriteCommandAction.runWriteCommandAction(context.project) {
                                context.document.replaceString(
                                    startOffset,
                                    tailOffset,
                                    BuilderResultStringGenerator().process(it)
                                )
                            }
                        }
                    result.addElement(builderElement)
                }



                val constructorElement = LookupElementBuilder.create("constructor with default values")
                    .withInsertHandler { context, _ ->
                        val startOffset = context.startOffset - (psiElement.textLength + 1)
                        val tailOffset = context.tailOffset
                        WriteCommandAction.runWriteCommandAction(context.project) {
                            context.document.replaceString(
                                startOffset,
                                tailOffset,
                                ConstructorResultStringGenerator().process(it)
                            )
                        }
                    }
                result.addElement(constructorElement)

            }
//                if (findClass != null) {
//                    println("findClass: ${findClass.name}")
//                    findClass.annotations.forEach {
//                        println(it)
//                    }
//
//                    findClass.constructors.forEach {
//                        it.annotations.forEach {
//                            println(it)
//                        }
//                    }

//                    val lookupElement = LookupElementBuilder.create("constructor without default values")
//                        .withInsertHandler { context, _ ->
//                            val startOffset = context.startOffset - (psiElement.textLength + 1)
//                            val tailOffset = context.tailOffset
//                            WriteCommandAction.runWriteCommandAction(context.project) {
//                                context.document.replaceString(startOffset, tailOffset, "new ${findClass.name}()")
//                            }
//                        }
//
//                    result.addElement(lookupElement.appendTailText("                test", true))
//                }
        }

    }
//
//        val findClass = JavaPsiFacade.getInstance(parameters.originalFile.project)
//        PsiTreeUtil.getParentOfType(
//            parameters.originalFile.originalElement.
//            PsiClass::class.java
//        )
//        findClass.findClass("Test", GlobalSearchScope.allScope(parameters.originalFile.project))?.let {
//            result.addElement(LookupElementBuilder.create(it))
//        }
////
////        val instance = JavaPsiFacade.getInstance(parameters.originalFile.project)
//
////        val project = parameters.originalFile.project
////        val findClass = JavaPsiFacade.getInstance(project)
////            .findClass("Test", GlobalSearchScope.allScope(project))
//        val position = parameters.position
//        val psiClass = PsiTreeUtil.getPrevSiblingOfType(position, PsiClass::class.java)
//        if (psiClass != null) {
//            val document = position.containingFile.viewProvider.document
//            val offset = parameters.offset
//
//            if (document != null && offset > 0) {
//                val charBeforeCursor = document.charsSequence[offset - 1]
//                if (charBeforeCursor == ',') {
//                    result.addElement(LookupElementBuilder.create(psiClass))
//                }
//            }
//        }
//    }

//        val prefixMatcher = result.prefixMatcher
//        val withPrefixMatcher = result.withPrefixMatcher(prefixMatcher)
//
////        val psiClass = e.getData(CommonDataKeys.PSI_ELEMENT) as PsiClass
//        val offset = parameters.editor.caretModel.offset
//        val originalFile = parameters.originalFile.findElementAt(offset)
//        val originalFile1 = parameters.originalFile
//
//
//
//        val tttttt = PsiTreeUtil.getParentOfType(originalFile, PsiClass::class.java)
//
//        PsiTreeUtil.getParentOfType(originalFile, PsiClass::class.java)?.let {
//            val className = it.name
//            if (className != null) {
//                result.addElement(LookupElementBuilder.create(className))
//            }
//        }
//
//
//        val position = parameters.position
//        val psiClass = position.parent as? PsiClass
//        val className = psiClass?.name
//        if (className != null) {
//            result.addElement(LookupElementBuilder.create(className))
//        }

}