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
class BuilderCompletionProcessor : CompletionProcessor() {

    companion object {
        private const val SUPPORT_OPTION = "Builder"
    }

    override fun supportOption() = SUPPORT_OPTION
    override fun applicable(targetClass: PsiClass) = (targetClass.getAnnotation(SUPPORT_OPTION) != null)

    override fun completionString(targetClass: PsiClass): String {
        return targetClass.fields.joinToString(
            prefix = "${targetClass.name}.builder()\n",
            postfix = "\n.build();\n",
            separator = "\n"
        ) { field -> ".${field.name}(${field.name})" }
    }
}