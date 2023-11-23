package kr.samjo.javabuilderfill.processor.impl

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import kr.samjo.javabuilderfill.processor.CompletionProcessor

/**
 * BuilderCompletionProcessor
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/23/23
 */
class ConstructorCompletionProcessor : CompletionProcessor() {

    companion object {
        private val SUPPORT_OPTION = "All Args Constructor"
    }

    override fun supportOption() = SUPPORT_OPTION
    override fun applicable(targetClass: PsiClass): Boolean {
        val constructors = targetClass.constructors
        constructors.forEach { constructor ->
            if (constructor.parameterList.parameters.size == targetClass.fields.size) {
                return true
            }
        }
        return false
    }

    override fun completionString(targetClass: PsiClass): String {
        return targetClass.fields.joinToString(
            prefix = "new ${targetClass.name}(",
            postfix = ");\n",
            separator = ","
        ) { field -> field.name }
    }
}