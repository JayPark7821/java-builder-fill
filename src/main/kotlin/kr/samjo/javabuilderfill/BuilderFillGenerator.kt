package kr.samjo.javabuilderfill

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ModalTaskOwner.project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import kr.samjo.javabuilderfill.generator.ResultStringGenerator
import kr.samjo.javabuilderfill.generator.impl.BuilderResultStringGenerator
import kr.samjo.javabuilderfill.generator.impl.ConstructorResultStringGenerator


/**
 * BuilderFillGenerator
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/17/23
 */
class BuilderFillGenerator : AnAction() {

    private val resultStringGenerators: List<ResultStringGenerator> = listOf(
        BuilderResultStringGenerator(),
        ConstructorResultStringGenerator()
    )

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
        )?: throw IllegalArgumentException("Invalid PsiClass")

        val resultOption = BuilderFillOptions.findOption(menuText)

        val resultMapString: String = resultStringGenerators.find { it.support(resultOption) }
            ?.process(psiClass)
            ?: throw IllegalArgumentException("Invalid BuilderFillOptions")

//        Toolkit.getDefaultToolkit().systemClipboard
//            .setContents(StringSelection(resultMapString), null)

//        val psiClass = e.getData(CommonDataKeys.PSI_ELEMENT) as PsiClass
        val project = e.getRequiredData(CommonDataKeys.PROJECT);
        val document = editor.getDocument();

        WriteCommandAction.writeCommandAction(project)
            .run<RuntimeException> {
                document.insertString(editor.caretModel.offset, resultMapString)
            }
    }

    private fun isClassElement(psiFile: PsiFile, editor: Editor): Boolean {
        val currentCaretElement = psiFile.findElementAt(editor.caretModel.offset)
        val psiClass = PsiTreeUtil.getParentOfType(
            currentCaretElement!!, PsiClass::class.java
        )
        return psiClass != null
    }
}