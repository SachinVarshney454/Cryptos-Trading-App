package com.example.govt01

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.govt01.ui.theme.Govt01Theme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.merge
import retrofit2.Callback

class buy :ComponentActivity(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
                Govt01Theme {
                    var name=intent.getStringExtra("name")

                    var price=intent.getStringExtra("price".toString())
                    var sym=intent.getStringExtra("symbol")
                    var change = intent.getStringExtra("change").toString()
                    var market=intent.getStringExtra("market").toString()
                    Buy(name?:"not",price?:"05435",sym?:"",change?:"0",market?:"0")


                }
            }
        }
    }
@Composable
fun Buy(name: String, price: String, symbol: String, change: String, market: String) {
    val cryptoPrice = price
    val cryptoName = name
    val context = LocalContext.current


    var investedAmount by remember { mutableStateOf("Loading...") }

    // Fetch invested amount
    LaunchedEffect(Unit) {
        getpurchaseinfo(context, cryptoName) {
            investedAmount = it
        }
    }

    val cryptoInfo = listOf(
        "Symbol" to symbol,
        "Market Cap" to market,
        "24h Change" to "$change%",
        "Current Value" to price
    )

    Column(
        modifier = Modifier
            .fillMaxSize().background(Color(0xFFFAF3E0))
            .verticalScroll(rememberScrollState())
            .padding(16.dp).padding(top=55.dp)
    ) {
        Text(
            text = cryptoName,
            color = Color.White, fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth().height(50.dp).clip(
                RoundedCornerShape(18.dp)).background(Color(0xFF1E1E2C)).padding(start = 23.dp, top = 12.dp)
            ,textAlign = TextAlign.Center,

        )

        Spacer(modifier = Modifier.height(0.dp))

        CryptoChartPlaceholder(cryptoName.toLowerCase())

        Spacer(modifier = Modifier.height(24.dp))


        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(cryptoInfo) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(text = item.first, fontSize = 14.sp, color = Color.LightGray)
                    val valueColor = if (item.second.isDigitsOnly() && item.second.toFloat() > 0) Color.Green else Color.Red
                    val displayText = if (item.first == "Current Value") "$${item.second}" else item.second
                    Text(
                        text = (displayText),
                        color = if (item.first == "Current Value") Color.White else Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
//        dbdetail(cryptoName) { list ->
//            val data = listOf(
//                "Average price" to list[0],
//                "Total Crypto" to list[1]
//            )
//        }
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
//            modifier = Modifier.height(90.dp)
//                   , verticalArrangement = Arrangement.spacedBy(8.dp),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp),
//            contentPadding = PaddingValues(horizontal = 8.dp)
//        ) {
//            items() { item->
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
//                        .padding(12.dp)
//
//                ) {
//                    Text(text = item.first, fontSize = 14.sp, color = Color.LightGray)
//                    Text(
//                        text = item.second,
//                        fontSize = 16.sp,
//                        color = Color.White,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                }
//            }
//        }

        Spacer(modifier = Modifier.height(0.dp))
        var cryptoAmount by remember { mutableStateOf("") }
        var value by remember  { mutableStateOf("0")}

        OutlinedTextField(
            value = cryptoAmount,
            onValueChange = { cryptoAmount = it
                if(cryptoAmount.equals("")){}
               else {
                    var temp = 1 / price.toDouble()
                    value = String.format("%.4f",cryptoAmount.toDouble() * temp)
                }
                            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                .padding(12.dp)

        ) {
            Text(text = "Amount of Crypto", fontSize = 14.sp, color = Color.LightGray)
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
        val cryptoown = remember { mutableStateOf("") }
        val assetvalue = remember { mutableStateOf("") }

        LaunchedEffect(cryptoName) {
            specificcrypto(cryptoName) { list ->
                 cryptoown.value = list.get(0)
                 assetvalue.value = list.get(1)
            }

        }
        val list = listOf(
            "Crypto you own" to cryptoown,
            "Asset Value" to assetvalue
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth().padding(top=15.dp)
                .height(90.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(list){item->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(text =item.first, fontSize = 14.sp, color = Color.LightGray)
                    Text(
                        text = item.second.toString(),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }




        Spacer(modifier = Modifier.height(6.dp))

        // Buy / Sell buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
//                   sell()
                },
                modifier = Modifier.weight(1F),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("Sell", color = Color.White)
            }

            Button(
                onClick = {
                    cryptobuy(context, name, price, cryptoAmount,value.toString())
                },
                modifier = Modifier.weight(1F),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("Buy", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(40.dp)) // Bottom spacing
    }
}
fun cryptobuy(context: Context,cryptoname:String,price: String,current: String,numberofcrypto:String){
    val fb=FirebaseAuth.getInstance().currentUser?:null
//    val context= LocalContext.current
    val email=fb?.email
    val db=FirebaseFirestore.getInstance()
    if(email==null){
//        Toast.makeText(, "", Toast.LENGTH_SHORT).show()
        return
    }
    db.collection("data").document(email).collection("purchase").document(cryptoname)
        .get().addOnSuccessListener { task->
            if(!task.exists()){
                createdata(email,cryptoname,price, current ,numberofcrypto)
            }
            else{
                getdata(email,cryptoname,numberofcrypto,current,price)


            }
        }
}
fun getdata(email: String,cryptoname: String,quantity: String,currmoney:String,currenttradingprice:String ){
    val db=FirebaseFirestore.getInstance()
    var money = "0"
    var quant="0"
    db.collection("data").document(email).collection("purchase").document(cryptoname)
        .get()
        .addOnSuccessListener { task->

             money=task.get("totalmoneyspent").toString()
             quant= task.get("totalquantity").toString()
            val tradingprice=task.get("averagetradingprice").toString()
            val totalmoney=money.toFloat()+currmoney.toFloat()
            val totalquantity= quant.toFloat()+quantity.toFloat()
            val averagetradingprice= (tradingprice.toFloat()+currenttradingprice.toFloat())/2;

            val fb=FirebaseAuth.getInstance().currentUser?:null
            val data= mapOf(
                "totalmoneyspent" to totalmoney,
                "totalquantity" to String.format("%.4f",totalquantity),
                "averagetradingprice" to averagetradingprice
            )
            db.collection("data").document(email).collection("purchase").document(cryptoname)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {

                }
        }
    Log.d("quantity",quant)





}
fun createdata(email:String,cryptoname: String,price: String,current:String,quantity:String){
    val fb=FirebaseAuth.getInstance().currentUser?:null
    val data= mapOf(
        "name" to cryptoname,
        "totalquantity" to quantity,
        "totalmoneyspent" to current,
        "averagetradingprice" to price
    )


    val db=FirebaseFirestore.getInstance()
    db.collection("data").document(email).collection("purchase").document(cryptoname)
        .set(data)
        .addOnSuccessListener {

        }

}
fun getpurchaseinfo(context: Context, name: String, callback: (String) -> Unit) {
    val email = FirebaseAuth.getInstance().currentUser?.email ?: return callback("Not logged in")
    val db = FirebaseFirestore.getInstance()

    db.collection("data").document(email).collection("purchase").document(name)
        .get()
        .addOnSuccessListener { doc ->
            if (doc.exists()) {
                val qty = doc.get("quantity")?.toString() ?: "0"
                callback("${qty}")
            } else {
                callback("₹0")
            }
        }
        .addOnFailureListener {
            callback("Error loading")
        }
}
fun dbdetail(name: String, onResult: (List<String>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val email = FirebaseAuth.getInstance().currentUser?.email

    db.collection("data").document(email ?: "").collection("purchase").document(name)
        .get()
        .addOnSuccessListener { task ->
            if (task.exists()) {
                val quant = task.get("quantity").toString()
                val avg = task.get("average").toString()

                val quantity = String.format("%.4f", quant.toDoubleOrNull() ?: 0.0)
                val average = String.format("%.4f", avg.toDoubleOrNull() ?: 0.0)

                onResult(listOf(average, quantity))
            } else {
                onResult(listOf("0.0000", "0.0000"))
            }
        }
        .addOnFailureListener {
            onResult(listOf("0.0000", "0.0000"))
        }
}
fun specificcrypto(name:String,onResult: (List<String>) -> Unit){
    val db = FirebaseFirestore.getInstance()
    val email = FirebaseAuth.getInstance().currentUser?.email

    db.collection("data").document(email ?: "").collection("purchase").document(name)
        .get()
        .addOnSuccessListener { task ->
            if (task.exists()) {
                val cryptoown= task.get("totalquantity").toString()
                val averagetradingprice=task.get("averagetradingprice").toString()
                val assetvalue=cryptoown.toFloat()*averagetradingprice.toFloat()
                 onResult(listOf(cryptoown.toString(),assetvalue.toString()))
            }

            }

}
@Preview(showBackground = true)
@Composable
fun buypre(){
    Govt01Theme {
        Buy("0","0","0","0","0")
    }
}
