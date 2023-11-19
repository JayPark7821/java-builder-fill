package kr.samjo.javabuilderfill

import com.intellij.psi.PsiClass


/**
 * ExtractBuilderElements
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/17/23
 */
data class ExtractBuilderElements(
    val psiClass: PsiClass,
    val resultMap: Map<String, BuilderResultStructure>
)