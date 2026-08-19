package com.example.model

sealed class InputField(
    open val id: String,
    open val label: String,
    open val hint: String = "",
    open val isRequired: Boolean = true,
    open val helpText: String = ""
) {
    data class Text(
        override val id: String,
        override val label: String,
        override val hint: String = "",
        override val isRequired: Boolean = true,
        val defaultValue: String = "",
        override val helpText: String = ""
    ) : InputField(id, label, hint, isRequired, helpText)

    data class TextArea(
        override val id: String,
        override val label: String,
        override val hint: String = "",
        override val isRequired: Boolean = true,
        val defaultValue: String = "",
        val minLines: Int = 3,
        override val helpText: String = ""
    ) : InputField(id, label, hint, isRequired, helpText)

    data class Select(
        override val id: String,
        override val label: String,
        val options: List<String>,
        val defaultValue: String = options.firstOrNull() ?: "",
        override val isRequired: Boolean = true,
        override val helpText: String = ""
    ) : InputField(id, label, "", isRequired, helpText)

    data class Slider(
        override val id: String,
        override val label: String,
        val min: Float,
        val max: Float,
        val steps: Int = 0,
        val defaultValue: Float,
        val unit: String = "",
        override val isRequired: Boolean = false,
        override val helpText: String = ""
    ) : InputField(id, label, "", isRequired, helpText)

    data class Toggle(
        override val id: String,
        override val label: String,
        val defaultValue: Boolean = false,
        override val isRequired: Boolean = false,
        override val helpText: String = ""
    ) : InputField(id, label, "", isRequired, helpText)
}
