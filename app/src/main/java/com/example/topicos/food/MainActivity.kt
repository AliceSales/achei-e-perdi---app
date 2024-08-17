package com.example.topicos.food

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.topicos.food.ui.theme.FoodTheme
import com.example.topicos.food.R.color
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.options
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import java.sql.Date

// Definindo a família de fontes Inter
val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_bold, FontWeight.Bold)
)

// Para abrir o serviço de emails
fun openEmailService(context: Context, email: String, subject: String = "", body: String = "") {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    context.startActivity(Intent.createChooser(intent, "Escolha o app de e-mail"))
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
        // Defina outras variações conforme necessário...
    )

    MaterialTheme(
        typography = typography,
        content = content
    )
}

class MainActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            FoodTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController, onSignInClicked = { signIn() })
            }
        }
    }
    fun RegisterUser(NomeCompleto: String, Email: String, Celular: String, Cpf: String, Data: Date){

        val userMap = hashMapOf(
            "Nome" to NomeCompleto,
            "Email" to Email,
            "Celular" to Celular,
            "CPF" to Cpf,
            "DataCriacao" to Data
        )
        db.collection("users").document(Cpf)
            .set(userMap).addOnCompleteListener {
                Log.d("DB", "A inserção de usuario " + Cpf + " foi bem sucedida")
            }.addOnFailureListener{
                Log.d("DB", "A inserção de usuario " + Cpf + " foi mal sucedida")
            }
    }

    fun RegisterObjPerdido(id: String, NomeObj: String, Tag: String, Descricao: String, Data: Date,
                           Mensagem: String, ContatoCelular: String, ContatoEmail:String, Encontrado: Boolean){

        val userMap = hashMapOf(
            "NomeObj" to NomeObj,
            "Tag" to Tag,
            "Descricao" to Descricao,
            "Data" to Data,
            "Mensagem" to Mensagem,
            "Celular" to ContatoCelular,
            "Email" to ContatoEmail,
            "Encontrado" to Encontrado
        )
        db.collection("ObjetosPerdidos").document(id)
            .set(userMap).addOnCompleteListener {
                Log.d("DB", "A inserção de Obj Perdido " + id + " foi bem sucedida")
            }.addOnFailureListener{
                Log.d("DB", "A inserção de Obj Perdido " + id + " foi mal sucedida")
            }
    }
    fun RegisterObjEncontrado(id: String, NomeObj: String, Tag: String, Descricao: String, Data: Date,
                              Mensagem: String, ContatoCelular: String, ContatoEmail:String, Encontrado: Boolean){

        val userMap = hashMapOf(
            "NomeObj" to NomeObj,
            "Tag" to Tag,
            "Descricao" to Descricao,
            "Data" to Data,
            "Mensagem" to Mensagem,
            "Celular" to ContatoCelular,
            "Email" to ContatoEmail,
            "Encontrado" to Encontrado
        )
        db.collection("ObjetosPerdidos").document(id)
            .set(userMap).addOnCompleteListener {
                Log.d("DB", "A inserção de Obj Perdido " + id + " foi bem sucedida")
            }.addOnFailureListener{
                Log.d("DB", "A inserção de Obj Perdido " + id + " foi mal sucedida")
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Se o login for bem-sucedido, você obteremos os detalhes do usuário aqui.
            val email = account?.email
            // Agora, o user pode navegar para a tela "home"
        } catch (e: ApiException) {
            // Lida com falhas no login
        }
    }
}

@Composable
fun NavGraph(navController: NavHostController, onSignInClicked: () -> Unit) {
    NavHost(navController = navController, startDestination = "home") {
        composable("onboarding") {
            OnboardingScreen(navController, onSignInClicked)
        }
        composable("home") {
            App(navController)
        }
        composable("details/{item}") { backStackEntry ->
            val item = backStackEntry.arguments?.getString("item")
            Details(item = item ?: "", navController)
        }
        composable("cadastro-item-encontrado") {
            CadastroObjetosEncontrados(navController = navController)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavHostController, onSignInClicked: () -> Unit) {
    val context = LocalContext.current
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
        "Dentro do app você pode cadastrar e procurar seus pertences perdidos!",
        "Dentro do app você pode cadastrar objetos encontrados dentro do campus!",
        "Dentro do app você pode cadastrar objetos encontrados!"
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
                            onSignInClicked()
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
        // Imagem de background
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        // Conteúdo sobreposto
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
fun App(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFFFFFF)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeaderUser(text = "Achei e perdi")
            Spacer(modifier = Modifier.height(16.dp))
            InstructionHeader(navController = navController)
            Spacer(modifier = Modifier.height(16.dp))
            FiltrosSection()
            Spacer(modifier = Modifier.height(16.dp))
            RecentsSection(navController = navController)
        }
    }
}

@Composable
fun CustomIconButton(
    colorBackground: Color,
    colorIcon: Color,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(colorBackground)
    ) {
        IconButton(
            onClick = {
                navController.navigate("home")
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
fun Details(item: String, navController: NavHostController) {
    val context = LocalContext.current
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
                    navController = navController
                )
            }
        }
        item {
            Image(
                painter = painterResource(id = R.drawable.sushi),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                contentScale = ContentScale.Crop
            )
        }
        item {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "NOME DO ITEM",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                    fontFamily = InterFontFamily
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(2f)) {
                        Text(
                            text = "encontrado no:",
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            fontFamily = InterFontFamily
                        )
                        Text(
                            text = "CAC - centro de artes e comunicação",
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
                            text = "22/05/2024",
                            modifier = Modifier.fillMaxWidth(),
                            fontFamily = InterFontFamily
                        )
                    }
                }
                Spacer(modifier = Modifier.height(25.dp))
                Text(
                    text = "DESCRIÇÃO: FDKNF AMFDKAN JDSFNDAJO DSADOJDAN SNDJSANDAK JNFDKJDSNAK JNFKDNKDSA JKDNFJDFK JDKNFKDS",
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
                            text = "comidinha",
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
                            text = "objeto perdido",
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
                        painter = painterResource(id = R.drawable.delivery),
                        contentDescription = "Profile User",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(color = Color(0XFF049EFE), width = 2.dp, shape = CircleShape),
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
                            email = "example@ufpe.br",
                            subject = "Assunto",
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
            painter = painterResource(id = R.drawable.delivery),
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
fun InstructionHeader(navController: NavHostController) {
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
                text = "Encontrei um objeto perdido pela UFPE e desejo entregar para o dono!",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color(0XFFFFFFFF)
                ),
                modifier = Modifier.weight(1f),
                fontFamily = InterFontFamily
            )
            Button(
                onClick = {navController.navigate("cadastro-item-encontrado")},
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

data class RecentItem(val name: String, val imageRes: Int)

@Composable
fun RecentsSection(navController: NavHostController) {
    Column() {
        SectionHeader(text = "Objetos encontrados mais recentes")
        Spacer(modifier = Modifier.height(16.dp))

        val items = listOf(
            RecentItem("Casual Brown", R.drawable.bebida),
            RecentItem("Casual Brown", R.drawable.bebida),
            RecentItem("Casual Brown", R.drawable.bebida),
            RecentItem("Casual Brown", R.drawable.bebida)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(items.size) { index ->
                val item = categoryList[index]
                CardObjeto(item = item.name, imageRes = item.imgRes, navController = navController)
            }
        }
    }
}

@Composable
fun CardObjeto(item: String, imageRes: Int, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        onClick = {
            navController.navigate("details/$item")
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = item,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontFamily = InterFontFamily
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Lorem ipsum is a placeholder text commonly used",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontFamily = InterFontFamily
            )
        }
    }
}

@Composable
fun CadastroObjetosEncontrados(navController: NavHostController) {
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
                        navController = navController
                    )
                }
            }
            item {
                Column(modifier = Modifier.padding(25.dp)) {
                    Text(text = "Cadastrar objeto encontrado",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ))
                    Spacer(modifier = Modifier.height(20.dp))
                    Image(
                        painter = painterResource(id = R.drawable.default_image),
                        contentDescription = "Cadastro do objeto encontrado",
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        OutlinedTextField(
                            value = "", // Estado do input
                            onValueChange = {}, // Lógica de atualização do estado
                            label = { Text("Digite o nome do objeto") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Digite a descrição") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Digite o local") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Selecione a categoria") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF049EFE))
                        ) {
                            Text(
                                text = "Cadastrar objeto encontrado",
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
