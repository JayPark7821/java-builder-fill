package kr.craft.javaboilercraft.processor.impl

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.popup.PopupChooserBuilder
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.ui.components.JBList
import kr.craft.javaboilercraft.processor.CompletionProcessor
import kr.craft.javaboilercraft.processor.util.MethodPropertiesPsiConverter.convert
import kr.craft.javaboilercraft.processor.util.MockMvcTestBoilerplateGenerator.generateBoilerplate
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future

/**
 * MockMvcTestCompletionProcessor
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/30/23
 */
class MockMvcTestCompletionProcessor : CompletionProcessor() {

    companion object {
        private const val SUPPORT_OPTION = "MockMvc Test for RestDocs"
        private const val TEST_FILE_PATH = "/test"
        private const val SRC_TEST_FILE_PATH = "/src/test"
        private const val CONTROLLER = "Controller"
    }

    override fun supportOption() = SUPPORT_OPTION

    override fun applicable(targetElement: PsiElement, targetClass: PsiClass): Boolean {
        val filePath = targetElement.containingFile.virtualFile.canonicalPath ?: return false
        return targetClass.annotations.any { annotation ->
            annotation.qualifiedName?.contains(CONTROLLER) == true && isCurrentCursorInTestScope(filePath)
        }
    }

    private fun isCurrentCursorInTestScope(filePath: String) =
        filePath.let { it.contains(TEST_FILE_PATH) || it.contains(SRC_TEST_FILE_PATH) }


    override fun completionString(
        targetClass: PsiClass,
        targetElement: PsiElement,
        parameters: CompletionParameters,
    ): String {
        val methods: Array<PsiMethod> = targetClass.methods

        if (methods.isEmpty()) return ""

        val methodList = JBList(methods.map { it.name })

        val selectedMethods =
            showPopUpAndGetSelectedMethods(methodList, parameters)

        val filterMethods = methods.filter {method ->

            method.name in selectedMethods
        }
        if (filterMethods.isEmpty()) return ""
        val result = filterMethods.mapNotNull { method ->
            method.let {
                convert(targetClass, method)?.let {
                    generateBoilerplate(it, targetElement)
                }
            }
        }

        return result.joinToString("\n")
    }

    private fun showPopUpAndGetSelectedMethods(
        methodList: JBList<String>,
        parameters: CompletionParameters,
    ): List<String> {
        val latch = CountDownLatch(1)
        var selectedMethods: List<String> = emptyList()
        ApplicationManager.getApplication().invokeLater {
            PopupChooserBuilder(methodList)
                .setTitle("Select Methods to Generate MockMvc Test")
                .setItemChoosenCallback {
                    selectedMethods = methodList.selectedValue?.let { listOf(it) } ?: emptyList()
                    latch.countDown()
                }
                .createPopup()
                .showInBestPositionFor(parameters.editor)
        }
        latch.await() // This will block until the count reaches zero
        return selectedMethods
    }

}