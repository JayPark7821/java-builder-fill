package kr.samjo.javabuilderfill.generator

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiType
import kr.samjo.javabuilderfill.BuilderFillOptions
import java.util.*

/**
 * ResultGenerator
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/21/23
 */
abstract class ResultStringGenerator {

    abstract fun support(builderFillOptions: BuilderFillOptions): Boolean
    fun process(psiClass: PsiClass): String{
        return getStringBuilderResult(generateResult(initValue , psiClass))
    }
    abstract fun generateResult(initValue: (psiType: PsiType)-> String, psiClass: PsiClass): StringBuilder

    private fun getStringBuilderResult(stringBuilder: StringBuilder): String {
        val result = stringBuilder.toString()
        stringBuilder.setLength(0)
        return result
    }

    private val initValue = { psiType: PsiType ->
        val type = psiType.deepComponentType.presentableText
        val psiTypes = psiType.superTypes
        when{
            "Integer" == type || "int" == type ->  "1"
            "Long" == type || "long" == type-> "1L"
            "Short" == type || "short" == type-> "1"
            "Float" == type || "float" == type-> "1.0f"
            "Double" == type || "double" == type-> "1.0"
            "Character" == type || "char" == type-> "'a'"
            "Boolean" == type || "boolean" == type-> "true"
            "String" == type-> "\"\""
            "BigDecimal" == type-> "BigDecimal.ZERO"
            type.contains("List")-> "List.of()"
            type.contains("Map")-> "Map.of()"
            type.contains("Set")-> "Set.of()"
            type.contains("LocalDateTime")-> "LocalDateTime.now()"
            else -> {
                val anEnum = Arrays.stream(psiTypes)
                    .filter { psiType: PsiType ->
                        psiType.presentableText.startsWith("Enum")
                    }
                    .findFirst()
                    .isPresent
                if (anEnum) {
                    type
                } else "null"
            }
        }
    }
}
