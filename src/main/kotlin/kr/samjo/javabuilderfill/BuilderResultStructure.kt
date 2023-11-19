package kr.samjo.javabuilderfill

import com.intellij.psi.PsiField

/**
 * BuilderResultStructure
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/17/23
 */
data class BuilderResultStructure(
    val className: String,
    val propertyName: String,
    val propertyList: List<PsiField>,
)
