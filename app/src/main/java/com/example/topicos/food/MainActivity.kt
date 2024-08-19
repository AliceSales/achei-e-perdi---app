package com.example.topicos.food

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.topicos.food.ui.theme.FoodTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import android.location.Geocoder
import android.Manifest
import android.location.Location

val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_bold, FontWeight.Bold)
)

fun openEmailService(context: Context, email: String, subject: String = "", body: String = "") {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    context.startActivity(Intent.createChooser(intent, "Escolha o app de e-mail"))
}

@Composable
fun CurrentLocationButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, Color(0xFF049EFE), CircleShape),
        colors = ButtonDefaults.buttonColors(Color(0XFFFFFFFF))
    ) {
        Text(
            text = "Pegar minha localização",
            style = TextStyle(
                color = Color(0xFF049EFE),
                fontFamily = InterFontFamily
            )
        )
    }
}

@Composable
fun FoodTheme(content: @Composable () -> Unit) {
    val typography = Typography(
        displayLarge = TextStyle(
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 57.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
    )

    MaterialTheme(
        typography = typography,
        content = content
    )
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
fun generateRandomString(): String {
    val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..128)
        .map { chars.random() }
        .joinToString("")
}
fun generateRandomIdToImage(): String {
    val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..64)
        .map { chars.random() }
        .joinToString("")
}

fun RegisterUser(NomeCompleto: String, Email: String, Celular: String, Cpf: String, Data: String, imagem: String){
    val db = FirebaseFirestore.getInstance()
    val userMap = hashMapOf(
        "Nome" to NomeCompleto,
        "Email" to Email,
        "Celular" to Celular,
        "CPF" to Cpf,
        "DataCriacao" to Data,
        "imagem" to imagem
    )
    db.collection("users").document(Cpf)
        .set(userMap).addOnCompleteListener {
            Log.d("DB", "A inserção de usuario " + Cpf + " foi bem sucedida")
        }.addOnFailureListener{
            Log.d("DB", "A inserção de usuario " + Cpf + " falhou")
        }
}

fun RegisterObjPerdido(NomeObj: String, Tag: String, Descricao: String, Data: String,
                       Mensagem: String, ContatoCelular: String, ContatoEmail:String, Encontrado: Boolean,
                       imagem: String, Local: String, Colection: String){
    val db = FirebaseFirestore.getInstance()
    val id = generateRandomString()
    val ObjMap = hashMapOf(
        "id" to id,
        "NomeObj" to NomeObj,
        "Tag" to Tag,
        "Descricao" to Descricao,
        "Data" to Data,
        "Mensagem" to Mensagem,
        "Celular" to ContatoCelular,
        "Email" to ContatoEmail,
        "Encontrado" to Encontrado,
        "Colection" to Colection,
        "imagem" to imagem,
        "Local" to Local
    )
    db.collection("Perdidos").document(id)
        .set(ObjMap).addOnCompleteListener {
            Log.d("DB", "A inserção de Obj Perdido " + id + " foi bem sucedida")
        }.addOnFailureListener{
            Log.d("DB", "A inserção de Obj Perdido " + id + " falhou")
        }
}

suspend fun GetObj(id: String, collection: String): Map<String, Any?>? {
    Log.d("OBJGET", "O GET começou o id é $collection")
    val db = FirebaseFirestore.getInstance()
    return try {
        val documentSnapshot = db.collection(collection).document(id).get().await()
        if (documentSnapshot.exists()) {
            Log.d("OBJETOGET", "O GET foi bem sucedido")
            hashMapOf(
                "NomeObj" to documentSnapshot.getString("NomeObj"),
                "Tag" to documentSnapshot.getString("Tag"),
                "Descricao" to documentSnapshot.getString("Descricao"),
                "Data" to documentSnapshot.getString("Data"),
                "id" to documentSnapshot.getString("id"),
                "Mensagem" to documentSnapshot.getString("Mensagem"),
                "ContatoCelular" to documentSnapshot.getString("Celular"),
                "ContatoEmail" to documentSnapshot.getString("Email"),
                "Colection" to documentSnapshot.getString("Colection"),
                "Encontrado" to documentSnapshot.getBoolean("Encontrado"),
                "Local" to documentSnapshot.getString("Local"),
                "imagem" to documentSnapshot.getString("imagem")
            )
        } else {
            Log.d("OBJETOGET", "O GET não encontrou o documento")
            null
        }
    } catch (e: Exception) {
        Log.d("OBJETOGET", "O GET falhou: ${e.message}")
        null
    }
}

fun RegisterObjEncontrado(
    NomeObj: String, Tag: String, Descricao: String, Data: String,
    Mensagem: String, ContatoCelular: String, ContatoEmail:String, Encontrado: Boolean,
    imagem: String, Colection: String, Local: String){
    val db = FirebaseFirestore.getInstance()
    val id = generateRandomString()
    val ObjMap = hashMapOf(
        "id" to id,
        "NomeObj" to NomeObj,
        "Tag" to Tag,
        "Descricao" to Descricao,
        "Data" to Data,
        "Mensagem" to Mensagem,
        "Celular" to ContatoCelular,
        "Email" to ContatoEmail,
        "Encontrado" to Encontrado,
        "Local" to Local,
        "Colection" to Colection,
        "imagem" to imagem
    )
    db.collection("Encontrados").document(id)
        .set(ObjMap).addOnCompleteListener {
            Log.d("DB", "A inserção de Obj encontrado " + id + " foi bem sucedida")
        }.addOnFailureListener{
            Log.d("DB", "A inserção de Obj encontrado " + id + " falhou")
        }
}

fun GetObjPerdidos(onResult: (List<Map<String, Any?>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val objts = ArrayList<Map<String, Any?>>()

    db.collection("Perdidos")
        .get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                if (result != null) {
                    for (obj in result) {
                        if (obj.getBoolean("Encontrado") != true) {
                            val objPerdidoMap = hashMapOf(
                                "NomeObj" to obj.getString("NomeObj"),
                                "Tag" to obj.getString("Tag"),
                                "Descricao" to obj.getString("Descricao"),
                                "Data" to obj.getString("Data"),
                                "id" to obj.getString("id"),
                                "Mensagem" to obj.getString("Mensagem"),
                                "ContatoCelular" to obj.getString("Celular"),
                                "ContatoEmail" to obj.getString("Email"),
                                "Colection" to obj.getString("Colection"),
                                "Encontrado" to obj.getBoolean("Encontrado"),
                                "Local" to obj.getString("Local"),
                                "imagem" to obj.getString("imagem")
                            )
                            objts.add(objPerdidoMap)
                        }
                        Log.d("OBJPERDIDO", "nomeobj: ${obj.getString("NomeObj")}")
                    }
                }
                onResult(objts)
            } else {
                Log.d("GET", "O GET em Perdidos falhou")
                onResult(emptyList())
            }
        }
}

private fun GetObjEncontrados(onResult: (List<Map<String, Any?>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val objts = ArrayList<Map<String, Any?>>()

    db.collection("Encontrados")
        .get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("GET", "O GET em Encontrados foi bem sucedido")
                val result = task.result
                if (result != null) {
                    for (obj in result) {
                        if (obj.getBoolean("Encontrado") != true) {
                            val objPerdidoMap = hashMapOf(
                                "NomeObj" to obj.getString("NomeObj"),
                                "Tag" to obj.getString("Tag"),
                                "Descricao" to obj.getString("Descricao"),
                                "Data" to obj.getString("Data"),
                                "id" to obj.getString("id"),
                                "Mensagem" to obj.getString("Mensagem"),
                                "ContatoCelular" to obj.getString("Celular"),
                                "ContatoEmail" to obj.getString("Email"),
                                "Colection" to obj.getString("Colection"),
                                "Encontrado" to obj.getBoolean("Encontrado"),
                                "Local" to obj.getString("Local"),
                                "imagem" to obj.getString("imagem")
                            )
                            objts.add(objPerdidoMap)
                            Log.d("OBJENCONTRADO", "nomeobj: ${obj.getString("NomeObj")}")
                        }
                    }
                }
                onResult(objts)
            } else {
                Log.d("GET", "O GET em Encontrados falhou")
                onResult(emptyList())
            }
        }
}

fun GetUsers(): ArrayList<Any> {
    val db = FirebaseFirestore.getInstance()
    var objts = ArrayList<Any>()
    val Users = db.collection("Users")
        .get().addOnCompleteListener {
        Log.d("GET", "O GET em Users foi bem sucedido")
        }.addOnFailureListener{
            Log.d("GET", "O GET em Users falhou")
        }.addOnSuccessListener { result ->
            for (obj in result) {
                val objPerdidoMap = hashMapOf(
                    "Nome" to obj.getString("NomeCompleto"),
                    "Email" to obj.getString("Email"),
                    "Celular" to obj.getString("Celular"),
                    "CPF" to obj.getString("Cpf"),
                    "DataCriacao" to obj.getString("Data"),
                    "imagem" to obj.getString("imagem")
                )
                objts.add(objPerdidoMap)
                Log.d("OBJPERDIDO", "nomeobj: $obj.getString('NomeObj')")
            }
        }
    return objts
}

fun DeleteObjPerdido(id: String){
    val db = FirebaseFirestore.getInstance()
    db.collection("Perdidos").document(id).delete().addOnCompleteListener {
        Log.d("DELETE", "O DELETE em Perdidos foi bem sucedido")
    }.addOnFailureListener{
        Log.d("DELETE", "O DELETE em Perdidos falhou")
    }
}

fun DeleteObjEncontrado(id: String){
    val db = FirebaseFirestore.getInstance()
    db.collection("Encontrados").document(id).delete().addOnCompleteListener {
            Log.d("DELETE", "O DELETE em Encontrados foi bem sucedido")
    }.addOnFailureListener{
        Log.d("DELETE", "O DELETE em Encontrados falhou")
    }
}

fun UpdateObjStatus(id: String, Encontrado: Boolean, Collection: String){
    val db = FirebaseFirestore.getInstance()
    db.collection(Collection).document(id).update("Encontrado", Encontrado).addOnCompleteListener {
        Log.d("UPDATE", "O UPDATE em Perdidos foi bem sucedido")
    }.addOnFailureListener{
        Log.d("UPDATE", "O UPDATE em Perdidos falhou")
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(navController)
        }
        composable(
            route = "home/{tab}",
            arguments = listOf(navArgument("tab") { type = NavType.IntType })
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getInt("tab") ?: 0
            App(navController, tab = tab)
        }
        composable("details/{item}/{tab}") { backStackEntry ->
            val item = backStackEntry.arguments?.getString("item")
            val tabString = backStackEntry.arguments?.getString("tab")
            val tab = tabString?.toIntOrNull() ?: 0
            Details(item = item ?: "", navController = navController, tab = tab, imgCollectionId = "/images/1000097143")
        }
        composable("cadastro-item/{tab}") { backStackEntry ->
            val tabString = backStackEntry.arguments?.getString("tab")
            val tab = tabString?.toIntOrNull() ?: 0
            CadastroObjetos(navController = navController, tab)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavHostController, tab: Int = 0) {
    val scope = rememberCoroutineScope()
    val images = listOf(
        R.drawable.tip_image_1,
        R.drawable.tip_image_2,
        R.drawable.tip_image_3
    )
    val texts = listOf(
        "Encontre seus pertences",
        "Cadastre itens encontrados",
        "Vem fazer parte dessa corrente do bem!",
    )
    val descriptions = listOf(
        "Dentro do app você pode cadastrar seus pertences perdidos para que outras pessoas possam ajudá-lo a encontrá-lo!",
        "Dentro do app você pode cadastrar objetos encontrados dentro do campus para o dono entrar em contato e você poder devolvê-lo!",
        ""
    )

    val backgrounds = listOf(
        R.drawable.background_1,
        R.drawable.background_2,
        R.drawable.background_3
    )
    val pagerState = rememberPagerState(pageCount = { images.size })

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPage(imageRes = images[page], text = texts[page], description = descriptions[page], backgroundRes = backgrounds[page])
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(70.dp)
            ) {
                if (pagerState.currentPage > 0) {
                    Button(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF049EFE)),
                    ) {
                        Text(text = "Voltar", fontFamily = InterFontFamily)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (pagerState.currentPage < images.size - 1) {
                    Button(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF049EFE)),
                    ) {
                        Text(text = "Próximo", fontFamily = InterFontFamily)
                    }
                } else {
                    Button(
                        onClick = {
                            navController.navigate("home/$tab")
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF049EFE)),
                    ) {
                        Text(text = "Começar", fontFamily = InterFontFamily)
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(
    imageRes: Int,
    text: String,
    description: String,
    backgroundRes: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.width(250.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = text,
                style = TextStyle(
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 3.sp
                ),
                fontFamily = InterFontFamily
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = description,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                ),
                fontFamily = InterFontFamily
            )
        }
    }
}

@Composable
fun App(navController: NavHostController, tab: Int) {
    var selectedTab by remember { mutableStateOf(tab) }
    val tabs = listOf("Encontrados", "Perdidos")

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFFFFFF)),
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                when (selectedTab) {
                    0 -> {
                        SectionHeaderUser(text = "Achei e perdi")
                        Spacer(modifier = Modifier.height(16.dp))
                        InstructionHeader(navController = navController, 0)
                        Spacer(modifier = Modifier.height(16.dp))
                        RecentsSection(navController = navController, 0)
                    }
                    1 -> {
                        SectionHeaderUser(text = "Achei e perdi")
                        Spacer(modifier = Modifier.height(16.dp))
                        InstructionHeader(navController = navController, 1)
                        Spacer(modifier = Modifier.height(16.dp))
                        RecentsSection(navController = navController, 1)
                    }
                }
            }

            TabRow(
                selectedTabIndex = selectedTab,
                contentColor = Color.Black,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color.White)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            val tab = index.toString()
                            navController.navigate("home/$tab")
                        },
                        text = { Text(title) }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomIconButton(
    colorBackground: Color,
    colorIcon: Color,
    navController: NavController,
    tab: Int
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(colorBackground)
    ) {
        IconButton(
            onClick = {
                navController.navigate("home/$tab")
            }
        ) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Back to home Icon",
                tint = colorIcon
            )
        }
    }
}

@Composable
fun Details(item: String, navController: NavHostController, tab: Int, imgCollectionId: String = "/images/1000097143") {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var objeto by remember { mutableStateOf<Map<String, Any?>?>(null) }

    val collection = if (tab == 0) "Encontrados" else "Perdidos"
    LaunchedEffect(item) {
        objeto = GetObj(item, collection)
        Log.d("OBJETO", objeto.toString())
        isLoading = false
    }
    ImagePickerAndUploader(collection = if (tab == 0) "Encontrados" else "Perdidos")

    if (isLoading) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0XFFFFFF))
        ) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .background(Color(0XFF049EFE))
                        .padding(16.dp, 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomIconButton(
                        colorBackground = Color.White,
                        colorIcon = Color(0XFF049EFE),
                        navController = navController,
                        tab = tab
                    )
                }
            }
            item {
                objeto?.let {obj ->
                    FirebaseImage(filePath = imgCollectionId, item = obj["imagem"].toString())
                }
            }
            item {
                objeto?.let { obj ->
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = obj["NomeObj"] as String? ?: "NOME DO ITEM",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                            fontFamily = InterFontFamily
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(3f)) {
                                Text(
                                    text = if (tab == 0) "encontrado no:" else "perdido perto do:",
                                    style = TextStyle(fontWeight = FontWeight.Bold),
                                    fontFamily = InterFontFamily
                                )
                                Text(
                                    text = obj["Local"] as String? ?: "Sem local definido",
                                    modifier = Modifier.fillMaxWidth(),
                                    fontFamily = InterFontFamily
                                )
                            }
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "data:",
                                    style = TextStyle(fontWeight = FontWeight.Bold),
                                    fontFamily = InterFontFamily
                                )
                                Text(
                                    text = obj["Data"] as String? ?: "22/05/2024",
                                    modifier = Modifier.fillMaxWidth(),
                                    fontFamily = InterFontFamily
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(25.dp))
                        Text(
                            text = "descrição:",
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            fontFamily = InterFontFamily
                        )
                        Text(
                            text = obj["Descricao"] as String? ?: "DESCRIÇÃO: ...",
                            fontFamily = InterFontFamily
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(2f)) {
                                Text(
                                    text = "categoria:",
                                    style = TextStyle(fontWeight = FontWeight.Bold),
                                    fontFamily = InterFontFamily
                                )
                                Text(
                                    text = obj["Tag"] as String? ?: "Categoria",
                                    modifier = Modifier.fillMaxWidth(),
                                    fontFamily = InterFontFamily
                                )
                            }
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "tipo anúncio:",
                                    style = TextStyle(fontWeight = FontWeight.Bold),
                                    fontFamily = InterFontFamily
                                )
                                Text(
                                    text = if (tab == 0) "objeto encontrado" else "objeto perdido",
                                    modifier = Modifier.fillMaxWidth(),
                                    fontFamily = InterFontFamily
                                )
                            }
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.bonequinho),
                                contentDescription = "Profile User",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .border(
                                        color = Color(0XFF049EFE),
                                        width = 2.dp,
                                        shape = CircleShape
                                    ),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = "NOME DA PESSOA",
                                Modifier.padding(start = 10.dp),
                                fontFamily = InterFontFamily
                            )
                        }
                        Button(
                            onClick = {
                                openEmailService(
                                    context = context,
                                    email = obj["ContatoEmail"] as String? ?: "example@ufpe.br",
                                    subject = if (tab == 0) "Você encontrou meu objeto, obrigada!" else "Encontrei o seu objeto, como posso te devolver?",
                                    body = "Corpo da mensagem"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF049EFE))
                        ) {
                            Text(
                                text = "Enviar e-mail",
                                style = TextStyle(
                                    color = Color(0XFFFFFFFF),
                                    fontFamily = InterFontFamily
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        objeto?.let { obj ->
                            Button(
                                onClick = {
                                    UpdateObjStatus(obj["id"] as String? ?: "", true, obj["Colection"] as String? ?: "Perdidos")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .border(1.dp, Color(0xFF049EFE), CircleShape),
                                colors = ButtonDefaults.buttonColors(Color(0XFFFFFFFF))
                            ) {
                                Text(
                                    text = "Marcar como entregue",
                                    style = TextStyle(
                                        color = Color(0xFF049EFE),
                                        fontFamily = InterFontFamily
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeaderUser(text: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            fontFamily = InterFontFamily,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.bonequinho),
            contentDescription = "Profile User",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(color = Color(0XFF049EFE), width = 2.dp, shape = CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.Bold
        ),
        fontFamily = InterFontFamily,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
fun InstructionHeader(navController: NavHostController, tab: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(Color(0XFF049EFE))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .height(100.dp),
        ) {
            Text(
                text = if (tab==0) "Encontrei um objeto perdido pela UFPE e desejo entregar para o dono!" else "Perdi um objeto pela UFPE e desejo cadastrá-lo para as pessoas me ajudarem a encontrá-lo!",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color(0XFFFFFFFF)
                ),
                modifier = Modifier.weight(1f),
                fontFamily = InterFontFamily
            )
            Button(
                onClick = {
                    navController.navigate("cadastro-item/$tab")
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(Color(0XFFFFFFFF))
            ) {
                Text(
                    text = "Cadastrar item",
                    style = TextStyle(
                        color = Color(0xFF049EFE),
                        fontFamily = InterFontFamily
                    )
                )
            }
        }
    }
}

@Composable
fun FiltrosSection() {
    Column {
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(text = "Filtros")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(categoryList.size) { index ->
                val category = categoryList[index]
                CategoryCard(category = category.name, imgCard = category.imgRes)
            }
        }
    }
}

val categoryList = listOf(
    Category("Carregadores", R.drawable.carregador),
    Category("Chaves", R.drawable.chaves),
    Category("Documentos", R.drawable.documento),
    Category("Mouse", R.drawable.mouse),
    Category("Documentos", R.drawable.carteira)
)

data class Category(val name: String, val imgRes: Int)

@Composable
fun CategoryCard(imgCard: Int, category: String) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0XFF049EFE))
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imgCard),
                    contentDescription = "Category Image",
                    modifier = Modifier
                        .size(64.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.Black),
                    modifier = Modifier.padding(8.dp),
                    fontFamily = InterFontFamily
                )
            }
        }
    }
}

@Composable
fun RecentsSection(navController: NavHostController, tab: Int) {
    val items = remember { mutableStateOf(emptyList<Map<String, Any?>>()) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (tab == 0) {
            GetObjEncontrados { result ->
                Log.d("OBJENCONTRADO", result.toString())
                items.value = result
                isLoading.value = false
            }
        } else {
            GetObjPerdidos { result ->
                Log.d("OBJPERDIDO", result.toString())
                items.value = result
                isLoading.value = false
            }
        }
    }

    Column {
        SectionHeader(text = if (tab==0) "Objetos encontrados mais recentes" else "Objetos perdidos mais recentes")
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading.value) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(items.value.size) { item ->
                    CardObjeto(
                        items.value[item].toString(),
                        navController,
                        items.value[item].get("imagem").toString(),
                        tab = tab
                    )
                }
            }
        }
    }
}

@Composable
fun CardObjeto(item: String, navController: NavHostController, imgCollectionId: String = "/images/1000097143", tab: Int) {
    Log.d("CARD", item)
    val regex_id = """id=([^,}]+)""".toRegex()
    val regex_nome = """NomeObj=([^,}]+)""".toRegex()
    val regex_descricao = """Descricao=([^,}]+)""".toRegex()
    //val regex_imagem = """imagem=([^,}]+)""".toRegex()
    val id = regex_id.find(item)?.groups?.get(1)?.value ?: "Id não disponível"
    val nomeObj = regex_nome.find(item)?.groups?.get(1)?.value ?: "Nome não disponível"
    val descricao = regex_descricao.find(item)?.groups?.get(1)?.value ?: "Descricao não disponível"
    val imagem = imgCollectionId
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        onClick = {
            navController.navigate("details/$id/$tab")
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            FirebaseImage(filePath = imagem, item = item)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = nomeObj,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontFamily = InterFontFamily
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = descricao,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontFamily = InterFontFamily
            )
        }
    }
}

data class CadastroForm(
    var nomeObj: String = "",
    var descricao: String = "",
    var celular: String = "",
    var categoria: String = "",
    var data: String = "",
    var mensagem: String = "",
    var contatoEmail: String = "",
    var local: String = "",
    var collection: String = ""
)

@Composable
fun CadastroObjetos(navController: NavHostController, tab: Int) {
    val form = remember { mutableStateOf(CadastroForm()) }
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var locationText by remember { mutableStateOf("Localização não disponível") }

    val locationPermissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionGranted.value) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0XFFFFFF))) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .background(Color(0XFF049EFE))
                        .padding(16.dp, 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomIconButton(
                        colorBackground = Color.White,
                        colorIcon = Color(0XFF049EFE),
                        navController = navController,
                        tab = tab
                    )
                }
            }
            item {
                Column(modifier = Modifier.padding(25.dp)) {
                    Text(
                        text = if (tab == 0) "Cadastrar objeto encontrado" else "Cadastrar objeto perdido",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    ImagePickerAndUploader(collection = if (tab == 0) "Encontrados" else "Perdidos")
                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        OutlinedTextField(
                            value = form.value.nomeObj,
                            onValueChange = { form.value = form.value.copy(nomeObj = it) },
                            label = { Text("Digite o nome do objeto") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = form.value.descricao,
                            onValueChange = { form.value = form.value.copy(descricao = it) },
                            label = { Text("Digite a descrição") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = form.value.celular,
                            onValueChange = { form.value = form.value.copy(celular = it) },
                            label = { Text("Celular") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = form.value.categoria,
                            onValueChange = { form.value = form.value.copy(categoria = it) },
                            label = { Text("Selecione a categoria") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = form.value.data,
                            onValueChange = { form.value = form.value.copy(data = it) },
                            label = { Text(if (tab == 0) "Selecione a data que você encontrou o objeto" else "Seleciona a data que você acredita ter perdido o objeto") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = form.value.mensagem,
                            onValueChange = { form.value = form.value.copy(mensagem = it) },
                            label = { Text("Escreva a mensagem") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = form.value.contatoEmail,
                            onValueChange = { form.value = form.value.copy(contatoEmail = it) },
                            label = { Text("Selecione o email que você deseja ser contatado") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (locationPermissionGranted.value) {
                                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                                        location?.let {
                                            val locationString = "Latitude: ${it.latitude}, Longitude: ${it.longitude}"
                                            locationText = locationString
                                            form.value = form.value.copy(local = locationString)
                                        } ?: run {
                                            locationText = "Localização não encontrada"
                                        }
                                    }
                                } else {
                                    locationText = "Permissão não concedida"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .border(1.dp, Color(0xFF049EFE), CircleShape),
                            colors = ButtonDefaults.buttonColors(Color(0XFFFFFFFF))
                        ) {
                            Text(
                                text = "Pegar minha localização",
                                style = TextStyle(
                                    color = Color(0xFF049EFE),
                                    fontFamily = InterFontFamily
                                )
                            )
                        }
                        Text(text = locationText)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Button(
                            onClick = {
                                if (tab == 0) {
                                    RegisterObjEncontrado(
                                        NomeObj = form.value.nomeObj,
                                        Tag = form.value.categoria,
                                        Descricao = form.value.descricao,
                                        Data = form.value.data,
                                        Mensagem = form.value.mensagem,
                                        ContatoCelular = form.value.celular,
                                        ContatoEmail = form.value.contatoEmail,
                                        Encontrado = false,
                                        Local = form.value.local,
                                        imagem = Global.globalVariable,
                                        Colection = form.value.collection
                                    )
                                } else {
                                    RegisterObjPerdido(
                                        NomeObj = form.value.nomeObj,
                                        Tag = form.value.categoria,
                                        Descricao = form.value.descricao,
                                        Data = form.value.data,
                                        Mensagem = form.value.mensagem,
                                        ContatoCelular = form.value.celular,
                                        ContatoEmail = form.value.contatoEmail,
                                        Encontrado = false,
                                        Local = form.value.local,
                                        imagem = Global.globalVariable,
                                        Colection = form.value.collection
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF049EFE))
                        ) {
                            Text(
                                text = if (tab == 0) "Cadastrar objeto encontrado" else "Cadastrar objeto perdido",
                                style = TextStyle(
                                    color = Color(0XFFFFFFFF),
                                    fontFamily = InterFontFamily
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FirebaseImage(filePath: String, item: String) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(filePath) {
        try {
            getImageUrl(filePath,
                onSuccess = { url ->
                    imageUrl = url
                    isLoading = false
                },
                onFailure = { ex ->
                    error = ex.message
                    isLoading = false
                }
            )
        }catch (e: Exception) {
            error = e.message
        }
    }

    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(128.dp)
        ) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(128.dp)
        ) {
            Text(text = "Error: $error")
        }
    } else if (imageUrl != null) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = item,
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )
    }
}

fun getImageUrl(filePath: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
    val storageRef = Firebase.storage.reference.child(filePath)

    storageRef.downloadUrl
        .addOnSuccessListener { uri ->
            onSuccess(uri.toString())
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}
object Global {
    var globalVariable: String = ""
}

@Composable
fun ImagePickerAndUploader(collection: String) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadState by remember { mutableStateOf<UploadState>(UploadState.Idle) }
    var imageIdInFirebase = ""

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            Global.globalVariable = uploadImageToFirebase(collection, it, onSuccess = {
                Log.d("UPLOAD", "Upload realizado com sucesso, $imageIdInFirebase")
                uploadState = UploadState.Success(it)
            }, onFailure = {
                uploadState = UploadState.Failure(it)
            })
        }
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Escolher Imagem")
        }

        Spacer(modifier = Modifier.height(16.dp))

        imageUri?.let {
            Image(painter = rememberAsyncImagePainter(it), contentDescription = null, modifier = Modifier.size(128.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val downloadUrl: String) : UploadState()
    data class Failure(val error: String) : UploadState()
}

fun uploadImageToFirebase(collection: String, uri: Uri, onSuccess: (String) -> Unit, onFailure: (String) -> Unit): String {
    val storageRef = Firebase.storage.reference.child("$collection/${uri.lastPathSegment}")

    val uploadTask = storageRef.putFile(uri)

    uploadTask.addOnSuccessListener {
        storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
            onSuccess(downloadUri.toString())
        }.addOnFailureListener { exception ->
            onFailure(exception.message ?: "Unknown error")
        }
    }.addOnFailureListener { exception ->
        onFailure(exception.message ?: "Unknown error")
    }
    return "$collection/${uri.lastPathSegment}"
}