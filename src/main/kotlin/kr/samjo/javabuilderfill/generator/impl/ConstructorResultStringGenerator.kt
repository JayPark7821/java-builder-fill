package kr.samjo.javabuilderfill.generator.impl

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiType
import kr.samjo.javabuilderfill.BuilderFillOptions
import kr.samjo.javabuilderfill.generator.ResultStringGenerator

class ConstructorResultStringGenerator : ResultStringGenerator() {

    companion object{
        val SUPPORT_OPTION = BuilderFillOptions.CONSTRUCTOR
    }
    override fun support(builderFillOptions: BuilderFillOptions): Boolean {
        return SUPPORT_OPTION == builderFillOptions
    }

    override fun generateResult(initValue: (psiType: PsiType) -> String, psiClass: PsiClass): StringBuilder {
        val resultMapBuilder = StringBuilder("new " + psiClass.name + "(")

        psiClass.fields.forEach { field: PsiField ->
                resultMapBuilder.append(
                     initValue(field.type) + ", "
                )
            }

        resultMapBuilder.setLength(resultMapBuilder.length - 2)
        resultMapBuilder.append(");")
        return resultMapBuilder
    }

}