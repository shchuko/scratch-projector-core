package com.github.shchuko.args

import com.github.shchuko.args.exception.ArgsParseException
import com.github.shchuko.args.exception.OptionsValidationException
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation

object ArgsParser {
    private const val OPTION_PREFIX = "-"
    private const val REQUIRED_DESCRIPTION = " (required)"

    fun parse(options: Any, args: List<String>, validateOptions: Boolean = false) {
        if (validateOptions) {
            validate(options)
        }
        lintInternal(options, args)
        parseInternal(options, args)
    }

    fun help(options: Any, header: String? = null, indentLen: Int = 2): String {
        val descriptors = options.toDescriptors()
        if (descriptors.isEmpty()) {
            return header ?: ""
        }

        val padLength = descriptors.maxOf { it.helpPrefix.length }

        return buildString {
            if (header != null) {
                appendLine(header)
            }

            for (descriptor in descriptors) {
                append(" ".repeat(indentLen))
                append(descriptor.helpPrefix.padEnd(padLength))
                append("  ")
                appendLine(descriptor.description)
            }
        }
    }

    fun validate(options: Any) {
        if (options::class.visibility != KVisibility.PUBLIC) {
            throw OptionsValidationException("Options class must be public")
        }

        val descriptors = options.toDescriptors()

        val unsupportedTypes = descriptors
            .filter { !it.isSupportedType() }
            .map { "$OPTION_PREFIX${it.alias} [${it.type}]" }

        if (unsupportedTypes.isNotEmpty()) {
            throw OptionsValidationException(
                "Options of unsupported types found: [${unsupportedTypes.joinToString(", ")}]"
            )
        }

        val duplicatedAliases = descriptors
            .map { it.alias }
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .map { "$OPTION_PREFIX${it.key}" }

        if (duplicatedAliases.isNotEmpty()) {
            throw OptionsValidationException(
                "Duplicated aliases found: [${duplicatedAliases.joinToString(", ")}]"
            )
        }
    }

    private fun Any.toDescriptors() = this::class.declaredMemberProperties
        .asSequence()
        .filter { it.visibility == KVisibility.PUBLIC }
        .filterIsInstance<KMutableProperty<*>>()
        .filter { it.hasAnnotation<Option>() }
        .map { OptionDescriptor(it) }
        .sortedBy { it.alias }
        .toList()

    private fun lintInternal(options: Any, args: List<String>) {
        val descriptors = options.toDescriptors()
        val iterator = args.iterator()

        while (iterator.hasNext()) {
            val token = iterator.next()
            if (!token.startsWith(OPTION_PREFIX)) {
                continue
            }

            val maybeAlias = token.drop(OPTION_PREFIX.length)
            val option = descriptors.find { it.alias == maybeAlias } ?: continue

            if (!option.isBoolean) {
                if (!iterator.hasNext()) {
                    throw ArgsParseException("Options $OPTION_PREFIX$maybeAlias has no value")
                }
                iterator.next()
            }
            option.provided = true
        }

        val requiredButNotProvided = descriptors.filter { it.required && !it.provided }
        if (requiredButNotProvided.isNotEmpty()) {
            val first = requiredButNotProvided.first()
            throw ArgsParseException("Option '${first.alias}' is required")
        }
    }

    private fun parseInternal(options: Any, args: List<String>) {
        val descriptors = options.toDescriptors()
        val iterator = args.iterator()

        while (iterator.hasNext()) {
            val token = iterator.next()
            if (!token.startsWith(OPTION_PREFIX)) {
                continue
            }

            val maybeAlias = token.drop(OPTION_PREFIX.length)
            val option = descriptors.find { it.alias == maybeAlias } ?: continue

            if (option.isBoolean) {
                option.setBoolean(options)
                continue
            }
            val value = iterator.next()
            option.setFromString(value, options)
        }
    }

    private class OptionDescriptor(val property: KMutableProperty<*>) {
        val alias: String
        val description: String
        val required: Boolean
        var provided = false
        val type = property.returnType
        val typeName by lazy { findTypeName() }
        val isBoolean = type == Boolean::class.createType()
        val helpPrefix by lazy { buildHelpPrefix() }

        init {
            val annotation = property.annotations.filterIsInstance<Option>().first()
            alias = annotation.alias.ifBlank { property.name }
            description = annotation.description
            required = annotation.required
        }


        fun setBoolean(options: Any) = setValue(options, true)

        fun setFromString(value: String, options: Any) {
            val transformedValue = try {
                parseValue(value)
            } catch (e: ArgsParseException) {
                throw e
            } catch (e: Throwable) {
                throw ArgsParseException("Unable to parse '$value' into option '$alias' of type '$typeName'", e)
            }
            setValue(options, transformedValue)
        }

        fun isSupportedType(): Boolean {
            return when (type) {
                String::class.createType(), Byte::class.createType(), Short::class.createType(), Int::class.createType(), Long::class.createType(), Float::class.createType(), Double::class.createType(), Boolean::class.createType() -> true
                else -> false
            }
        }

        private fun buildHelpPrefix(): String = buildString {
            append("$OPTION_PREFIX${alias}")
            if (!isBoolean) {
                append(" $typeName")
            }

            if (required) {
                append(REQUIRED_DESCRIPTION)
            }
        }

        private fun findTypeName(): String = when (type) {
            String::class.createType() -> "STR"
            Byte::class.createType() -> "BYTE"
            Short::class.createType() -> "SHORT"
            Int::class.createType() -> "INT"
            Long::class.createType() -> "LONG"
            Float::class.createType() -> "FLOAT"
            Double::class.createType() -> "DOUBLE"
            else -> ""
        }

        private fun parseValue(value: String): Any {
            return when (type) {
                String::class.createType() -> value
                Byte::class.createType() -> value.toByte()
                Short::class.createType() -> value.toShort()
                Int::class.createType() -> value.toInt()
                Long::class.createType() -> value.toLong()
                Float::class.createType() -> value.toFloat()
                Double::class.createType() -> value.toDouble()
                else -> throw ArgsParseException("Unsupported type: ${property.getter.returnType}")
            }
        }

        private fun setValue(options: Any, value: Any) {
            property.setter.call(options, value)
        }
    }
}
