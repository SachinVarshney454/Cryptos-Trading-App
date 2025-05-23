package com.example.govt01.Authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.govt01.MainActivity
import com.example.govt01.ui.theme.Govt01Theme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class Signup:ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Govt01Theme {
                SignUpScreen()
            }
        }
    }
}
fun signup(name:String,email:String,password:String,context: Context){
    val db = Firebase.firestore;
    val firebase=FirebaseAuth.getInstance()
    firebase.createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener(){task->
            if (task.isSuccessful){
                val userdata= hashMapOf(
                    "name" to name,
                    "email" to email,
                    "chips" to 100000
                )
                db.collection("user").document(email)
                    .set(userdata)

                    .addOnCompleteListener{task->
                        if(task.isSuccessful){
                            val intent=Intent(context,MainActivity::class.java)
                            context.startActivity(intent)
                        }

                    }


            }
            else Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show()

        }


}
@Composable
fun SignUpScreen(
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repass by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign Up")
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = repass,
            onValueChange = { repass = it },
            label = { Text("Re-Enter Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        val context= LocalContext.current

        Button(
            onClick = {
                if (name.equals("")){
                    Toast.makeText(context, "Please Enter Name", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (email.equals("")){
                    Toast.makeText(context, "Please Enter Email", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (password.equals("")){
                    Toast.makeText(context, "Please Enter Password", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if(!repass.equals(password)){
                    Toast.makeText(context, "Please Enter Same Password", Toast.LENGTH_SHORT).show()
                    return@Button

                }
                signup(name,email,password,context)



            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen()
}
