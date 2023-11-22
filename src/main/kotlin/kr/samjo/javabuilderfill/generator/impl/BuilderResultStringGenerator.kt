package kr.samjo.javabuilderfill.generator.impl

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiType
import kr.samjo.javabuilderfill.BuilderFillOptions
import kr.samjo.javabuilderfill.generator.ResultStringGenerator


class BuilderResultStringGenerator : ResultStringGenerator() {
    companion object{
        val SUPPORT_OPTION = BuilderFillOptions.BUILDER
    }
    override fun support(builderFillOptions: BuilderFillOptions): Boolean {
        return SUPPORT_OPTION == builderFillOptions
    }

    override fun generateResult(initValue: (psiType: PsiType) -> String, psiClass: PsiClass): StringBuilder {
        val resultMapBuilder = StringBuilder(psiClass.name + ".builder()\n")

        psiClass.fields.forEach { field: PsiField ->
                resultMapBuilder.append(
                    "." + field.name + "(" + initValue(field.type) + ")\n"
                )
            }

        resultMapBuilder.append(".build();")
        return resultMapBuilder
    }
}