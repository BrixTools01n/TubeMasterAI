package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HistoryEntity
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.model.GenerationResult
import com.example.model.InputField
import com.example.model.ToolConfig
import com.example.model.ToolOutputType
import com.example.ui.components.CustomDropdown
import com.example.ui.components.CustomSlider
import com.example.ui.components.GenerateButton
import com.example.ui.components.PlatformBadge
import com.example.ui.components.ProTag
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ProGold
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceBorderActive
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TubeMasterRed
import com.example.ui.theme.TubeMasterRedGlow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeneratorScreen(
    tool: ToolConfig,
    inputs: Map<String, Any>,
    isGenerating: Boolean,
    result: GenerationResult?,
    isSaved: Boolean,
    error: String?,
    history: List<HistoryEntity>,
    isPro: Boolean,
    language: AppLanguage,
    onInputChange: (String, Any) -> Unit,
    onGenerate: () -> Unit,
    onSave: () -> Unit,
    onCopy: (String, String) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("generator_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Top App Bar
        item {
            GeneratorHeader(
                tool = tool,
                onBack = onBack
            )
        }

        // Tool Info Box
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlatformBadge(platform = tool.platform)
                        Text(
                            text = tool.category,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tool.description,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Input Form
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = Translations.get("generator.customize_input", language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    tool.fields.forEach { field ->
                        FieldRenderer(
                            field = field,
                            currentValue = inputs[field.id],
                            onValueChange = { onInputChange(field.id, it) }
                        )
                    }

                    // Optional Custom Directives Input
                    Column {
                        Text(
                            text = Translations.get("generator.extra_instructions", language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = (inputs["additional_instructions"] as? String) ?: "",
                            onValueChange = { onInputChange("additional_instructions", it) },
                            placeholder = {
                                Text(
                                    text = Translations.get("generator.extra_instructions_hint", language),
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceDark,
                                unfocusedContainerColor = SurfaceDark,
                                focusedBorderColor = TubeMasterRed,
                                unfocusedBorderColor = SurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Error Message
                    if (error != null) {
                        Text(
                            text = error,
                            fontSize = 12.sp,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Generate CTA Button
                    GenerateButton(
                        text = if (result != null) Translations.get("generator.regenerate", language) else Translations.get("generator.generate_btn", language),
                        isLoading = isGenerating,
                        onClick = onGenerate
                    )
                }
            }
        }

        // Generated Output Section
        if (result != null) {
            item {
                OutputContainer(
                    result = result,
                    isSaved = isSaved,
                    language = language,
                    onSave = onSave,
                    onCopy = onCopy
                )
            }
        }

        // History Accordion for this Tool
        if (history.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Translations.get("generator.recent_gen", language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }

                    history.take(3).forEach { item ->
                        HistoryCard(
                            item = item,
                            onCopy = { onCopy(item.content, Translations.get("toast.copied", language)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun GeneratorHeader(
    tool: ToolConfig,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("generator_back_button")
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = tool.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (tool.isPro) {
                    Spacer(modifier = Modifier.width(6.dp))
                    ProTag()
                }
            }
            Text(
                text = "${tool.platform.displayName} Toolkit",
                fontSize = 11.sp,
                color = TubeMasterRedGlow
            )
        }
    }
}

@Composable
fun FieldRenderer(
    field: InputField,
    currentValue: Any?,
    onValueChange: (Any) -> Unit
) {
    Column {
        when (field) {
            is InputField.Text -> {
                Text(
                    text = field.label + if (field.isRequired) " *" else "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                val strVal = currentValue?.toString() ?: field.defaultValue
                OutlinedTextField(
                    value = strVal,
                    onValueChange = onValueChange,
                    placeholder = { Text(field.hint, fontSize = 12.sp, color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedBorderColor = TubeMasterRed,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is InputField.TextArea -> {
                Text(
                    text = field.label + if (field.isRequired) " *" else "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                val strVal = currentValue?.toString() ?: field.defaultValue
                OutlinedTextField(
                    value = strVal,
                    onValueChange = onValueChange,
                    placeholder = { Text(field.hint, fontSize = 12.sp, color = TextMuted) },
                    minLines = field.minLines,
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedBorderColor = TubeMasterRed,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is InputField.Select -> {
                val selected = currentValue?.toString() ?: field.defaultValue
                CustomDropdown(
                    label = field.label + if (field.isRequired) " *" else "",
                    options = field.options,
                    selectedOption = selected,
                    onOptionSelected = { onValueChange(it) }
                )
            }

            is InputField.Slider -> {
                val floatVal = (currentValue as? Float) ?: field.defaultValue
                CustomSlider(
                    label = field.label,
                    value = floatVal,
                    onValueChange = { onValueChange(it) },
                    min = field.min,
                    max = field.max,
                    steps = (field.max - field.min).toInt() - 1,
                    unit = field.unit
                )
            }

            is InputField.Toggle -> {
                val boolVal = (currentValue as? Boolean) ?: field.defaultValue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = field.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Switch(
                        checked = boolVal,
                        onCheckedChange = { onValueChange(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TubeMasterRed,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceDark
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OutputContainer(
    result: GenerationResult,
    isSaved: Boolean,
    language: AppLanguage,
    onSave: () -> Unit,
    onCopy: (String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("generation_result_card"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardElevated),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderActive)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with title and Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Translations.get("generator.output_title", language),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { onCopy(result.rawText, Translations.get("toast.copied", language)) },
                        modifier = Modifier.testTag("copy_all_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy All",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onSave,
                        modifier = Modifier.testTag("save_result_button")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) TubeMasterRed else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body Display depending on outputType
            when (result.outputType) {
                ToolOutputType.LIST -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        result.items.forEachIndexed { index, item ->
                            ListItemCard(
                                index = index + 1,
                                text = item,
                                onCopy = { onCopy(item, Translations.get("toast.copied", language)) }
                            )
                        }
                    }
                }

                ToolOutputType.TAGS -> {
                    Column {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            result.tags.forEach { tag ->
                                TagChip(
                                    tag = tag,
                                    onClick = { onCopy(tag, Translations.get("toast.copied", language)) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onCopy(result.rawText, Translations.get("toast.copied", language)) },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                        ) {
                            Text("Copy All ${result.tags.size} Tags", fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }

                ToolOutputType.SCRIPT -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (result.sections.isNotEmpty()) {
                            result.sections.forEach { (heading, body) ->
                                ScriptSectionCard(heading = heading, body = body)
                            }
                        } else {
                            Text(
                                text = result.rawText,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                ToolOutputType.STRATEGY, ToolOutputType.KEY_VALUE -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (result.sections.isNotEmpty()) {
                            result.sections.forEach { (title, content) ->
                                StrategySectionCard(title = title, content = content)
                            }
                        } else {
                            Text(
                                text = result.rawText,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }

                ToolOutputType.TEXT -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceDark)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = result.rawText,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListItemCard(index: Int, text: String, onCopy: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "$index.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TubeMasterRedGlow,
                modifier = Modifier.width(22.dp)
            )
            Text(
                text = text,
                fontSize = 13.sp,
                color = TextPrimary,
                lineHeight = 18.sp
            )
        }

        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ContentCopy,
                contentDescription = "Copy Item",
                tint = TextMuted,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
fun TagChip(tag: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = tag,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TubeMasterRedGlow
        )
    }
}

@Composable
fun ScriptSectionCard(heading: String, body: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = heading,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TubeMasterRed
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = body,
                fontSize = 13.sp,
                color = TextPrimary,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
fun StrategySectionCard(title: String, content: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ProGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun HistoryCard(item: HistoryEntity, onCopy: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.inputSummary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = item.content,
                fontSize = 11.sp,
                color = TextMuted,
                maxLines = 2
            )
        }
        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ContentCopy,
                contentDescription = "Copy",
                tint = TextMuted,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}
