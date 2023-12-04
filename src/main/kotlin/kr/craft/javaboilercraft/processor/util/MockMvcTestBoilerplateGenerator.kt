package kr.craft.javaboilercraft.processor.util

import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


/**
 * MockMvcTestBoilerplateGenerator
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/30/23
 */
object MockMvcTestBoilerplateGenerator {
    fun generateBoilerplate(methodProperty: MethodProperty): String {

        val mockMvcTestBoilerplate = StringBuilder()
        mockMvcTestBoilerplate.append("\t@Test\n")
        mockMvcTestBoilerplate.append("\tvoid ${methodProperty.name}() throws Exception {\n")
        mockMvcTestBoilerplate.append("\t\tmockMvc.perform(\n")
        mockMvcTestBoilerplate.append(getHttpMethodAndEndPoint(methodProperty))
        mockMvcTestBoilerplate.append("\t\t\t\t.contentType(MediaType.APPLICATION_JSON)\n")

        if (methodProperty.requestBody != null) {
            mockMvcTestBoilerplate.append(
                "\t\t\t\t.accept(MediaType.APPLICATION_JSON)\n" +
                        "\t\t\t\t.content(\n" +
                        "\t\t\t\t\tobjectMapper.writeValueAsString( requestBody )\n" +
                        "\t\t\t\t)\n"
            )
        }
        mockMvcTestBoilerplate.append("\t\t)\n")
        mockMvcTestBoilerplate.append("\t\t\t\t.andDo(print())\n")
        mockMvcTestBoilerplate.append("\t\t\t\t.andDo(document(\"\" ")
        if (methodProperty.pathVariables.isNotEmpty())
            mockMvcTestBoilerplate.append(
                getPathParametersDocs(methodProperty)
            )

        if (methodProperty.queryParams.isNotEmpty())
            mockMvcTestBoilerplate.append(
                getQueryParamsDocs(methodProperty)
            )

        if (methodProperty.requestBody != null) {
            mockMvcTestBoilerplate.append(
                getRequestFieldsDocs(methodProperty.requestBody)
            )
        }

        if (methodProperty.responseType != null) {
            mockMvcTestBoilerplate.append(
                getResponseFieldsDocs(methodProperty.responseType)
            )
        }


        mockMvcTestBoilerplate.append("\n\t\t\t\t));\n")
        mockMvcTestBoilerplate.append("\t}\n")

        return mockMvcTestBoilerplate.toString()
    }

    private fun getRequestFieldsDocs(requestBody: PsiParameter): String {
        val psiClass = (requestBody.type as PsiClassType).resolve()
        return if (psiClass != null) {
            ",\n\t\t\t\t\trequestFields(" +
                    "\n${
                        generateRecursiveRestDocText(
                            psiClass,
                            "",
                            AtomicInteger(10)
                        ).joinToString(
                            separator = ",\n"
                        ) { fieldWithPath ->
                            "\t\t\t\t\t\t $fieldWithPath"
                        }
                    }\t\t\t\t\t" +
                    "\n\t\t\t\t\t)"
        } else {
            ""
        }
    }

    private fun getResponseFieldsDocs(response: PsiType): String {
        if (response.canonicalText == "void") return ""
        val responseType = getPsiClassFrom(response) ?: return ""
        return ",\n\t\t\t\t\tresponseFields(" +
                "\n${
                    generateRecursiveRestDocText(
                        responseType,
                        "",
                        AtomicInteger(10)
                    ).joinToString(
                        separator = ",\n"
                    ) { fieldWithPath ->
                        "\t\t\t\t\t\t $fieldWithPath"
                    }
                }\t\t\t\t\t" +
                "\n\t\t\t\t\t)"
    }

    private fun getPsiClassFrom(
        response: PsiType,
    ): PsiClass? {
        val psiClassType = (response as PsiClassType)
        if (TypeReference.isCollection(response.canonicalText)) {
            val parameter = psiClassType.parameters[0]
            return (parameter as PsiClassType).resolve()
        }
        return psiClassType.resolve()
    }

    private fun getPathParametersDocs(methodProperty: MethodProperty) =
        methodProperty.pathVariables.joinToString(
            prefix = ",\n\t\t\t\t\t pathParameters(\n",
            separator = ",\n",
            postfix = "\n\t\t\t\t\t)"
        ) { pathVariable ->
            "\t\t\t\t\t\t parameterWithName(\"${pathVariable.name}\").description(\"${pathVariable.name}\")"
        }

    private fun getQueryParamsDocs(methodProperty: MethodProperty) =
        methodProperty.queryParams.joinToString(
            prefix = ",\n\t\t\t\t\t queryParameters(\n",
            separator = ",\n",
            postfix = "\n\t\t\t\t\t)"
        ) { queryParam ->
            "\t\t\t\t\t\t parameterWithName(\"${queryParam.name}\").description(\"${queryParam.name}\")"
        }

    private fun getHttpMethodAndEndPoint(methodProperty: MethodProperty) =
        "\t\t\t${methodProperty.httpMethodName}(${getRequestPath(methodProperty)})\n"

    private fun getRequestPath(methodProperty: MethodProperty): String {
        val baseEndPoint = methodProperty.requestPath
        var urlVariables = ""
        if (methodProperty.pathVariables.isNotEmpty()) {
            urlVariables = methodProperty.pathVariables.joinToString(
                prefix = urlVariables,
                separator = ", "
            ) { pathVariable -> pathVariable.name }
        }
        if (methodProperty.queryParams.isNotEmpty()) {
            val queryParams = methodProperty.queryParams.joinToString(
                prefix = "${baseEndPoint}?",
                separator = "&"
            ) { queryParam -> "${queryParam.name}={${queryParam.name}}" }

            urlVariables = methodProperty.queryParams.joinToString(
                prefix = if (urlVariables != "") "${urlVariables}, " else "",
                separator = ", "
            ) { queryParam -> queryParam.name }

            return "\"${queryParams}\", $urlVariables"
        }
        return if (urlVariables != "") "\"${baseEndPoint}\", ${urlVariables}" else "\"${baseEndPoint}\""
    }

    private fun generateRecursiveRestDocText(
        psiClass: PsiClass,
        beforeFieldName: String,
        atomicInteger: AtomicInteger,
    ): List<String> {

        println("fieldName : $beforeFieldName")
        println("1111 psiClass: ${psiClass.qualifiedName}")
        val restDocList: MutableList<String> = ArrayList()
        if (atomicInteger.getAndAdd(-1) < 1) {
            return restDocList
        }
        val fields = psiClass.fields

        fields.forEach { field: PsiField ->
            val type = field.type
            val className = type.canonicalText
            var fieldName = beforeFieldName + field.name

            if (type is PsiPrimitiveType)
            {
                println("primitive fieldName : $beforeFieldName")
                println("primitive psiClass: ${psiClass.qualifiedName}")
                restDocList.add(generateFieldWithPathText(fieldName))
            }
            if (type is PsiClassType) {
                val resolvedClass = type.resolve()
                if (resolvedClass != null && isDocumentableClass(resolvedClass)){
                    println("isDocumentableClass fieldName : $beforeFieldName")
                    println("isDocumentableClass psiClass: ${psiClass.qualifiedName}")
                    println("isDocumentableClass qualifiedName: ${psiClass.qualifiedName}")
                    restDocList.add(generateFieldWithPathText(fieldName))
                } else if (TypeReference.isCollection(className)) {
                    println("COLLECTION fieldName : $beforeFieldName")
                    println("COLLECTION psiClass: ${psiClass.qualifiedName}")
                    val parameter = type.parameters[0]
                    val genericClass = (parameter as PsiClassType).resolve()
                    fieldName += ".[]."
                    if (genericClass == null) return@forEach
                    restDocList.addAll(
                        generateRecursiveRestDocText(genericClass, fieldName, atomicInteger)
                    )
                } else {
                    println("else fieldName : $beforeFieldName")
                    println("else psiClass: ${psiClass.qualifiedName}")
                    if (resolvedClass == null) return@forEach
                    fieldName += "."
                    restDocList.addAll(
                        generateRecursiveRestDocText(
                            resolvedClass,
                            fieldName,
                            atomicInteger
                        )
                    )
                }
            }
        }
        return restDocList
    }

    private fun isDocumentableClass(targetClass: PsiClass): Boolean {
        println("targetClass: ${targetClass.qualifiedName}")
        return targetClass.qualifiedName?.let { qualifiedName ->
            return TypeReference.isDocumentableClass(qualifiedName)
        } ?: false
    }

    private fun generateFieldWithPathText(fieldName: String) =
        "fieldWithPath(\"$fieldName\").description(\"$fieldName\")"
}