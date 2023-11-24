package kr.craft.javaboilercraft.processor.impl

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import kr.craft.javaboilercraft.processor.CompletionProcessor

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
        private const val PACKAGE_NAME = "lombok"
    }

    override fun supportOption() = SUPPORT_OPTION
    override fun applicable(targetClass: PsiClass) = (targetClass.getAnnotation("${PACKAGE_NAME}.${SUPPORT_OPTION}") != null)

    override fun completionString(targetClass: PsiClass, targetElement: PsiElement): String {
        val indent = " ".repeat(getIndent(targetElement))
        return targetClass.fields.joinToString(
            prefix = "${targetClass.name}.builder()\n",
            postfix = "\n${indent} \t.build();\n",
            separator = "\n"
        ) { field -> "${indent}\t.${field.name}(${field.name})" }
    }

    private fun getIndent(psiElement: PsiElement): Int {
        val psiFile = psiElement.containingFile ?: return 0
        val documentManager = FileDocumentManager.getInstance()
        val document: Document = documentManager.getDocument(psiFile.virtualFile) ?: return 0
        val lineNumber = document.getLineNumber(psiElement.textOffset)
        val lineStartOffset = document.getLineStartOffset(lineNumber)
        val lineEndOffset = document.getLineEndOffset(lineNumber)
        val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))
        return "^\\s*".toRegex().find(lineText)?.value?.length ?: 0
    }
}