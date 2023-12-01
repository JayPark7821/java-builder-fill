package kr.craft.javaboilercraft.processor.util

import com.intellij.psi.PsiElement
import kr.craft.javaboilercraft.processor.util.EditorUtils.getIndent


/**
 * MockMvcTestBoilerplateGenerator
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/30/23
 */
object MockMvcTestBoilerplateGenerator {
    fun generateBoilerplate(methodProperty: MethodProperty, targetElement: PsiElement): String {

        val mockMvcTestBoilerplate = StringBuilder()
        mockMvcTestBoilerplate.append("\t@Test\n")
        mockMvcTestBoilerplate.append("\tvoid ${methodProperty.name}() throws Exception {\n")
        mockMvcTestBoilerplate.append("\t\tmockMvc.perform(\n")
        mockMvcTestBoilerplate.append("\t\t\t${methodProperty.httpMethodName}(${getRequestPath(methodProperty)})\n")
        mockMvcTestBoilerplate.append("\t\t\t\t.contentType(MediaType.APPLICATION_JSON)\n")
        mockMvcTestBoilerplate.append("\t\t\t\t.accept(MediaType.APPLICATION_JSON)\n")
        mockMvcTestBoilerplate.append("\t\t)\n")
        if (methodProperty.requestBody != null) mockMvcTestBoilerplate.append("\t\t\t\t.content(objectMapper.writeValueAsString(${methodProperty.requestBody}))\n")
        mockMvcTestBoilerplate.append("\t\t\t\t.andDo(print())\n")
        mockMvcTestBoilerplate.append("\t\t\t\t.andDo(document(\"\" ")
       if(methodProperty.pathVariables.isNotEmpty())
           mockMvcTestBoilerplate.append(methodProperty.pathVariables.joinToString(
            prefix = ",\n\t\t\t\t\t pathParameters(\n",
            separator = ",\n",
            postfix = "\n\t\t\t\t\t)"
        ) { pathVariable ->
            "\t\t\t\t\t\t parameterWithName(\"${pathVariable.name}\").description(\"${pathVariable.name}\")"
        })
        if(methodProperty.queryParams.isNotEmpty())
            mockMvcTestBoilerplate.append(methodProperty.queryParams.joinToString(
            prefix = ",\n\t\t\t\t\t queryParameters(\n",
            separator = ",\n",
            postfix = "\n\t\t\t\t\t)"
        ) { queryParam ->
            "\t\t\t\t\t\t parameterWithName(\"${queryParam.name}\").description(\"${queryParam.name}\")"
        })
        if(methodProperty.requestBody != null)
            mockMvcTestBoilerplate.append(",\n\t\t\t\t\t requestFields(\n" +
                    "\t\t\t\t\t\t fieldWithPath(\"${methodProperty.requestBody.name}\").description(\"${methodProperty.requestBody.name}\")\n" +
                    "\t\t\t\t\t)\n")

        mockMvcTestBoilerplate.append("\n\t\t\t\t));\n")
        mockMvcTestBoilerplate.append("\t}\n")

        return mockMvcTestBoilerplate.toString()
    }

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
                prefix = "${baseEndPoint }?",
                separator = "&"
            ) { queryParam ->"${queryParam.name}={${queryParam.name}}"}

            urlVariables = methodProperty.queryParams.joinToString(
                prefix = if(urlVariables!= "") "${urlVariables}, " else "",
                separator = ", "
            ) { queryParam -> queryParam.name }

            return "\"${queryParams}\", $urlVariables"
        }
        return if(urlVariables != "") "\"${baseEndPoint}\", ${urlVariables}" else "\"${baseEndPoint}\""
    }
}