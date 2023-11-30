package kr.craft.javaboilercraft.processor.impl

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import kr.craft.javaboilercraft.processor.CompletionProcessor

/**
 * RestDocsCompletionProcessor
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/30/23
 */
class RestDocsCompletionProcessor : CompletionProcessor() {

    companion object {
        private const val SUPPORT_OPTION = "RestDocs"
        private const val TEST_FILE_PATH = "/test"
        private const val SRC_TEST_FILE_PATH = "/src/test"
        private const val CONTROLLER = "Controller"
    }

    override fun supportOption() = SUPPORT_OPTION

    override fun applicable(targetElement: PsiElement, targetClass: PsiClass): Boolean {
        val filePath = targetElement.containingFile.virtualFile.canonicalPath?: return false
        return targetClass.annotations.any { annotation ->
            annotation.qualifiedName?.contains(CONTROLLER) == true && isCurrentCursorInTestScope(filePath)
        }
    }

    private fun isCurrentCursorInTestScope(filePath: String) =
        filePath.let { it.contains(TEST_FILE_PATH) || it.contains(SRC_TEST_FILE_PATH) }


    override fun completionString(targetClass: PsiClass, targetElement: PsiElement): String {
        return "etetetetet"
    }
}