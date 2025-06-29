package com.venki.xmppdemo.ui

import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.venki.xmppdemo.R

@Composable
fun TextFieldComponent(
    modifier: Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = true
    )
}

@Composable
fun ButtonComponent(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(text = text)
    }
}

@Composable
fun ImageComponent(
    modifier: Modifier,
    alignment: Alignment,
    imageId: Int
) {
    Image(
        modifier = modifier,
        alignment = alignment,
        painter = painterResource(id = imageId),
        contentDescription = "app_logo"
    )
}