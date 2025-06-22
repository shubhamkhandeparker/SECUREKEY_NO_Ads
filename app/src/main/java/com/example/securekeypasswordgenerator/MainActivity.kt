package com.example.securekeypasswordgenerator

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.core.graphics.alpha
import kotlin.random.Random
import com.example.securekeypasswordgenerator.ui.theme.SECUREKEYPasswordGeneratorTheme

private val DarkGreen = Color(0xFF001100)   //Bg. Dark Green
private val BrightGreen = Color(0xFF00FF00)     //text. light green
private val MediumGreen = Color(0xFF008800)     //medium green

@Composable
fun SecureKeyTheme(content: @Composable () -> Unit) {

    val colors = darkColorScheme(
        background = DarkGreen,
        surface = Color(0xFF002200),
        primary = BrightGreen,
        onBackground = BrightGreen,
        onSurface = BrightGreen
    )
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            passwordGeneratorApp()
            TestCanvasScreeb()
        }

    }
}

@Composable
fun passwordGeneratorApp() {
    SecureKeyTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()

        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {

                var passwordLength by remember { mutableStateOf(12f) }
                var generatedpassword by remember { mutableStateOf("Kx9#mP2vL8QqR5") }
                var includeUpperCase by remember { mutableStateOf(true) }
                var includeLowerCase by remember { mutableStateOf(true) }
                var includeNumbers by remember { mutableStateOf(true) }
                var includeSymbols by remember { mutableStateOf(false) }
                var displayText by remember { mutableStateOf("") }
                var isAnimating by remember { mutableStateOf(false) }
                var showCursor by remember { mutableStateOf(false) }

                val loadingMessages = listOf(
                    "INITIALIZING CRYPTO ENGINE",
                    "SCANNING ENTROPY SOURCES",
                    "GENERATING SECURE KEY",
                    "PROCESSING..."
                )

                suspend fun animatePasswordGeneration(
                    messages: List<String>,
                    finalPassword: String,
                    onUpdateDisplay: (String) -> Unit,
                    onUpdateCursor: (Boolean) -> Unit
                ) {
                    //Animate Loading Messages

                    for (message in messages) {
                        for (i in 1..message.length) {
                            onUpdateDisplay(message.substring(0, i))
                            delay(50) //speed of typing of each character
                        }
                        delay(800)
                    }

                    //clear and start password typing
                    onUpdateDisplay("")
                    delay(300)

                    //Type the password

                    for (i in 1..finalPassword.length) {
                        onUpdateDisplay(finalPassword.substring(0, i))
                        delay(100) //speed of password typing
                    }

                    //Stop cursor blinking
                    onUpdateCursor(false)

                }

                //LaunchedEffect to handle animation

                LaunchedEffect(isAnimating) {
                    if (isAnimating) {
                        animatePasswordGeneration(
                            messages = loadingMessages,
                            finalPassword = generatedpassword,
                            onUpdateDisplay = { displayText = it },
                            onUpdateCursor = { showCursor = it }
                        )

                        //Animation finished
                        isAnimating = false
                        displayText = ""
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "SECUREKEY",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Secure Password Generator",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)

                    )
                }

                Spacer(modifier = Modifier.height(20.dp))


                //Password display Box

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .border(1.dp, BrightGreen),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF002200))
                ) {
                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {
                        val context = LocalContext.current
                        Text(
                            text = if (isAnimating) "${displayText}${if (showCursor) "█" else ""}" else generatedpassword,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Button(
                            onClick = {
                                //Copy to clipboard functionality

                                val clipboardManager =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                                val clip =
                                    ClipData.newPlainText("Generated Password", generatedpassword)

                                clipboardManager.setPrimaryClip(clip)

                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text(
                                "COPY",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                //password Length slider section


                Text(
                    text = "PASSWORD LENGTH : ${passwordLength.toInt()}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))


                Slider(
                    value = passwordLength,
                    onValueChange = { passwordLength = it },
                    valueRange = 4f..50f,
                    colors = SliderDefaults.colors(
                        thumbColor = BrightGreen,
                        activeTickColor = BrightGreen,
                        inactiveTickColor = MediumGreen
                    )
                )

                //Adding Space after Slider

                Spacer(modifier = Modifier.height(15.dp))

                //Checkbox options


                Text(
                    text = "PASSWORD OPTIONS",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary

                )

                Spacer(modifier = Modifier.height(10.dp))

                // First Row UpperCase & LoserCase  CheckBox
                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)

                        ) {
                            Checkbox(
                                checked = includeUpperCase,
                                onCheckedChange = { includeUpperCase = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BrightGreen,
                                    uncheckedColor = MediumGreen
                                )
                            )
                            Text(
                                text = " UpperCase ",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {

                            Checkbox(
                                checked = includeLowerCase,
                                onCheckedChange = { includeLowerCase = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BrightGreen,
                                    uncheckedColor = MediumGreen
                                )
                            )
                            Text(
                                text = "Lowercase",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }

                    //Second Row Numbers & Symbols

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = includeNumbers,
                                onCheckedChange = { includeNumbers = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BrightGreen,
                                    uncheckedColor = MediumGreen
                                )
                            )
                            Text(
                                text = "Numbers",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {

                            Checkbox(
                                checked = includeSymbols,
                                onCheckedChange = { includeSymbols = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BrightGreen,
                                    uncheckedColor = MediumGreen
                                )
                            )
                            Text(
                                text = "Symbols",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                //Add space before button

                Spacer(modifier = Modifier.height(16.dp))

                //Execute Button

                Button(
                    onClick = {


                        if (!isAnimating) {
                            //Generate Character sets based on checkboxes

                            var charset = ""
                            if (includeUpperCase) charset += "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                            if (includeLowerCase) charset += "abcdefghijklmnopqrstuvwxyz"
                            if (includeNumbers) charset += "0123456789"
                            if (includeSymbols) charset += "!@#\$%^&*()_+-=[]{}|;:,.<>?"

                            //Generate random Password
                            if (charset.isNotEmpty()) {
                                val newPassword = (1..passwordLength.toInt())
                                    .map { charset.random() }
                                    .joinToString("")

                                //Start animation
                                isAnimating = true
                                showCursor = true

                                //store the new password for later

                                generatedpassword = newPassword


                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightGreen,
                        contentColor = DarkGreen
                    )
                ) {
                    Text(
                        text = "> EXECUTE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }


            }

        }


    }


}

@Composable
fun TestCanvasScreeb() {
    SecureKeyTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            //Our test Canvas!

            SimpleCanvasExample()
        }
    }
}

@Composable
fun SimpleCanvasExample() {

    //Data class for each particle

    data class Particle(
        var x: Float,
        var y: Float,
        var alpha: Float,
        var speed: Float,
        var size: Float,
        var velocityX: Float,
        var life: Float
    )

    // List of particles
    var particles by remember {
        mutableStateOf(
            List(25) {  //create 20 particles
                Particle(
                    x = kotlin.random.Random.nextInt(0, 201).toFloat(),
                    y = 200f + kotlin.random.Random.nextInt(0, 51),
                    alpha = 1f,
                    speed = kotlin.random.Random.nextFloat() * 2f + 1f,
                    size = kotlin.random.Random.nextFloat() * 6f + 2f,
                    velocityX = kotlin.random.Random.nextFloat() * 4f - 2f,
                    life = 1f


                )

            }
        )
    }


    //The movement Loop

    LaunchedEffect(Unit) {
        while (true) {
            particles = particles.map { particle ->
                val newY = particle.y - particle.speed * 0.5f
                val newAlpha = if (newY > 0) newY / 200f else 0f

                //Reset particle if its gone

                if (newY < -50f) {
                    Particle(
                        x = kotlin.random.Random.nextInt(0, 201).toFloat(),
                        y = 200f + kotlin.random.Random.nextInt(0, 51),
                        alpha = 1f,
                        speed = kotlin.random.Random.nextFloat() * 2f + 1f,
                        size = kotlin.random.Random.nextFloat() * 6f + 2f,
                        velocityX = kotlin.random.Random.nextFloat() * 4f - 2f,
                        life = 1f
                    )
                } else {

                    Particle(
                        x = particle.x + particle.velocityX,
                        y = newY,
                        alpha = newAlpha,
                        speed = particle.speed,
                        size = particle.size,
                        velocityX = particle.velocityX,
                        life = particle.life

                    )

                }
            }
            delay(16)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { particle ->
            if (particle.alpha > 0.01f) {
                //Draw a green circle at position (100,100)
                drawCircle(
                    color = Color.Green.copy(alpha = particle.alpha * 0.3f),
                    radius = particle.size * 3f, //3x bigger than glow
                    center = Offset(
                        (particle.x / 201f) * canvasWidth,
                        (particle.y / 251f) * canvasHeight
                    )
                )
                drawCircle(
                    color = Color.Green.copy(alpha = particle.alpha * 0.6f),
                    radius = particle.size * 1.5f,
                    center = Offset(
                        (particle.x / 201f) * canvasWidth,
                        (particle.y / 251f) * canvasHeight
                    )
                )
                drawCircle(
                    color = Color.Green.copy(alpha = particle.alpha),
                    radius = particle.size * 0.5f,
                    center = Offset(
                        (particle.x / 201f) * canvasWidth,
                        (particle.y / 251f) * canvasHeight
                    )
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun previewApp() {
    passwordGeneratorApp()
}
