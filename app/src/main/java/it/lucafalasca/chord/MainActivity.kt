package it.lucafalasca.chord

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.lucafalasca.chord.ui.theme.ChordTheme
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Paths
import java.util.logging.Logger
import kotlin.random.Random
val rnd = mutableStateOf(0)

class MainActivity : ComponentActivity() {
    companion object {
        val LOG = Logger.getLogger(MainActivity::class.java.name)
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        val inputStream = resources.openRawResource(R.raw.chord_progressions2)
        val songs = readCsv(inputStream)
        super.onCreate(savedInstanceState)
        setContent {
            ChordTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(songs[rnd.value].title, songs[rnd.value].artist, songs[rnd.value].chords,
                        onSongChange = {
                            rnd.value = Random.nextInt(songs.size)
                            LOG.info("Button clicked")
                        },
                        modifier = Modifier)

                }
            }
        }
    }
}

@Composable
fun Greeting(title: String, artist: String, chords: String, onSongChange: () -> Unit, modifier: Modifier = Modifier) {
    var isVisible by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Song: $title",
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            textAlign = TextAlign.Center // Allinea il testo al centro
        )
        Text(
            text = "Artist: $artist",
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            textAlign = TextAlign.Center // Allinea il testo al centro
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center // Allinea il testo al centro
        ) {
            Text(
                text = "Chords: ",
                fontSize = 32.sp
            )
            if (isVisible) {
                Text(
                    text = chords,
                    fontSize = 32.sp
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    isVisible = !isVisible
                }
            ) {
                Text("Show Chord Progression")
            }
            Button(
                onClick = onSongChange
            ) {
                Text("Change Song")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChordTheme {
        Greeting("Titolo", "Artista", "I IV V VI", {})
    }
}

fun readCsv(inputStream: InputStream): List<Song> {
    val reader = inputStream.bufferedReader()
    val header = reader.readLine()
    return reader.lineSequence()
        .filter { it.isNotBlank() }
        .map {
            val (title, artist, chord) = it.split(',', ignoreCase = false, limit = 3)
            Song(title, artist, chord)
        }.toList()
}