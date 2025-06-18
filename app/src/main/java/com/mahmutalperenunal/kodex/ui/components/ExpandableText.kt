package com.mahmutalperenunal.kodex.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.mahmutalperenunal.kodex.R

@Composable
fun ExpandableText(
    text: String,
    minimizedMaxLines: Int = 4
) {
    var expanded by remember { mutableStateOf(false) }
    var isOverflow by remember { mutableStateOf(false) }

    val showMoreLabel = if (expanded)
        stringResource(R.string.show_less)
    else
        stringResource(R.string.show_more)

    Column {
        Text(
            text = text,
            maxLines = if (expanded) Int.MAX_VALUE else minimizedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                if (!expanded) {
                    isOverflow = textLayoutResult.hasVisualOverflow
                }
            }
        )

        if (isOverflow || expanded) {
            TextButton(onClick = { expanded = !expanded }) {
                Text(showMoreLabel, fontSize = 13.sp)
            }
        }
    }
}