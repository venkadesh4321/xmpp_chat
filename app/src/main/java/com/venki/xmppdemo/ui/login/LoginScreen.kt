package com.venki.xmppdemo.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.venki.xmppdemo.R
import com.venki.xmppdemo.ui.ButtonComponent
import com.venki.xmppdemo.ui.ImageComponent
import com.venki.xmppdemo.ui.TextFieldComponent

@Composable
fun LoginScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        ImageComponent(
            modifier = Modifier.fillMaxWidth(),
            alignment = Alignment.Center,
            imageId = R.drawable.vee_logo
        )

        Column(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextFieldComponent(
                modifier = Modifier.fillMaxWidth(),
                label = "Username",
                value = ""
            ) {
            }

            TextFieldComponent(
                modifier = Modifier.fillMaxWidth(),
                label = "Password",
                value = ""
            ) {
            }

            Spacer(modifier = Modifier.size(20.dp))

            ButtonComponent(
                modifier = Modifier.fillMaxWidth(),
                text = "Login"
            ) {

            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.End,
                text = "Join VeeChat",
                color = Color.Blue
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}