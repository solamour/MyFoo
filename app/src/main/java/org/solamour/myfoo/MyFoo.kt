package org.solamour.myfoo

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.thedeanda.lorem.LoremIpsum
import kotlinx.coroutines.launch
import org.solamour.myfoo.ui.theme.MyFooTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFoo(
    logList: List<LogItem>,
    modifier: Modifier = Modifier,
    onPlay: () -> Unit,
    onClearLog: () -> Unit,
) {
    val activity = LocalActivity.current.takeIf { !LocalInspectionMode.current }
            as? ComponentActivity ?: LocalActivity.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var isSizeChanged by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = logList.size) {
        if (logList.isNotEmpty()) {
            lazyListState.animateScrollToItem(logList.size - 1)
        }
    }

    LaunchedEffect(key1 = isSizeChanged) {
        if (isSizeChanged) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(id = R.string.more),
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.clear_log)) },
                            onClick = {
                                onClearLog()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = context.resources.getString(R.string.log_cleared)
                                    )
                                }
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onPlay,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onSizeChanged { isSizeChanged = true },
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = stringResource(id = R.string.play),
                    tint = MaterialTheme.colorScheme.surface,
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            state = lazyListState,
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(items = logList, key = { it.key }) { listItem ->
                Text(
                    text = listItem.log,
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

//--------------------------------------------------------------------------------------------------
@Preview(
    showSystemUi = true,
    showBackground = true,
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE
)
@Composable
fun MyFooPreview() {
    MyFooTheme(dynamicColor = false) {
        MyFoo(
            logList = List(10) {
                val timestamp = DateTimeFormatter.ofPattern("mm:ss.SSS").format(LocalDateTime.now())
                LogItem(log = "[$timestamp] ${LoremIpsum.getInstance().firstNameFemale}")
            },
            onPlay = {},
            onClearLog = {},
        )
    }
}
