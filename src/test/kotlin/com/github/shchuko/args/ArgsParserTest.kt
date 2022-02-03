package com.github.shchuko.args

import com.github.shchuko.args.exception.ArgsParseException
import com.github.shchuko.args.exception.OptionsValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class ArgsParserTest {
    @Test
    fun `test validate throws private class`() {
        assertThrows<OptionsValidationException> {
            ArgsParser.validate(OptionsBadPrivateClass)
        }
    }

    @Test
    fun `test validate throws private field`() {
        assertThrows<OptionsValidationException> {
            ArgsParser.validate(OptionsBadPrivateField)
        }
    }

    @Test
    fun `test validate throws immutable field`() {
        assertThrows<OptionsValidationException> {
            ArgsParser.validate(OptionsBadImmutableField)
        }
    }

    @Test
    fun `test validate throws unsupported type`() {
        assertThrows<OptionsValidationException> {
            ArgsParser.validate(OptionsBadUnsupportedType)
        }
    }

    @Test
    fun `test validate throws boolean with 'required=true'`() {
        assertThrows<OptionsValidationException> {
            ArgsParser.validate(OptionsBadBooleanRequired)
        }
    }

    @Test
    fun `test validate throws duplicated aliases`() {
        assertThrows<OptionsValidationException> {
            ArgsParser.validate(OptionsBadDuplicatedAliases)
        }
    }

    @Test
    fun `test parse ok positive`() {
        val parseInto = OptionsOk()
        ArgsParser.parse(parseInto, OptionsOk.ExpectedPositive.toArgs())
        assertEquals(OptionsOk.ExpectedPositive, parseInto)
    }

    @Test
    fun `test parse ok negative`() {
        val parseInto = OptionsOk()
        ArgsParser.parse(parseInto, OptionsOk.ExpectedNegative.toArgs())
        assertEquals(OptionsOk.ExpectedNegative, parseInto)
    }

    @Test
    fun `test parse ok positive with garbage`() {
        val parseInto = OptionsOk()
        ArgsParser.parse(parseInto, OptionsOk.ExpectedPositive.toArgsWithGarbage())
        assertEquals(OptionsOk.ExpectedPositive, parseInto)
    }

    @Test
    fun `test parse throws no required`() {
        val parseInto = OptionsOk()
        assertThrows<ArgsParseException> {
            ArgsParser.parse(
                parseInto,
                OptionsOk.ExpectedPositive.toBadArgsNoRequired()
            )
        }
    }

    @Test
    fun `test parse throws no value`() {
        val parseInto = OptionsOk()
        assertThrows<ArgsParseException> { ArgsParser.parse(parseInto, OptionsOk.ExpectedPositive.toBadArgsNoValue()) }
    }

    @Test
    fun `test parse throws malformed value`() {
        val parseInto = OptionsOk()
        assertThrows<ArgsParseException> {
            ArgsParser.parse(
                parseInto,
                OptionsOk.ExpectedPositive.toBadArgsMalformedValue()
            )
        }
    }

    @Test
    fun `test parse with validate throws`() {
        assertThrows<OptionsValidationException> {
            ArgsParser.parse(
                OptionsBadUnsupportedType,
                OptionsOk.ExpectedPositive.toBadArgsMalformedValue(),
                validateOptions = true
            )
        }
    }
}

private object OptionsBadPrivateClass {
    @Option
    var a: Int = 0
}

object OptionsBadPrivateField {
    @Option
    private var a: Int = 0
}

object OptionsBadImmutableField {
    @Option
    val a: Int = 0
}

object OptionsBadUnsupportedType {
    @Option
    var a: Map<Int, Int>? = null
}

object OptionsBadBooleanRequired {
    @Option(required = true)
    var a: Boolean = false
}

object OptionsBadDuplicatedAliases {
    @Option(alias = "foo")
    var a: Boolean = false

    @Option(alias = "foo")
    var b: Boolean = false
}

open class OptionsOk {
    @Option
    open var int: Int = 0

    @Option
    open var string: String = ""

    @Option
    open var byte: Byte = 0

    @Option
    open var short: Short = 0

    @Option
    open var float: Float = 0.0f

    @Option
    open var double: Double = 0.0

    @Option
    open var bool: Boolean = false

    @Option(alias = "someAlias")
    open var withAlias: Int = 0

    @Option(required = true)
    open var required: Int = 0

    fun toArgs(): List<String> {
        val list = mutableListOf(
            "-int", "$int",
            "-string", string,
            "-byte", "$byte",
            "-short", "$short",
            "-float", "$float",
            "-double", "$double",
            "-someAlias", "$withAlias",
            "-required", "$required",
        )

        if (bool) {
            list.add("-bool")
        }
        return list
    }

    fun toArgsWithGarbage(): List<String> {
        val list = mutableListOf(
            "-int", "$int",
            "-string", string,
            "-byte", "$byte",
            "-short", "$short",
            "-garbage", "garbage",
            "-float", "$float",
            "--garbage2", "garbage2",
            "-double", "$double",
            "-someAlias", "$withAlias",
            "-required", "$required",
        )

        if (bool) {
            list.add("-bool")
        }
        return list
    }

    fun toBadArgsNoRequired(): List<String> {
        val list = mutableListOf(
            "-int", "$int",
            "-string", string,
            "-byte", "$byte",
            "-short", "$short",
            "-float", "$float",
            "-double", "$double",
            "-someAlias", "$withAlias",
//            Removed required property
//            "-required", "$required",
        )

        if (bool) {
            list.add("-bool")
        }
        return list
    }

    fun toBadArgsMalformedValue(): List<String> {
        val list = mutableListOf(
            "-int", "fooBar$int",
            "-string", string,
            "-byte", "$byte",
            "-short", "$short",
            "-float", "$float",
            "-double", "$double",
            "-someAlias", "$withAlias",
            "-required", "$required",
        )

        if (bool) {
            list.add("-bool")
        }
        return list
    }

    fun toBadArgsNoValue(): List<String> {
        val list = mutableListOf(
            "-int", "fooBar$int",
            "-string", string,
            "-byte", "$byte",
            "-short", "$short",
            "-float", "$float",
            "-double", "$double",
            "-someAlias", "$withAlias",
        )
        if (bool) {
            list.add("-bool")
        }
        list.add("-required") // No value provided
        return list
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (other is OptionsOk) {
            if (int != other.int) return false
            if (string != other.string) return false
            if (byte != other.byte) return false
            if (short != other.short) return false
            if (float != other.float) return false
            if (double != other.double) return false
            if (bool != other.bool) return false
            if (withAlias != other.withAlias) return false
            if (required != other.required) return false
            return true
        }

        return false
    }

    override fun hashCode(): Int {
        var result = int
        result = 31 * result + string.hashCode()
        result = 31 * result + byte
        result = 31 * result + short
        result = 31 * result + float.hashCode()
        result = 31 * result + double.hashCode()
        result = 31 * result + bool.hashCode()
        result = 31 * result + withAlias
        result = 31 * result + required
        return result
    }


    object ExpectedPositive : OptionsOk() {
        override var int = 42
        override var string = "hello_there"
        override var byte: Byte = 33
        override var short: Short = 54
        override var float: Float = 12.5f
        override var double: Double = 135.245
        override var bool: Boolean = true
        override var withAlias: Int = 55
        override var required: Int = 44
    }

    object ExpectedNegative : OptionsOk() {
        override var int = -42
        override var string = "hello_there"
        override var byte: Byte = -33
        override var short: Short = -54
        override var float: Float = -12.5f
        override var double: Double = -135.245
        override var bool: Boolean = false
        override var withAlias: Int = -55
        override var required: Int = -44
    }
}
