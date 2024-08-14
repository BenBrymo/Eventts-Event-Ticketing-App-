package com.example.eventts.admin.event_management

import EventsManagementViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.eventts.dataClass.Event
import com.example.eventts.navigation.Routes

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsMgmtScreen(navController: NavController, viewModel: EventsManagementViewModel = viewModel()) {

    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<Event?>(null) }

    LaunchedEffect(Unit) {

        //Fetch events on initial composition
        viewModel.fetchEvents()
    }

    val events by viewModel.events.collectAsState()

    // Scaffold to provide a basic material layout structure
    Scaffold(
        // top bar
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Manage Events", color = MaterialTheme.colorScheme.primary)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() } // navigates to Events Management Screen
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate back")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Surface to hold main contents with padding applied
        Surface(Modifier.padding(paddingValues)) {
            EventsMgt(
                navController = navController,
                onDeleteEventClick = { event ->
                    eventToDelete = event
                    showDeleteDialog = true
                },
                events = events
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            eventToDelete?.let { event ->
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(text = "Delete Event") },
                    text = { Text("Are you sure you want to delete the event \"${event.eventName}\"? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteEvent(event)
                                showDeleteDialog = false
                                eventToDelete = null
                            }
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                eventToDelete = null
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun EventsMgt(
    modifier: Modifier = Modifier,
    navController: NavController,
    onDeleteEventClick: (Event) -> Unit,
    events: List<Event>
) {
    Column(modifier.fillMaxSize()) {
        // Search Bar
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
                navController = navController
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (events.isEmpty()) {
            Text(
                text = "No events available",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(events) { event ->
                    EventItem(
                        event = event,
                        onEventClick = { navController.navigate("detailed_event_screen_admin/${event.id}") },
                        onDeleteClick = { onDeleteEventClick(event) },
                        onEditClick = { navController.navigate("edit_event_screen/${event.id}") },
                        onCopyClick = { navController.navigate("copy_event_screen/${event.id}") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Add Event
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            CreateEventButton(
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }


    }
}


@Composable
fun CreateEventButton(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FloatingActionButton(
            onClick = {
               navController.navigate(Routes.CREATE_EVENT_SCREEN) {
                    popUpTo(Routes.EVENTS_MANAGEMENT_SCREEN)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, bottom = 16.dp, top = 16.dp, end = 6.dp),
            contentColor = Color.White,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Event")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    placeholder: String = "Search events",
    navController: NavController
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
    ) {
        Column() {
            TextField(
                value = "",
                onValueChange = {},
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    // backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    //   textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                placeholder = {
                    Text(placeholder, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f))
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            //println(" focus state in focus change property: ,$isSearchBarFocused")
                            //navController.navigate(Routes.ADMIN_SEARCH_SCREEN)
                        }
                    }
            )
        }

    }
}

@Composable
fun EventItem(
    event: Event,
    onEventClick: (Event) -> Unit,
    onDeleteClick: (Event) -> Unit,
    onEditClick: () -> Unit,
    onCopyClick: (Event) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEventClick(event) }, // Makes the whole card clickable
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Image at the top of the card
                Image(
                    painter = rememberAsyncImagePainter(event.imageUri),
                    contentDescription = event.eventName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Kebab menu (three-dot menu) at the top right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    IconButton(onClick = { /* Show menu */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert, // You can use your own icon
                            contentDescription = "More options"
                        )
                    }
                }
            }

            // Event details directly below the image
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = event.eventName,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${event.startDate}, ${event.eventTime}. ${event.eventVenue}",
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.eventStatus,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}



@Preview
@Composable
fun CreateEventButtonPreview() {
    CreateEventButton(navController = rememberNavController())
}

