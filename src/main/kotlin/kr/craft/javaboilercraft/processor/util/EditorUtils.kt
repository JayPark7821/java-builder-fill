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

    fun getIndent(psiElement: PsiElement): IndentInfo {
        val psiFile = psiElement.containingFile ?: return IndentInfo.default()
        val documentManager = FileDocumentManager.getInstance()
        val document: Document = documentManager.getDocument(psiFile.virtualFile) ?: return IndentInfo.default()
        val lineNumber = document.getLineNumber(psiElement.textOffset)
        val lineStartOffset = document.getLineStartOffset(lineNumber)
        val lineEndOffset = document.getLineEndOffset(lineNumber)
        val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))

        return IndentInfo(
            "^\\s*".toRegex().find(lineText)?.value?.length ?: 0,
            "^\\t*".toRegex().find(lineText)?.value?.length ?: 0,
        )
    }
}

data class IndentInfo
(
    val spaceCount: Int,
    val tabCount: Int
){
    companion object {
        fun default() = IndentInfo(0, 0)
    }

    fun getIndentString() = " ".repeat(spaceCount) + "\t".repeat(tabCount)

}

