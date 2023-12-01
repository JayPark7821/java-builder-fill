package kr.craft.javaboilercraft.processor

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement

/**
 * CompletionProcessor
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/23/23
 */
abstract class CompletionProcessor {
    abstract fun supportOption(): String
    abstract fun applicable(targetElement: PsiElement, targetClass: PsiClass): Boolean
    abstract fun completionString(targetClass: PsiClass, targetElement: PsiElement, parameters: CompletionParameters): String

    fun process(
        targetClass: PsiClass,
        targetElement: PsiElement,
        result: CompletionResultSet,
        parameters: CompletionParameters
    ) {
        val completionString = completionString(targetClass, targetElement, parameters)
        val element = LookupElementBuilder.create(supportOption() + " Completion")
            .withInsertHandler { context, _ ->
                val startOffset = context.startOffset - (targetElement.textLength + 1)
                val tailOffset = context.tailOffset
                WriteCommandAction.runWriteCommandAction(context.project) {
                    context.document.replaceString(
                        startOffset,
                        tailOffset,
                        completionString
                    )
                }
            }
            .withTypeText(completionString)

        result.addElement(element)
    }
}

