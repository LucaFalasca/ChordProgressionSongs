package it.lucafalasca.chord

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.lucafalasca.chord.ui.theme.ChordTheme
import java.io.InputStream
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
        val chordProgressions = songs.map { it.chords }.toSet()
        LOG.info("Chord progressions: $chordProgressions")
        super.onCreate(savedInstanceState)
        setContent {
            var selectedChords by remember { mutableStateOf(setOf<String>()) }
            var isVisible by remember { mutableStateOf(false) }

            ChordTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val filteredSongs = songs.filter { song -> selectedChords.any { song.chords.contains(it) } }
                    val displayedSong = if (filteredSongs.isNotEmpty()) filteredSongs[rnd.value % filteredSongs.size] else songs[rnd.value % songs.size]

                    Greeting(
                        title = displayedSong.title,
                        artist = displayedSong.artist,
                        chords = displayedSong.chords,
                        onSongChange = {
                            rnd.value = Random.nextInt(if (filteredSongs.isNotEmpty()) filteredSongs.size else songs.size)
                            LOG.info("Button clicked")
                            isVisible = false
                        },
                        modifier = Modifier,
                        chordProgressions = chordProgressions,
                        onSelectedChordsChange = { selectedChords = it },
                        isVisible = isVisible,
                        onVisibilityChange = { isVisible = it }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    title: String,
    artist: String,
    chords: String,
    onSongChange: () -> Unit,
    modifier: Modifier = Modifier,
    chordProgressions: Set<String>,
    onSelectedChordsChange: (Set<String>) -> Unit,
    isVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Song: $title",
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Artist: $artist",
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            textAlign = TextAlign.Center
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
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
                onClick = { onVisibilityChange(!isVisible) }
            ) {
                Text("Show Chord Progression")
            }
            Button(
                onClick = onSongChange
            ) {
                Text("Change Song")
            }
        }
        CheckboxChordProgressions(chordProgressions, onSelectedChordsChange)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChordTheme {
        Greeting("Titolo", "Artista", "I IV V VI", {}, chordProgressions = setOf("I IV V VI"), onSelectedChordsChange = {}, isVisible = false, onVisibilityChange = {})
    }
}

@Composable
fun CheckboxChordProgressions(chordProgressions: Set<String>, onSelectedChordsChange: (Set<String>) -> Unit, columns: Int = 3) {
    val childCheckedStates = remember { mutableStateListOf(*Array(chordProgressions.size) { true }) }

    val parentState = when {
        childCheckedStates.all { it } -> ToggleableState.On
        childCheckedStates.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Select all")
            TriStateCheckbox(
                state = parentState,
                onClick = {
                    val newState = parentState != ToggleableState.On
                    childCheckedStates.indices.forEach { index ->
                        childCheckedStates[index] = newState
                    }
                    onSelectedChordsChange(chordProgressions.filterIndexed { index, _ -> childCheckedStates[index] }.toSet())
                }
            )
        }

        val chordProgressionList = chordProgressions.toList()
        val rowCount = (chordProgressionList.size + columns - 1) / columns

        Column {
            for (rowIndex in 0 until rowCount) {
                Row {
                    for (columnIndex in 0 until columns) {
                        val itemIndex = rowIndex * columns + columnIndex
                        if (itemIndex < chordProgressionList.size) {
                            val progression = chordProgressionList[itemIndex]
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(progression)
                                Checkbox(
                                    checked = childCheckedStates[itemIndex],
                                    onCheckedChange = { isChecked ->
                                        childCheckedStates[itemIndex] = isChecked
                                        onSelectedChordsChange(chordProgressions.filterIndexed { index, _ -> childCheckedStates[index] }.toSet())
                                    }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    if (childCheckedStates.all { it }) {
        Text("All options selected")
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
