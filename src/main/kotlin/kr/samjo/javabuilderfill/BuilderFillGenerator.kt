package kr.samjo.javabuilderfill

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.concurrent.atomic.AtomicInteger


/**
 * BuilderFillGenerator
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/17/23
 */
class BuilderFillGenerator : AnAction() {

    override fun update(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        var visible = false
        if (psiFile != null && editor != null) {
            visible = isClassElement(psiFile, editor)
        }
        e.presentation.setEnabledAndVisible(visible)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (psiFile == null || editor == null) return

        val currentCaretElement = psiFile.findElementAt(editor.caretModel.offset)
        val menuText = e.presentation.text

        val psiClass = PsiTreeUtil.getParentOfType(
            currentCaretElement, PsiClass::class.java
        )

        Toolkit.getDefaultToolkit().systemClipboard
            .setContents(StringSelection("BUILDER TEST GENERATED"), null)
    }

    private fun isClassElement(psiFile: PsiFile, editor: Editor): Boolean {
        val currentCaretElement = psiFile.findElementAt(editor.caretModel.offset)
        val psiClass = PsiTreeUtil.getParentOfType(
            currentCaretElement!!, PsiClass::class.java
        )
        return psiClass != null
    }
}