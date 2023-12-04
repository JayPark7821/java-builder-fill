package kr.craft.javaboilercraft.processor.util

import com.intellij.psi.CommonClassNames.*

/**
 * TypeReference
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/3/23
 */
enum class TypeReference(
    private val type: List<String>,
) {
    COLLECTION(
        listOf(
            JAVA_UTIL_ARRAYS,
            JAVA_UTIL_COLLECTIONS,
            JAVA_UTIL_COLLECTION,
            JAVA_UTIL_MAP,
            JAVA_UTIL_MAP_ENTRY,
            JAVA_UTIL_HASH_MAP,
            JAVA_UTIL_LINKED_HASH_MAP,
            JAVA_UTIL_CONCURRENT_HASH_MAP,
            JAVA_UTIL_LIST,
            JAVA_UTIL_ARRAY_LIST,
            JAVA_UTIL_LINKED_LIST,
            JAVA_UTIL_SET,
            JAVA_UTIL_HASH_SET,
            JAVA_UTIL_LINKED_HASH_SET,
            JAVA_UTIL_SORTED_SET,
        )
    ),
    CHARACTER(
        listOf(
            JAVA_LANG_STRING,
            JAVA_LANG_CHARACTER,
        )
    ),
    BOOLEAN(
        listOf(
            JAVA_LANG_BOOLEAN,
        )
    ),
    NUMBER(
        listOf(
            JAVA_LANG_SHORT,
            JAVA_LANG_INTEGER,
            JAVA_LANG_LONG,
            JAVA_LANG_FLOAT,
            JAVA_LANG_DOUBLE,
            JAVA_LANG_NUMBER
        )
    )
    ;

    companion object {
        fun isCollection(className: String): Boolean {
            return COLLECTION.type.any { type ->
                    className.contains(type)
            }
        }

        fun isDocumentableClass(className: String): Boolean {
            return listOf(NUMBER, BOOLEAN, CHARACTER).find { typeReference ->
                typeReference.type.any { type ->
                    type.contains(className)
                }
            } != null
        }
    }
}