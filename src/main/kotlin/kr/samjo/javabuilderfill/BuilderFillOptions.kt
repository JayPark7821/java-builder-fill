package kr.samjo.javabuilderfill

/**
 * BuilderFillOptions
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/17/23
 */
enum class BuilderFillOptions(val value: String) {

    BUILDER("Builder-Fill-Generator"),
    CONSTRUCTOR("Constructor-Fill-Generator"),
    ;

    companion object{
        fun findOption(value: String): BuilderFillOptions {
            return BuilderFillOptions.values().find { it.value == value } ?:
            throw IllegalArgumentException("Invalid BuilderFillOptions")
        }
    }
}