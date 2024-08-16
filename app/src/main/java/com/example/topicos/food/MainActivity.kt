package com.example.topicos.food

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.topicos.food.ui.theme.FoodTheme
import com.example.topicos.food.R.color
import com.google.firebase.Firebase
import com.google.firebase.options
import kotlinx.coroutines.launch

// Definindo a família de fontes Inter
val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_bold, FontWeight.Bold)
)

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase
        setContent {
            FoodTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(navController)
        }
        composable("home") {
            App(navController)
        }
        composable("details/{item}") { backStackEntry ->
            val item = backStackEntry.arguments?.getString("item")
            Details(item = item ?: "")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavHostController) {
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
                            navController.navigate("home")
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
        modifier = Modifier.fillMaxSize(),
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeaderUser(text = "Achei e perdi")
            Spacer(modifier = Modifier.height(16.dp))
            DeliveryProfile()
            Spacer(modifier = Modifier.height(16.dp))
            FiltrosSection()
            Spacer(modifier = Modifier.height(16.dp))
            RecentsSection(navController = navController)
        }
    }
}

@Composable
fun Details(item: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.delivery),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(250.dp)
                .clip(RectangleShape),
            contentScale = ContentScale.Crop
        )
        Column {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(text = "NOME DO ITEM")
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Item encontrado por Ana Luiza")
            }
            Row {
                Text(text = "DESCRIÇÔSAD  FDKNF AMFDKAN JDSFNDAJO DSADOJDAN SNDJSANDAK JNFDKJDSNAK JNFKDNKDSA JKDNFJDFK JDKNFKDS")
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
fun DeliveryProfile() {
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
                onClick = {},
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

// Supondo que você tenha uma lista de categorias com nomes e recursos de imagem
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
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Lorem ipsum is a placeholder text commonly used",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
