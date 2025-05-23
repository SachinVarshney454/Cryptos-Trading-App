package com.example.govt01.Authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.govt01.MainActivity
import com.example.govt01.R
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log
class Login : ComponentActivity() {
    public val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context= LocalContext.current
            val user= FirebaseAuth.getInstance().currentUser
            if(user!=null){
            val intent= Intent(context,MainActivity::class.java)
            context.startActivity(intent)
            finish()
            }
            Login1()
        }
    }
}
fun sign(ab:Context){
    val intent = Intent(ab, Signup::class.java)
    ab.startActivity(intent)
}
fun login(email:String,password:String,ab:Context){
    val auth = FirebaseAuth.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val intent=Intent(ab,MainActivity::class.java)
                ab.startActivity(intent)
            } else {
                Toast.makeText(ab, "Re-Check your Email and Password", Toast.LENGTH_SHORT).show()
            }
        }

}
@Composable
fun Login1(

) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(20.dp))
//        Row(
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Checkbox(
//                checked = rememberMe,
//                onCheckedChange = { rememberMe = it }
//            )
//            Text("Remember Me")
//        }
        val context= LocalContext.current
        Button(
            onClick = { login(username, password,context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.size(6.dp))
        Button(
            onClick = { sign(context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    Login1(
    )
}