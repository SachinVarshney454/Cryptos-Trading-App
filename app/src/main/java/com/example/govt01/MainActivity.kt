package com.example.govt01
import android.content.Context
import com.example.govt01.Authentication.Login
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.navigation.compose.NavHost
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.govt01.PRICERETRO.Coins
import com.example.govt01.PRICERETRO.Inter
import com.example.govt01.PRICERETRO.Retroins
import com.example.govt01.PRICERETRO.Ticket

import com.example.govt01.chart.RETROAPI
import com.example.govt01.ui.theme.Govt01Theme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.model.GradientColor
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Govt01Theme {
                val navcontroller= rememberNavController();
                NavHost(navcontroller);
//                  bitcoin()

            }
        }
    }
}
@Composable
fun NavHost(navController:NavHostController){
    NavHost(navController=navController,startDestination="home"){
        composable("home"){home(navController)}
        composable("setting"){ setting(navController) }
        composable("watchlist"){ watchpre(navController) }
        composable("portfolio"){ portfolio(navController) }
//        composable("buy"){ buy(navController) }
    }

}
@Composable
fun CoinCard(coinName: String,price :Double,percent:Double,onClick: () -> Unit) {
    Box(modifier = Modifier.clip(RoundedCornerShape(12.dp))){
        Column(
            modifier = Modifier
                .padding(end = 20.dp)
                .background(Color.DarkGray)
                .padding(15.dp).clickable {
                }
        ) {
            Text(text = coinName, color = Color.White, fontSize = 16.sp)
            val formattedPrice = String.format("%.2f", price)
            Text(text = "$$formattedPrice", color = Color.LightGray, fontSize = 14.sp)


            var per:Float=percent.toFloat()
            if(per>0){
                Text(text = "$percent%", color = Color.Green, fontSize = 14.sp)
            }
            else {
                Text(text = "$percent%", color = Color.Red, fontSize = 14.sp)
            }
        }}
}
@Composable
fun CryptoChartPlaceholder(id:String) {
    var chartData by remember { mutableStateOf<List<Pair<Long, Double>>>(emptyList()) }

    LaunchedEffect(true) {
        try {
            if (chartsingelton.chartData.isNotEmpty()) {
                chartData = chartsingelton.chartData
            } else {
                try {
                    val response = RETROAPI.api.getCoinChartData(
                        coin = if (id.isEmpty()) "bitcoin" else id,
                        currency = "inr",
                        days = 7
                    )
                    Log.d("datainc",if(id.isEmpty()) "dafjdsfoa" else "cafjsd")
                    chartData = response.prices.map { Pair(it[0].toLong(), it[1]) }
                    chartsingelton.chartData = chartData
                } catch (e: Exception) {
                    Log.e("ChartData", "Error: ${e.message}")
                }
            }
            Log.d("CharsdadasdsadatData", "Fetched ${chartData.size} data points")
        } catch (e: Exception) {
            Log.d("ijfdsfbcdsuhciusdcbdsc","$e")
            // handle error
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAF3E0))
            .padding(0.dp)
    ) {
//        Text(text = "Bitcoin 7-Day Price Chart", color = Color.White)
        if (chartData.isNotEmpty()) {
            BitcoinLineChart(dataPoints = chartData)
        } else {
            Text("Loading...", color = Color.Gray)
        }
    }
}
@Composable
fun BitcoinLineChart(dataPoints: List<Pair<Long, Double>>) {
    AndroidView(factory = { context ->
        LineChart(context).apply {
            val entries = dataPoints.mapIndexed { index, point ->
                Entry(index.toFloat(), point.second.toFloat()) // x = index, y = price
            }

            val dataSet = LineDataSet(entries, "BTC Price")
            dataSet.color = android.graphics.Color.GREEN
            dataSet.valueTextColor = android.graphics.Color.WHITE
            dataSet.setDrawCircles(false)
            dataSet.lineWidth = 2f

            val lineData = LineData(dataSet)
            this.data = lineData
            this.description.isEnabled = false
            this.setBackgroundColor(android.graphics.Color.DKGRAY)
            this.invalidate()
        }
    }, modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .padding(top = 8.dp))
}



@Composable
fun home(navController: NavHostController) {

    Scaffold(
        containerColor = Color.Black,
        bottomBar = { Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .height(60.dp)
            ,horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("home") }) {
                Image(painter = painterResource(R.drawable.home), contentDescription = null)
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "Home", color = Color.Cyan)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("watchlist") }) {
                Image(painter = painterResource(R.drawable.home), contentDescription = null)
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "WatchList", color = Color.Cyan)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("portfolio") }) {
                Image(painter = painterResource(R.drawable.portfolio1), contentDescription = null)
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "Portfolio", color = Color.Cyan)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("setting") }) {
                Image(painter = painterResource(R.drawable.setting), contentDescription = null)
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "Setting", color = Color.Cyan)
            }
        } } // 👈 Puts tab() at the bottom
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAF3E0))
                .padding(16.dp)
        ) {
            val context = LocalContext.current
            Spacer(modifier = Modifier.size(8.dp))
//            Log.d("TotalCalls", "Total API Calls Made: ${retrofitinstance.getCallCount()}")
            getbasicdata()
            Spacer(modifier = Modifier.size(6.dp))


            CryptoChartPlaceholder("")

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Top Cryptos", textAlign = TextAlign.Start,
                color = Color.White, fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth().height(50.dp).clip(
                    RoundedCornerShape(18.dp)).background(Color(0xFF1E1E2C)).padding(start = 23.dp, top = 12.dp)

            )
            var coins by remember { mutableStateOf<List<Ticket>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            if (coinsingelton.coins.isEmpty()) {
                val response = Retroins.api.getAllTickers()  // suspend call
                coinsingelton.coins = response
                coins = response
            } else {
                coins = coinsingelton.coins
            }
//            Log.e("ChartData", "Fetching data success")
        } catch (e: Exception) {
//            Log.e("ChartData", "Error fetching data: ${e.message}")
        }
    }

    LazyRow {
        items(coins) { coin ->
            // Replace CoinCard with your composable for each coin
            CoinCard(
                coinName = coin.name,
                price = coin.quotes["USD"]?.price ?: 0.0,
                percent = coin.quotes["USD"]?.percent_change_24h ?: 0.0,
                onClick = {  }
            )
        }
    }
}

            val navController1= rememberNavController();
        Box(modifier = Modifier.padding(top=595.dp)) {
            Column {
                Text(
                    text = "Portfolio", textAlign = TextAlign.Start,
                    color = Color.White, fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 0.dp).fillMaxWidth().height(60.dp).clip(
                        RoundedCornerShape(18.dp)
                    ).background(Color(0xFF1E1E2C)).padding(start = 23.dp, top = 20.dp)

                )
                Spacer(modifier = Modifier.size(8.dp))
                port("")
            }
        }
            Spacer(modifier = Modifier.size(30.dp))
//

        }}

@Composable
fun topinfo(name:String,chips:Int){
    Row(
        modifier = Modifier.height(125.dp).fillMaxWidth().padding(top = 40.dp).clip(RoundedCornerShape(18.dp))
        .background(Color(0xFF1E1E2C)).padding(start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$name",
            color = Color.White, fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )
        Box() {
            Row {
                Image(
                    painter = painterResource(
                        R.drawable.img
                    ),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 5.dp).size(35.dp).clip(RoundedCornerShape(10.dp))
                )
                Text(
                    text = "$1,250.00",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }
        }


    }
}
@Composable

fun getbasicdata() {
    var name by remember { mutableStateOf<String?>(null) }
    var chips by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance().currentUser?.email
        if (auth != null) {
            val doc = Firebase.firestore.collection("user").document(auth).get().await()
            name = doc.getString("name")
            chips = doc.getLong("chips")?.toInt()
        }
    }

    if (name != null && chips != null) {
        topinfo(name = name!!, chips = chips!!)
    } else {
        topinfo(name ="Yash Varshney", chips = 100000)
    }
}
@Composable
fun port(from:String){
    val navController= rememberNavController()
    Column(
        modifier = Modifier.padding(top = 0.dp)
            .background(Color(0xFFFAF3E0))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(horizontal = 16.dp).padding(bottom = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFBCA4FF), // Light purple/blue
                            Color(0xFF7C4DFF)  // Deeper purple

                        )
                    )
                ).clickable {
                    if(from.equals("home")){
                        navController.navigate("portfolio")

                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Text(
                    text = "Current",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$3,979.00",
                    color = Color.Green,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "2.6%",
                    color = Color.Red.copy(alpha = 0.6f),
                    fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp)
                )
            }
            Column {
                Text(
                    text = "Invested",
                    modifier = Modifier.fillMaxWidth().align(Alignment.End)
                        .padding(end = 28.dp, top = 15.dp),
                    fontSize = 14.sp,
                    color = Color.White,
                    textAlign = TextAlign.End
                )
                Text(
                    text = "$98300",
                    modifier = Modifier.fillMaxWidth().align(Alignment.End)
                        .padding(end = 28.dp, top = 1.dp),
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.End
                )
                Text(
                    text = "Total",
                    modifier = Modifier.fillMaxWidth().align(Alignment.End)
                        .padding(end = 28.dp, top = 4.dp),
                    fontSize = 14.sp,
                    color = Color.White,
                    textAlign = TextAlign.End
                )
                Text(
                    text = "$98300",
                    modifier = Modifier.fillMaxWidth().align(Alignment.End)
                        .padding(end = 28.dp, top = 0.dp),
                    fontSize = 18.sp,
                    color = Color.Red,
                    textAlign = TextAlign.End
                )
            }
        }}
}



    fun signout(context: Context){
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        val intent=Intent(context,Login::class.java)
        context.startActivity(intent)
}
@Composable
fun setting(navController: NavHostController) {
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .height(60.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    Triple("Home", R.drawable.home) { navController.navigate("home") },
                    Triple("WatchList", R.drawable.home) { navController.navigate("watchlist") },
                    Triple("Portfolio", R.drawable.portfolio1) { navController.navigate("portfolio") },
                    Triple("Setting", R.drawable.setting) { navController.navigate("setting") },
                ).forEach { (label, iconRes, onClick) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onClick)
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = label,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = label, color = Color.Cyan, fontSize = 12.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFAF3E0))
        ) {
            Image(
                painter = painterResource(R.drawable.profile),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            Text(text="Developed By Sachin Varshney", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.Red)
            

            listOf(
                "Name" to "test1",
                "Email" to "test1@gmail.com"
            ).forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "$label:", fontSize = 20.sp, modifier = Modifier.weight(1f))
                    Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            listOf(
                Triple("Change Password", Color(0xFF4CAF50), Color.White) to { /* TODO */ },
                Triple("Sign Out", Color(0xFFFFEB3B), Color.Black) to { signout(context) },
                Triple("Delete Account", Color(0xFFF44336), Color.White) to { /* TODO */ }
            ).forEach { (style, onClick) ->
                val (text, bgColor, textColor) = style
                Box(
                    modifier = Modifier
                        .padding(horizontal = 18.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                        .background(bgColor)
                        .height(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
@Composable
fun  watchlist(navController: NavHostController){
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("home") }) {
        Image(painter = painterResource(R.drawable.home), contentDescription = null)
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "Home", color = Color.Cyan)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("watchlist") }) {
        Image(painter = painterResource(R.drawable.home), contentDescription = null)
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "WatchList", color = Color.Cyan)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("portfolio") }) {
        Image(painter = painterResource(R.drawable.portfolio1), contentDescription = null)
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "Portfolio", color = Color.Cyan)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("setting") }) {
        Image(painter = painterResource(R.drawable.setting), contentDescription = null)
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "Setting", color = Color.Cyan)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(20) {
                Spacer(modifier = Modifier.padding(10.dp))
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .background(Color.DarkGray)
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .fillMaxWidth()
                        .height(80.dp)
                ) {
                    Text(text = "fhdsfjkndjs")

                }

            }
        }

    }
}

@Composable
fun watchpre(navController: NavHostController) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = { Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .height(60.dp)
            ,horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("home") }) {
                Image(painter = painterResource(R.drawable.home), contentDescription = null)
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "Home", color = Color.Cyan)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("watchlist") }) {
                Image(painter = painterResource(R.drawable.home), contentDescription = null)
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "WatchList", color = Color.Cyan)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("portfolio") }) {
                Image(painter = painterResource(R.drawable.portfolio1), contentDescription = null)
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "Portfolio", color = Color.Cyan)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("setting") }) {
                Image(painter = painterResource(R.drawable.setting), contentDescription = null)
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "Setting", color = Color.Cyan)
            }
        } } // 👈 Puts tab() at the bottom
    ) { paddingValues ->
        var data by remember { mutableStateOf<List<Ticket>>(emptyList()) }
        val scope = rememberCoroutineScope()

        // Load data once
        LaunchedEffect(Unit) {
            try {
                if (coinsingelton.coins.isEmpty()) {
                    val response = Retroins.api.getAllTickers()// response: Response<List<Ticket>>
                        coinsingelton.coins = response

                    data = coinsingelton.coins
                } else {
                    data = coinsingelton.coins
                }
            } catch (e: Exception) {
                Log.e("CoinList", "Error: ${e.message}")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize() .background(Color(0xFFFAF3E0))
                .padding(top = 30.dp, bottom = 100.dp)

        ) {
            Text(
                text = "Cryptos",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 19.dp),
                textAlign = TextAlign.Center,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold
            )
            val context= LocalContext.current

            LazyColumn {
                items(data.take(30)) { coin ->
                    Spacer(modifier = Modifier.size(4.dp))
                    Column(
                        modifier = Modifier
                            .padding(vertical = 5.dp, horizontal = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {

                                    val intent=Intent(context,buy::class.java)
//                                    intent.putExtra("name",coin.name)
//                                    intent.putExtra("price",coin.quotes["USD"]?.price.toString())
//                                    intent.putExtra("symbol",coin.symbol)
//                                    intent.putExtra("change",coin.quotes["USD"]?.percent_change_24h.toString() ?: 0.0.toString())
//                                    intent.putExtra("market",coin.quotes["USD"]?.market_cap.toString())
                                    val price = coin.quotes["USD"]?.price ?: 0.0
                                    val change = coin.quotes["USD"]?.percent_change_24h ?: 0.0
                                    val market = coin.quotes["USD"]?.market_cap ?: 0.0

                                    intent.putExtra("name", coin.name)
                                    intent.putExtra("price", String.format("%.3f", price))
                                    intent.putExtra("symbol", coin.symbol)
                                    intent.putExtra("change", String.format("%.3f", change))
                                    intent.putExtra("market", String.format("%.3f", market))
                                    context.startActivity(intent)
                                }
                                .height(90.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFFFF7F50), Color(0xFF00BFFF))
                                    )
                                )
                        ) {
                            // Coin name and symbol
                            Text(
                                text = "${coin.name} (${coin.symbol})",
                                fontSize = 17.sp,
                                color = Color.White,
                                modifier = Modifier.padding(top = 20.dp, start = 25.dp), fontWeight = FontWeight.Bold
                            )

                            // 24h percent change
                            val change2 = coin.quotes["USD"]?.percent_change_24h ?: 0.0
                            val change = coin.quotes["USD"]?.percent_change_24h ?: 0.0
                            val price = coin.quotes["USD"]?.price ?: 0.0
                            Text(
                                text = if(change2>=0) " ↑$change%" else  "↓$change%" ,
                                color = if(change2>=0) Color.Green else Color.Red,
                                modifier = Modifier.padding(top = 50.dp, start = 25.dp)

                            )

                            Box(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 30.dp, end = 20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Current:",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Right
                                    )

                                    Spacer(modifier = Modifier.width(8.dp)) // Optional spacing

                                    Text(
                                        text = "$${"%.2f".format(price)}",
                                        color = if(change2>=0) Color.Green else Color.Red,
                                        fontSize = 24.sp,
                                        textAlign = TextAlign.Right
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

//        LazyColumn(modifier = Modifier.fillMaxWidth()) {
//            items(10){
//                Spacer(modifier = Modifier.size(4.dp))
//               Column(modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp).fillMaxWidth()) {
//                   Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)).height(90.dp).background(brush = Brush.linearGradient(colors = listOf(
//                       Color(0xFFFF7F50), Color(0xFF00BFFF)
//                   )))){
//                       Text(text = "Bitcoin", fontSize = 22.sp, modifier = Modifier.padding(top = 20.dp, start = 25.dp))
//                       Text(text = "2.5%", modifier = Modifier.padding(top = 60.dp, start = 25.dp))
//                       Text( text = "Current :",textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth().align(Alignment.CenterEnd).padding(end=146.dp))
//                       Text( text = "$3744389",textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth().align(Alignment.CenterEnd).padding(end=20.dp), fontSize = 28.sp, color = Color.Green)
//
//                   }
//
//
//               }
//
////                Spacer(modifier = Modifier.size(20.dp))
//
//            }
//        }

    }



@Composable
fun portfolio(navController: NavHostController) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .height(60.dp), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { navController.navigate("home") }) {
                    Image(painter = painterResource(R.drawable.home), contentDescription = null)
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = "Home", color = Color.Cyan)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { navController.navigate("watchlist") }) {
                    Image(painter = painterResource(R.drawable.home), contentDescription = null)
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = "WatchList", color = Color.Cyan)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { navController.navigate("portfolio") }) {
                    Image(
                        painter = painterResource(R.drawable.portfolio1),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = "Portfolio", color = Color.Cyan)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { navController.navigate("setting") }) {
                    Image(painter = painterResource(R.drawable.setting), contentDescription = null)
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = "Setting", color = Color.Cyan)
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFFAF3E0)).padding(top = 30.dp, bottom = 100.dp)

        ) {
            Text(
                text = "Portfolio",
                modifier = Modifier.fillMaxWidth().padding(top = 17.dp),
                textAlign = TextAlign.Center,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(5.dp))
            Box {
                port("port")
            }
            var purchaseList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

            LaunchedEffect(Unit) {
                portdata { maps ->
                    purchaseList = maps
                }
            }
            val context= LocalContext.current
            val scope = rememberCoroutineScope()


            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.fillMaxWidth().clickable {


                }) {
                    items(purchaseList) { purchase ->
                        Spacer(modifier = Modifier.size(12.dp))
                        Column(
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                                .padding(vertical = 0.dp, horizontal = 20.dp).fillMaxWidth().clickable {

                                    val intent=Intent(context,buy::class.java)



                                    context.startActivity(intent)

                                }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                                    .height(90.dp).background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFFF7F50), Color(0xFF00BFFF)
                                            )
                                        )
                                    ).clickable {
//                                        portCryptoData(purchase.get("name").toString(),scope,)
                                    }
                            ) {
                                Text(
                                    text = purchase.get("name").toString(),
                                    fontSize = 22.sp,
                                    modifier = Modifier.padding(top = 20.dp, start = 25.dp)
                                )
                                Text(
                                    text = purchase.get("change").toString(),
                                    modifier = Modifier.padding(top = 60.dp, start = 25.dp)
                                )
                                Text(
                                    text = "Current :",
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterEnd)
                                        .padding(end = 146.dp)
                                )
                                Text(
                                    text = "$3744389",
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterEnd)
                                        .padding(end = 20.dp),
                                    fontSize = 28.sp,
                                    color = Color.Green
                                )

                            }


                        }

//                Spacer(modifier = Modifier.size(20.dp))

                    }
                }

            }
        }
    }
}
fun portCryptoData(name: String,scope: CoroutineScope, onResult: (List<Map<String, Any>>) -> Unit) {
    scope.launch {
        try {
            val resp = Retroins.api.getCoinById("btc-bitcoin")
            val data = listOf(
                mapOf("name" to resp.name),
                mapOf("symbol" to resp.symbol),
                mapOf("rank" to resp.rank),

            )
            onResult(data)
        } catch (e: Exception) {
            e.printStackTrace()
            onResult(emptyList()) // handle error case
        }
    }
}
fun portdata(onResult: (List<Map<String, Any>>) -> Unit) {
    val email = FirebaseAuth.getInstance().currentUser?.email
    val db = FirebaseFirestore.getInstance()

    if (email == null) {
        onResult(emptyList())
        return
    }

    db.collection("data").document(email).collection("purchase")
        .get()
        .addOnSuccessListener { querySnapshot ->
            val map = mutableListOf<Map<String, Any>>()
            for (document in querySnapshot) {
                map.add(document.data)
            }
            onResult(map)
        }
        .addOnFailureListener {
            onResult(emptyList())
        }
}

@Preview(showBackground = true)
@Composable
fun portfoliopre(){

    Govt01Theme {
        val navController = rememberNavController()
        portfolio(navController)
    }
}
@Preview(showBackground = true)
@Composable
fun watchpre(){

    Govt01Theme {
        val navController = rememberNavController()
        watchpre(navController)
    }
}
//@Preview(showBackground = true)
//@Composable
//fun watchlistpre(){
//    Govt01Theme {
//        val navController = rememberNavController()
//        watchlist(navController)
//    }
//}

@Preview(showBackground = true)
@Composable
fun settingpre(){
    Govt01Theme {
        val navController = rememberNavController()
        setting(navController)
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Govt01Theme {
        val navController = rememberNavController()
        home(navController)
    }
}
