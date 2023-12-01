package kr.craft.javaboilercraft.processor.util

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

/**
 * EditorUtils
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/1/23
 */
object  EditorUtils {

    fun getIndent(psiElement: PsiElement): Int {
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